package com.sky.service.impl;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

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
}
