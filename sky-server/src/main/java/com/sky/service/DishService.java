package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    int addDish(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    int deleteByIds(List<Long> ids);

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    int updateDish(DishDTO dishDTO);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    DishVO selectById(Long id);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> selectByCategoryId(Long categoryId);

    /**
     * 菜品起售/停售
     * @param status
     * @param id
     * @return
     */
    int startOrStop(Integer status, Long id);

    /**
     * 根据分类id查询菜品，并且查询该菜品的口味数据
     * @param categoryId
     * @return
     */
    List<DishVO> selectByCategoryIdWithFlavors(Long categoryId);
}
