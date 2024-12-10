package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        // 用户下单
        // 1. 查看当前登录用户的地址是否为空，如果不为空，那么可以继续下一步；如果为空，那么抛出异常
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.selectById(ordersSubmitDTO.getAddressBookId());

        if (addressBook == null) {
            throw new OrderBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 2. 查看当前登录用户的购物车是否为空，如果为空，抛出异常；否则进行下一步
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(userId);

        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new OrderBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 3. 添加订单数据
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, order);

        // 设置订单的相关属性
        order.setUserId(userId);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setOrderTime(LocalDateTime.now());
        order.setPayStatus(Orders.UN_PAID);
        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getDetail());
        order.setConsignee(addressBook.getConsignee());

        orderMapper.insert(order);

        // 4. 添加订单对应的若干条明细数据
        List<OrderDetail> details = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);

            orderDetail.setOrderId(order.getId());
            details.add(orderDetail);
        }

        orderDetailMapper.insertBatch(details);

        // 用户下单后，可以删除购物车中的数据
        shoppingCartMapper.deleteByUserId(userId);

        // 5. 封装OrdersSubmitVO数据返回
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime())
                .build();

        return orderSubmitVO;
    }

    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("code", "ORDERPAID");

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        //替代微信支付成功后的数据库订单状态更新
        paySuccess(ordersPaymentDTO.getOrderNumber());

        return vo;
    }

    @Override
    public void paySuccess(String orderNumber) {
        // 获取当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单号获取当前用户的订单
        Orders order = orderMapper.selectByOrderNumberAndUserId(userId, orderNumber);

        // 修改该订单的状态、支付状态、结账时间
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(Orders.TO_BE_CONFIRMED);
        order.setPayStatus(Orders.PAID);
        order.setCheckoutTime(LocalDateTime.now());

        // 支付成功，向管理端发送来单提醒
        Map map = new HashMap();
        map.put("type", 1);
        map.put("orderId", order.getId());
        map.put("content", "你有新的订单");

        webSocketServer.sendToAllClient(JSONObject.toJSONString(map));

        orderMapper.update(order);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO list(Long id) {
        // 除了要从orders中查询订单外，还要查询该订单对应的明细
        OrderVO orderVO = new OrderVO();
        Orders order = orderMapper.list(id);

        BeanUtils.copyProperties(order, orderVO);

        // 从order_detail中查询该订单对应的所有明细
        List<OrderDetail> details = orderDetailMapper.list(id);
        orderVO.setOrderDetailList(details);

        return orderVO;
    }

    /**
     * 历史订单查询（分页查询）
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        // 设置分页查询的相关参数
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        Page<OrderVO> page = orderMapper.pageQuery(ordersPageQueryDTO);

        // 要求查询的订单要带有口味数据
        List<OrderVO> result = page.getResult();

        for (OrderVO orderVO : result) {
            List<OrderDetail> details = orderDetailMapper.list(orderVO.getId());
            orderVO.setOrderDetailList(details);
        }

        return new PageResult(page.getTotal(), result);
    }

    /**
     * 取消订单
     * @param id
     */
    @Override
    public void cancel(Long id) {
        Orders order = Orders.builder()
                .id(id)
                .status(Orders.CANCELLED)
                .build();
        orderMapper.update(order);
    }

    /**
     * 再来一单
     * @param id
     */
    @Override
    public void reorder(Long id) {
        // 根据订单id查找该订单中所有明细
        // 将所有明细加入至购物车

        // 查找订单id对应的所有明细
        List<OrderDetail> details = orderDetailMapper.list(id);
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        for (OrderDetail detail : details) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(detail, shoppingCart);

            // 给shoppingCart设置创建时间和userid
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setUserId(BaseContext.getCurrentId());

            shoppingCarts.add(shoppingCart);
        }

        // 添加购物车
        shoppingCartMapper.insertBatch(shoppingCarts);
    }

    /**
     * 接单
     * @param confirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO confirmDTO) {
        Orders order = Orders.builder()
                .id(confirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();

        orderMapper.update(order);
    }

    /**
     * 派送订单
     * @param id
     */
    @Override
    public void deliver(Long id) {
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .deliveryStatus(StatusConstant.ENABLE)
                .build();
        orderMapper.update(orders);
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    @Override
    public void reject(OrdersRejectionDTO ordersRejectionDTO) {
        Orders order = Orders.builder()
                .id(ordersRejectionDTO.getId())
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .build();

        orderMapper.update(order);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void complete(Long id) {
        Orders order = Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .build();

        orderMapper.update(order);
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders order = Orders.builder()
                .id(ordersCancelDTO.getId())
                .status(Orders.CANCELLED)
                .cancelReason(ordersCancelDTO.getCancelReason())
                .build();

        orderMapper.update(order);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();

        orderStatisticsVO.setDeliveryInProgress(orderMapper.selectByStatus(Orders.DELIVERY_IN_PROGRESS));
        orderStatisticsVO.setConfirmed(orderMapper.selectByStatus(Orders.CONFIRMED));
        orderStatisticsVO.setToBeConfirmed(orderMapper.selectByStatus(Orders.TO_BE_CONFIRMED));

        return orderStatisticsVO;
    }

    /**
     * 催单
     * @param id
     */
    @Override
    public void remind(Long id) {
        // 给管理端发送催单提醒
        Map map = new HashMap<>();
        map.put("type", 2);
        map.put("orderId", id);
        map.put("content", "催单提醒");

        webSocketServer.sendToAllClient(JSONObject.toJSONString(map));
    }
}
