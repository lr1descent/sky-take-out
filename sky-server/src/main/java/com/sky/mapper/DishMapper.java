package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper {
    /**
     * 根据分类id查询菜品数量
     * @param id
     * @return
     */
    int selectCountByCategoryId(Long id);
}
