package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 修改分类
     * @param category
     * @return
     */
    @AutoFill(OperationType.UPDATE)
    int update(Category category);

    /**
     * 新增分类
     * @param category
     * @return
     */
    @AutoFill(OperationType.INSERT)
    int insert(Category category);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> selectByType(Integer type);

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    int deleteById(Long id);
}
