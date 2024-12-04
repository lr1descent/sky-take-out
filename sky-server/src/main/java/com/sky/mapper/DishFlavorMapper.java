package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入DishFlavor
     * @param flavors
     * @return
     */
    int insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id批量删除菜品口味数据
     * @param ids
     * @return
     */
    int deleteByIds(List<Long> ids);

    /**
     * 根据菜品id删除对应口味数据
     * @param dishId
     * @return
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    int deleteByDishId(Long dishId);

    /**
     * 根据菜品id查询对应口味数据
     * @param dishId
     * @return
     */
    List<DishFlavor> selectByDishId(Long dishId);
}
