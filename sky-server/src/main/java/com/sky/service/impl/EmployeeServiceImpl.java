package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 对前端传送过来的密码进行加密，比对加密后的密码
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println("password is " + password);
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @Override
    public int save(EmployeeDTO employeeDTO) {
        // 创建employee类
        Employee employee = new Employee();

        // 将employeeDTO中的属性赋值给employee
        BeanUtils.copyProperties(employeeDTO, employee);

        // 继续给employee赋值必要的属性
//        // 创建时间和修改时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        // 账户状态，默认状态是正常状态
        employee.setStatus(StatusConstant.ENABLE);

        // 账户密码，默认情况下是加密后的123456
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

//        // 当前记录的创建人id和修改人id
//        Long currentId = BaseContext.getCurrentId();
//        employee.setCreateUser(currentId);
//        employee.setUpdateUser(currentId);

        // 调用employeeMapper接口中的save方法，新增employee
        return employeeMapper.save(employee);
    }

    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // 从 employeePageQueryDTO中获取要查询的页数和页大小
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        // 执行员工分页查询
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        // 从page中获取PageResult中的total和records
        long total = page.getTotal();
        List<Employee> records = page.getResult();

        // 将total和record封装到PageResult中返回给controller
        return new PageResult(total, records);
    }

    /**
     * 修改员工账户状态信息
     * @param status
     * @param id
     * @return
     */
    @Override
    public int startOrStop(Integer status, Long id) {
        // 调用mapper接口的update方法，增强接口的适用性
        // 所以传一个pojo类Employee给mapper接口
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .build();

        // 调用mapper接口的update方法
        return employeeMapper.update(employee);
    }

    /**
     * 根据员工id查询员工状态信息
     * @param id
     * @return
     */
    @Override
    public Employee selectById(Long id) {
        Employee employee = employeeMapper.selectById(id);

        // 修改员工密码为****，防止隐私泄露
        employee.setPassword("********");
        return employee;
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     * @return
     */
    @Override
    public int updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

//        // 设置员工的修改时间和修改者id
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(BaseContext.getCurrentId());

        return employeeMapper.update(employee);
    }

}
