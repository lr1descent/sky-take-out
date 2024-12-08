package com.sky.service.impl;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        ShoppingCart entry = shoppingCartMapper.select(shoppingCart);

        // 如果该条目已经存在于购物车，执行update操作，变更条目数量
        if (entry != null) {
            entry.setNumber(entry.getNumber() + 1);
            shoppingCartMapper.update(entry);
            return;
        }

        // 如果该条目不存在于购物车，执行insert操作
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();

        // 判断该条目是菜品还是套餐
        if (dishId != null) {
            // 如果该条目是菜品
            Dish dish = new Dish();
            dish = dishMapper.selectById(dishId);

            // 将菜品的相关属性赋值给shoppingCart
            shoppingCart.setAmount(dish.getPrice());
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getName());
        } else {
            // 如果该条目是套餐
            Setmeal setmeal = new Setmeal();
            setmeal = setmealMapper.selectById(setmealId);

            // 将套餐的相关属性赋值给shoppingCart
            shoppingCart.setAmount(setmeal.getPrice());
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getName());
        }

        // 设置shoppingCart的相关属性
        shoppingCart.setNumber(1);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCart.setCreateTime(LocalDateTime.now());

        // 将shoppingCart插入至购物车中
        shoppingCartMapper.insert(shoppingCart);
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        return shoppingCartMapper.list(BaseContext.getCurrentId());
    }

    /**
     * 清空购物车
     */
    @Override
    public void delete() {
        Long currentId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(currentId);
    }

    /**
     * 删除购物车中的一个商品
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        // 求该商品的数量
        ShoppingCart entry = shoppingCartMapper.select(shoppingCart);

        // 如果不为1的话，那么update该商品的数量
        if (entry.getNumber() > 1) {
            entry.setNumber(entry.getNumber() - 1);
            shoppingCartMapper.update(entry);
        } else {
            // 如果商品的数量为1，那么删除这个条目
            shoppingCartMapper.deleteById(entry.getId());
        }
    }
}
