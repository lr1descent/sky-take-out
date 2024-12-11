package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.vo.SalesTop10ReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
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

    /**
     * 销量统计top10
     * @param beginTime
     * @param endTime
     * @return
     */
    List<GoodsSalesDTO> selectSalesTop10(LocalDateTime beginTime, LocalDateTime endTime);
}
