package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface ReportMapper {
    /**
     * 查询beginTime至endTime期间的已完成的订单并统计营业额综合
     * @return
     */
    @Select("select sum(amount) from orders " +
            "where order_time > #{beginTime} and order_time < #{endTime} and status = #{status}")
    Double selectByTime(Map map);

    /**
     * 根据map查询订单
     * @param map
     * @return
     */
    Integer selectByMap(Map map);
}
