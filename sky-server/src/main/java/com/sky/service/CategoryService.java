package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService{
    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    int update(CategoryDTO categoryDTO);

    /**
     * 启用/禁用分类
     * @param id
     * @return
     */
    int startOrStop(Integer status, Long id);

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    int insertCategory(CategoryDTO categoryDTO);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> selectByType(Integer type);
}
