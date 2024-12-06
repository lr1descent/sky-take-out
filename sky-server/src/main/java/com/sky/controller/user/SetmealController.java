package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    public Result<List<Setmeal>> selectByCategoryId(String categoryId) {
        log.info("根据分类id查询套餐：{}", categoryId);
        List<Setmeal> setmeals = setmealService.selectByCategoryId(categoryId);

        return Result.success(setmeals);
    }

    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询包含的菜品")
    public Result<List<DishItemVO>> selectBySetmealId(@PathVariable String id) {
        log.info("根据套餐id查询包含的菜品：{}", id);
        List<DishItemVO> dishItemVOS = setmealService.selectBySetmealId(id);
        return Result.success(dishItemVOS);
    }

}
