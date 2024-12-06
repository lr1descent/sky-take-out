package com.sky.controller.user;

import com.sky.dto.UserLoginDTO;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/user")
@Slf4j
@Api(tags = "用户端相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<UserLoginVO> userLogin(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO.getCode());
        UserLoginVO userLoginVO = userService.login(userLoginDTO);
        System.out.println("token is " + userLoginVO.getToken());
        return Result.success(userLoginVO);
    }
}
