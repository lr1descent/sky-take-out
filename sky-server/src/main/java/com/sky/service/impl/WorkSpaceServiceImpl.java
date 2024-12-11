package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.entity.Setmeal;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 查询今日运营数据
     * @return
     */
    @Override
    public BusinessDataVO businessData() {
        LocalDate today = LocalDate.now();

        // 查询beginTime至endTime区间内的运营数据
        LocalDateTime beginTime = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(today, LocalTime.MAX);

        BusinessDataVO businessDataVO = new BusinessDataVO();
        Map map = new HashMap();
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        map.put("status", Orders.COMPLETED);

        // 查询有效订单数
        Integer validOrdersCount = orderMapper.selectByMap(map);
        System.out.println("validOrdersCount is " + validOrdersCount);

        // 查询总订单数
        map.put("status", null);
        Integer totalOrdersCount = orderMapper.selectByMap(map);
        System.out.println("totalOrdersCount is " + totalOrdersCount);


        // 查询营业额
        Double turnover = orderMapper.turnOver(map);
        // 如果没有营业额，赋值为0
        turnover = turnover == null ? 0.0 : turnover;

        // 订单完成率
        Double completedOrderRate = 0.0;
        if (totalOrdersCount != 0) completedOrderRate = validOrdersCount.doubleValue() / totalOrdersCount;

        // 平局客单价
        Double unitPrice = validOrdersCount == 0 ? 0.0 : turnover / validOrdersCount;

        // 新增用户数
        Integer newUsers = userMapper.selectByTime(beginTime, endTime);

        // 将查询结果包装成BusinessDataVO返回
        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrdersCount)
                .orderCompletionRate(completedOrderRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    /**
     * 查询菜品总览
     * @return
     */
    @Override
    public DishOverViewVO overviewDishes() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        // 查询正在起售的菜品
        Integer soldDishes = dishMapper.selectByMap(map);

        // 查询正在停售的菜品
        map.put("status", StatusConstant.DISABLE);
        Integer discontinuedDishes = dishMapper.selectByMap(map);

        return DishOverViewVO.builder()
                .discontinued(discontinuedDishes)
                .sold(soldDishes)
                .build();
    }

    /**
     * 查询套餐总览
     * @return
     */
    @Override
    public SetmealOverViewVO overviewSetmeals() {
        Map map = new HashMap();

        // 查询起售状态的菜品
        map.put("status", StatusConstant.ENABLE);
        Integer soldSetmeals = setmealMapper.selectByMap(map);

        // 查询停售状态的菜品
        map.put("status", StatusConstant.DISABLE);
        Integer discontinuedSetmeals = setmealMapper.selectByMap(map);

        // 将查询结果包装成SetmealOverviewVO返回
        return SetmealOverViewVO
                .builder()
                .discontinued(discontinuedSetmeals)
                .sold(soldSetmeals)
                .build();
    }

    /**
     * 查询订单管理数据
     * @return
     */
    @Override
    public OrderOverViewVO overviewOrders() {
        // 查询全部订单数量
        Integer allOrders = orderMapper.selectByStatus(null);

        // 查询取消订单数量
        Integer cancelledOrders = orderMapper.selectByStatus(Orders.CANCELLED);

        // 查询完成订单数量
        Integer completedOrders = orderMapper.selectByStatus(Orders.COMPLETED);

        // 查询待派送订单数量
        Integer deliveredOrders = orderMapper.selectByStatus(Orders.DELIVERY_IN_PROGRESS);

        // 查询待接单订单数量
        Integer waitingOrders = orderMapper.selectByStatus(Orders.TO_BE_CONFIRMED);

        // 将查询结果包装成OrderOverViewVO返回
        return OrderOverViewVO
                .builder()
                .allOrders(allOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders)
                .build();
    }
}
