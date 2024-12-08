package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void updateSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 修改套餐
        setmealMapper.updateSetmeal(setmeal);

        // 修改套餐关联的菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        // SetmealDTO中不包含套餐的id，所以在插入该套餐关联的菜品的时候，需要为每一个菜品的setmealId赋值
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
        }

        // 1. 先将该套餐关联的菜品全部删除
        setmealDishMapper.deleteBySetmealId(setmeal.getId());

        // 2. 再添加套餐关联的菜品
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    public void insertSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmealMapper.insertSetmeal(setmeal);
    }

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 设置分页查询相关配置
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        // 设置查询的相关参数
        String name = setmealPageQueryDTO.getName();
        Integer categoryId = setmealPageQueryDTO.getCategoryId();
        Integer status = setmealPageQueryDTO.getStatus();

        Page<Setmeal> page = setmealMapper.pageQuery(name, categoryId, status);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 套餐起售/停售
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();

        setmealMapper.updateSetmeal(setmeal);
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    public void deleteBatch(List<String> ids) {
        setmealMapper.delete(ids);
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO selectById(Long id) {
        Setmeal setmeal = setmealMapper.selectById(id);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);

        List<SetmealDish> setmealDishes = setmealDishMapper.selectSetmealDishBySetmealId(setmeal.getId());
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 条件查询套餐
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        return setmealMapper.list(setmeal);
    }
}
