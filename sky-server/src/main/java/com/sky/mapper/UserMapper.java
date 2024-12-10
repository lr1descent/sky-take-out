package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {

    /**
     * 查询openid对应的user对象
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User selectByOpenid(String openid);

    /**
     * 插入User对象
     * @param user
     */
    void insert(User user);

    /**
     * 根据用户id查询用户
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    /**
     * 查询beginTime至endTime这个时间段的新增用户数量
     * @param beginTime
     * @param endTime
     * @return
     */
    @Select("select count(*) from user where create_time > #{beginTime} and create_time < #{endTime}")
    Integer selectByTime(LocalDateTime beginTime, LocalDateTime endTime);
}
