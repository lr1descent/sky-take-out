package com.sky.service;

import com.sky.entity.Setmeal;
import com.sky.vo.DishItemVO;

import java.util.List;

public interface SetmealService {
    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    List<Setmeal> selectByCategoryId(String categoryId);

    /**
     * 根据套餐id查询包含的菜品
     * @param id
     * @return
     */
    List<DishItemVO> selectBySetmealId(String id);
}
