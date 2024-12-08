package com.sky.mapper;

import com.sky.entity.SetmealDish;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据ids查找套餐
     * @param ids
     * @return
     */
    List<SetmealDish> selectByIds(List<Long> ids);

    /**
     * 根据套餐id查询包含的菜品
     * @param setmealId
     * @return
     */
    List<DishItemVO> selectBySetmealId(String setmealId);


    /**
     * 根据套餐id查询关联的餐品，返回为SetmealDish
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> selectSetmealDishBySetmealId(Long setmealId);

    /**
     * 根据套餐id删除所有套餐关联的菜品
     * @param setmealId
     */
    void deleteBySetmealId(Long setmealId);

    /**
     * 批量插入套餐关联的菜品
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);
}
