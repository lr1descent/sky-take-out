package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}