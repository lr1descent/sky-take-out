package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DishesNotNullException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STAlgType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        // 获取DTO中的页数和页大小，设置PageHelper中的参数
        int page = categoryPageQueryDTO.getPage();
        int pageSize = categoryPageQueryDTO.getPageSize();
        PageHelper.startPage(page, pageSize);

        // 执行查询
        Page<Category> pageQuery = categoryMapper.pageQuery(categoryPageQueryDTO);

        // 将page包装成PageResult类
        long total = pageQuery.getTotal();
        List<Category> result = pageQuery.getResult();
        return new PageResult(total, result);
    }

    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @Override
    public int update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        // 设置分类的修改时间和修改者的id
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());

        return categoryMapper.update(category);
    }

    /**
     * 启用/禁用分类
     * @param id
     * @return
     */
    @Override
    public int startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();

        return categoryMapper.update(category);
    }

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @Override
    public int insertCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        // 设置创建时间和创建者id
        category.setCreateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());

        // 设置更新时间和更新者id
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());

        // 设置分类状态，默认为启动
        category.setStatus(StatusConstant.ENABLE);

        // 新增分类
        return categoryMapper.insert(category);
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @Override
    public List<Category> selectByType(Integer type) {
        return categoryMapper.selectByType(type);
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @Override
    public int deleteById(Long id){
        // 查看dish中category_id = id的数量，如果不为0，说明dish中不含有该id对应的分类的菜品
        // 可以删除
        int numberOfDishes = dishMapper.selectCountByCategoryId(id);
        if (numberOfDishes != 0) {
            throw new DishesNotNullException(MessageConstant.DISHES_NOT_NULL);
        }
        return categoryMapper.deleteById(id);
    }
}
