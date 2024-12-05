package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺相关接口")
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String SHOP_STATUS = "SHOP_STATUS";

    /**
     * 设置店铺营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result setShopStatus(@PathVariable Integer status) {
        log.info("设置店铺营业状态为：{}",
                status == StatusConstant.ENABLE ? "营业中" : "已打烊");
        // 使用Redis设置店铺状态为status
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(SHOP_STATUS, status);

        return Result.success();
    }

    /**
     * 获取店铺营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getShopStatus() {
        // 使用redisTemplate获取redis中的店铺营业状态
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Integer shop_status = (Integer) valueOperations.get(SHOP_STATUS);
        System.out.println("shop_status is " + shop_status);

        log.info("获取当前店铺营业状态为：{}",
                (Objects.equals(shop_status, StatusConstant.ENABLE) ? "营业中" : "已打烊"));

        return Result.success(shop_status);
    }
}
