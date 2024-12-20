package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.constant.MessageConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DishesNotNullException;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController("adminCategoryController")
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分类分页查询：{}", categoryPageQueryDTO);

        PageResult page = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(page);
    }

    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类：{}", categoryDTO);
        int ok = categoryService.update(categoryDTO);
        if (ok == 1) {
            return Result.success();
        } else {
            return Result.error(MessageConstant.UPDATE_CATEGORY_FAILURE);
        }
    }

    /**
     * 启用/禁用分类
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用/禁用分类")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("启用/禁用分类：{}", id);

        int ok = categoryService.startOrStop(status, id);
        if (ok == 1) {
            return Result.success();
        } else {
            return Result.error(MessageConstant.UPDATE_STATUS_FAILURE);
        }
    }

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result insertCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);

        int ok = categoryService.insertCategory(categoryDTO);
        if (ok == 1) {
            return Result.success();
        } else {
            return Result.error(MessageConstant.SAVE_CATEGORY_FAILURE);
        }
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> selectByType(Integer type) {
        log.info("根据类型查询分类：{}", type);

        List<Category> categories = categoryService.selectByType(type);

        return Result.success(categories);
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("根据id删除分类")
    public Result deleteById(Long id) throws DishesNotNullException {
        log.info("根据id删除分类：{}", id);
        int ok = categoryService.deleteById(id);
        if (ok == 1) {
            return Result.success();
        } else {
            return Result.error(MessageConstant.DELETE_CATEGORY_ERROR);
        }
    }
}
