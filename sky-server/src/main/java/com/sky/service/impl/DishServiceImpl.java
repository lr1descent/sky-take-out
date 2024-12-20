package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @Override
    public int addDish(DishDTO dishDTO) {
        // 新建一个Dish对象，将Dish对象传递给mapper接口的insert方法
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 调用mapper接口的insertDish方法
        int ok = dishMapper.insertDish(dish);

        // 获取插入的Dish对应的id
        Long id = dish.getId();

        // 除了要新增Dish之外，还要判断是否含有DishFlavor
        // 从dishDTO中可以获取DishFlavor对象
        List<DishFlavor> flavors = dishDTO.getFlavors();

        // 如果flavors不为空，那么要新增flavor对象
        // 批量添加flavor对象
        if (flavors != null && flavors.size() > 0) {
            // 循环遍历flavors，设置每一种口味的dishId为当前菜品的dishId
            // 但是dishId是在插入Dish的时候自动生成（自增）的，如何获取id呢？
            // 可以在mapper.xml文件中指定生成主键，且主键设置为id
            // 这样一来就可以在插入Dish的时候获取自动生成的id
            for (DishFlavor dishFlavor : flavors) {
                dishFlavor.setDishId(id);
            }

            int ok2 = dishFlavorMapper.insertBatch(flavors);
        }

        return ok;
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        // 设置分页查询的页数和页大小
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        // 开始进行分页查询
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        // 将分页查询结果包装成PageResult返回
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     * 涉及多张表，所以设置该方法为事务方法
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public int deleteByIds(List<Long> ids) {
        // 删除的菜品中如果在售，无法删除
        for (Long id : ids) {
            // 根据菜品id查找对应的菜品，看是否是售出状态，如果是，那么抛出“无法删除在售菜品”
            Dish dish = dishMapper.selectById(id);

            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 如果套餐中有关联的菜品，无法删除
        List<SetmealDish> setmealDishes = setmealDishMapper.selectByIds(ids);

        if (setmealDishes != null && setmealDishes.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }

        // 批量删除菜品
        int dishesCount = dishMapper.deleteByIds(ids);

        // 批量删除菜品关联的口味数据
        int flavorsCount = dishFlavorMapper.deleteByIds(ids);

        // 返回删除数据的总条数
        return dishesCount + flavorsCount;
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @Override
    @Transactional
    public int updateDish(DishDTO dishDTO) {
        // 修改菜品信息
        // 由于dishDTO中存在口味数据，不妨新建一个Dish对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 更新菜品
        int updateDishOk = dishMapper.update(dish);

        // 修改菜品对象的口味数据
        // 1. 删除该菜品对应的所有口味数据
        int deleteFlavorsOk = dishFlavorMapper.deleteByDishId(dish.getId());

        // 2. 添加该菜品新增的口味数据
        // 由于flavors中不含有菜品id，所以应该遍历flavors，给每一个flavor设置菜品id
        List<DishFlavor> flavors = dishDTO.getFlavors();

        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dish.getId());
        }
        int insertFlavorsOk = dishFlavorMapper.insertBatch(flavors);

        return (updateDishOk & deleteFlavorsOk & insertFlavorsOk);
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO selectById(Long id) {
        // 根据id查询菜品
        Dish dish = dishMapper.selectById(id);
        DishVO dishVO = new DishVO();

        BeanUtils.copyProperties(dish, dishVO);

        // 根据菜品id查询对应的口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectByDishId(id);

        // 包装成DishVO返回
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> selectByCategoryId(Long categoryId) {
        return dishMapper.selectByCategoryId(categoryId);
    }

    /**
     * 商品起售/停售
     * @param status
     * @param id
     * @return
     */
    @Override
    public int startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder().id(id).status(status).build();
        int ok = dishMapper.update(dish);
        return 0;
    }

    @Override
    public List<DishVO> selectByCategoryIdWithFlavors(Long categoryId) {
        List<Dish> dishes = selectByCategoryId(categoryId);
        List<DishVO> dishVOS = new ArrayList<>();

        for (Dish dish : dishes) {
            List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(dish.getId());
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);
            dishVO.setFlavors(flavors);

            dishVOS.add(dishVO);
        }

        return dishVOS;
    }
}
