package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish selectById(Long id);

    /**
     * 根据ids批量删除菜品
     * @param ids
     * @return
     */
    int deleteByIds(List<Long> ids);

    /**
     * 修改菜品
     * @param dish
     * @return
     */
    @AutoFill(OperationType.UPDATE)
    int update(Dish dish);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> selectByCategoryId(Long categoryId);


    List<Dish> selectByCategoryIdWithFlavors(Long categoryId);

    /**
     * 根据map查询菜品的数量
     * @param map
     * @return
     */
    @Select("select count(*) from dish where status = #{status}")
    Integer selectByMap(Map map);
}
