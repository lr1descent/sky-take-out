package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
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
}
