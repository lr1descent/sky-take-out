package com.sky.service.impl;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    @Override
    public List<Setmeal> selectByCategoryId(String categoryId) {
        return setmealMapper.selectByCategoryId(categoryId);
    }

    /**
     * 根据套餐的id查询包含的菜品
     * @param setmealId
     * @return
     */
    @Override
    public List<DishItemVO> selectBySetmealId(String setmealId) {
        return setmealDishMapper.selectBySetmealId(setmealId);
    }
}
