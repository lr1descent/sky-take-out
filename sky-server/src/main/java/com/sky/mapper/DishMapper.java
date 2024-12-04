package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper {
    /**
     * 根据分类id查询菜品数量
     * @param id
     * @return
     */
    int selectCountByCategoryId(Long id);

    /**
     * 新增菜品
     * @param dish
     * @return
     */
    @AutoFill(OperationType.INSERT)
    int insertDish(Dish dish);
}
