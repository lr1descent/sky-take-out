package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders list(Long id);

    /**
     * 查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    Page<OrderVO> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据状态查询订单数量
     * @param status
     * @return
     */
    Integer selectByStatus(Integer status);

    /**
     * 查询未支付且订单时间超过15分钟的所有订单
     * @param pendingPayment
     * @param time
     * @return
     */
    @Select("select * from orders where status = #{pendingPayment} and order_time < #{time}")
    List<Orders> selectByStatusAndOrderTime(Integer pendingPayment, LocalDateTime time);

    /**
     * 根据map查询订单数量
     * @param map
     * @return
     */
    Integer selectByMap(Map map);

    /**
     * 查询某个区间之间的营业额总和
     * @param map
     * @return
     */
    Double turnOver(Map map);
}
