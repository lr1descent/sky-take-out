package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 添加员工
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @ApiOperation("添加员工")
    public Result<String> employee(@RequestBody EmployeeDTO employeeDTO) {
        // 记录添加员工的日志信息
        log.info("新增员工：{}",employeeDTO);

        // 添加员工
        // 如果添加成功，那么返回success
        // 如果添加失败，那么返回error
        int status = employeeService.save(employeeDTO);
        if (status == StatusConstant.ENABLE) {
            return Result.success();
        } else {
            return Result.error(MessageConstant.SAVE_ERROR);
        }
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("员工分页查询信息：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 员工账户状态修改
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("员工账户状态修改")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("员工账户状态修改：{}, {}", status, id);
        int ok = employeeService.startOrStop(status, id);
        if (ok == StatusConstant.ENABLE) {
            return Result.success();
        } else {
            return Result.error(MessageConstant.UPDATE_STATUS_FAILURE);
        }
    }

    /**
     * 根据员工id查询员工信息
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据员工id查询员工信息")
    public Result<Employee> selectById(@PathVariable Long id) {
        log.info("查询id = {}的员工信息", id);
        Employee employee = employeeService.selectById(id);
        return Result.success(employee);
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     * @return
     */
    @PutMapping
    @ApiOperation("编辑员工信息")
    public Result updateEmployee(@RequestBody EmployeeDTO employeeDTO) {
        log.info("编辑员工信息：{}", employeeDTO);
        int ok = employeeService.updateEmployee(employeeDTO);

        if (ok == 1) {
            return Result.success();
        } else {
            return Result.error(MessageConstant.UPDATE_EMPLOYEE_FAILURE);
        }
    }
}
