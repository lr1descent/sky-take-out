package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.common.utils.HttpUtil;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpUtils;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private static final String URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        // 根据code获取openid
        String openid = getOpenid(userLoginDTO.getCode());

        // 如果openid不合法，抛出异常
        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 查询数据库中是否存在该openid对应的User
        User user = userMapper.selectByOpenid(openid);

        // 如果不存在User对象，实现自动注册
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        // 为微信用户创建jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

//        System.out.println("为微信用户创建jwt令牌, token is " + token);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(openid)
                .token(token)
                .build();
        return userLoginVO;
    }

    private String getOpenid(String code) {
        // 通过发送Get请求给微信服务器，调用微信服务器的登录接口
        /*
        传参：
        1. appid：小程序id
        2. secret：小程序appSecret
        3. js_code：登录时获取的code
        4. grant_type：授权类型，要填写authorization_code
         */
        HashMap<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");

        // 获取微信服务器返回的json字符串
        String s = HttpClientUtil.doGet(URL, map);

        // 将json字符串转换成json对象，并获取openid
        JSONObject jsonObject = JSON.parseObject(s);
        String openid = jsonObject.getString("openid");

        return openid;
    }
}
