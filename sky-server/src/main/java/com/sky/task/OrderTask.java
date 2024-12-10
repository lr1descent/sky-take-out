package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时处理订单
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理未及时支付的订单
     * 15分钟内没有支付，设置订单状态为取消
     * 每分钟检查一次订单
     */
    @Scheduled(cron = "0 * * * * *")
//    @Scheduled(cron = "0/5 * * * * *")
    public void notPayOrder() {
        log.info("处理未及时支付的订单：{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        // 查询支付状态为未支付且订单时间超过15分钟的订单
        List<Orders> ordersList = orderMapper.selectByStatusAndOrderTime(Orders.PENDING_PAYMENT, time);

        if (ordersList != null && ordersList.size() > 0) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("超时未支付");
                orders.setCancelTime(LocalDateTime.now());

                orderMapper.update(orders);
            }
        }
    }

    /**
     * 处理未及时完成的订单
     * 每天凌晨2点检查所有正在派送的订单，设置为已完成
     */
    @Scheduled(cron = "0 0 2 * * *")
//    @Scheduled(cron = "1/5 * * * * *")
    public void notCompletedOrders() {
        log.info("处理未及时完成的订单：{}", LocalDateTime.now());

        // 查询正在派送的订单，设置为已完成
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);

        List<Orders> orders = orderMapper.selectByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, time);

        if (orders != null && orders.size() > 0) {
            for (Orders order : orders) {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }
    }
}
