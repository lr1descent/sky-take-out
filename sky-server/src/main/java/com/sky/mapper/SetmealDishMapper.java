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
     * @param id
     * @return
     */
    List<DishItemVO> selectBySetmealId(String setmealId);
}
