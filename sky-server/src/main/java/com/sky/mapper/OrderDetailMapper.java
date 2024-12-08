package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 批量添加明细数据
     * @param details
     */
    void insertBatch(List<OrderDetail> details);
}
