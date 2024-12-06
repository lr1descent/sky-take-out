package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userCategoryController")
@Slf4j
@RequestMapping("/user/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 查询菜品分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("条件查询")
    public Result<List<Category>> query(Integer type) {
        log.info("用户端查询菜品分类：{}", type);

        List<Category> categories = categoryService.selectByType(type);
        return Result.success(categories);
    }
}
