package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据ids查找套餐
     * @param ids
     * @return
     */
    List<SetmealDish> selectByIds(List<Long> ids);
}
