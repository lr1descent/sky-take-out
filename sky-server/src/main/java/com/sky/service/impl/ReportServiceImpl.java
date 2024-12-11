package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.PageResult;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        // 统计begin至end期间每天的营业额
        //localDateList存储begin至end期间每天的日期
        List<LocalDate> localDateList = new ArrayList<>();
        while (!begin.equals(end)) {
            localDateList.add(begin);
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }

        List<Double> sums = new ArrayList<>();
        // 统计begin至end期间每天的营业额
        for (LocalDate localDate : localDateList) {
            // 由于order的check_time统计到了时分秒，所以最好先根据localDate得到每天开始的时分秒和结束的时分秒
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            // 查询beginTime至endTime期间的完成的订单，并统计这些订单的营业额总和
            // 创建map，将传递的参数加入至map中
            Map map = new HashMap<>();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);

            Double sum = reportMapper.selectByTime(map);

            // 如果当天没有完成的订单，那么sum将为null，需要将sum赋值为0
            if (sum == null) sum = 0.0;

            // 将每天的营业额加入至sums中
            sums.add(sum);
        }

        // 将sums和localDateList包装成TurnoverReportVO返回
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(localDateList, ","))
                .turnoverList(StringUtils.join(sums, ","))
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        // 统计begin到end期间每天的新增用户量和总用户量

        // localDateList存储begin至end期间每天的日期
        List<LocalDate> localDateList = new ArrayList<>();

        while (!begin.equals(end)) {
            localDateList.add(begin);
            begin = begin.plusDays(1);
            localDateList.add(end);
        }

        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        // 查询每天的新增用户量和总用户量
        int totalUsers = 0;
        for (LocalDate localDate : localDateList) {
            // 用户的createTime记录了具体的时分秒，但localDate只记录了天
            // 所以首先根据localDate求出每天的开始时间beginTime和结束时间entTime
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Integer newUsers = userMapper.selectByTime(beginTime, endTime);

            totalUsers += newUsers;
            newUserList.add(newUsers);
            totalUserList.add(totalUsers);
        }

        // 将newUserList, totalUserList包装成UserReportVO返回
        return UserReportVO.builder()
                .dateList(StringUtils.join(localDateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 订单统计
     * @return
     */
    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        // 统计begin至end期间每天的订单数量，有效订单数量

        // localDateList存储begin至end期间每天的日期
        List<LocalDate> localDateList = new ArrayList<>();

        while (!begin.equals(end)) {
            localDateList.add(begin);
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }

        // orderTotalList存储每天的总订单量
        // orderInvalidList存储每天的有效订单量
        List<Integer> orderTotalList = new ArrayList<>();
        List<Integer> orderInvalidList = new ArrayList<>();

        Integer totalOrders = 0;
        Integer invalidOrders = 0;

        for (LocalDate localDate : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);

            // 查询一天的总订单数
            Integer ordersCount = reportMapper.selectByMap(map);

            map.put("status", Orders.COMPLETED);

            // 查询一天的有效订单数
            Integer invalidCount = reportMapper.selectByMap(map);

            orderTotalList.add(ordersCount);
            orderInvalidList.add(invalidCount);

            totalOrders += ordersCount;
            invalidOrders += invalidCount;
        }

        Double completedRate = 0.0;
        if (totalOrders != null) completedRate = invalidOrders.doubleValue() / totalOrders;

        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(StringUtils.join(localDateList, ","))
                .validOrderCountList(StringUtils.join(orderInvalidList, ","))
                .orderCountList(StringUtils.join(orderTotalList, ","))
                .orderCompletionRate(completedRate)
                .totalOrderCount(totalOrders)
                .validOrderCount(invalidOrders)
                .build();
        return orderReportVO;
    }

    /**
     * 查询销量排名top10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(begin, LocalTime.MAX);

        List<GoodsSalesDTO> goodsSalesDTOS = reportMapper.selectSalesTop10(beginTime, endTime);

        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();

        for (GoodsSalesDTO goodsSalesDTO : goodsSalesDTOS) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }
}
