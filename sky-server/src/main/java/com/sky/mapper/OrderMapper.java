package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     * @param order
     */
    void insert(Orders order);

    /**
     * 根据用户id和订单号查询订单
     * @param userId
     * @param orderNumber
     * @return
     */
    Orders selectByOrderNumberAndUserId(Long userId, String orderNumber);

    /**
     * 根据id更新订单状态
     * @param order
     */
    void update(Orders order);
}
