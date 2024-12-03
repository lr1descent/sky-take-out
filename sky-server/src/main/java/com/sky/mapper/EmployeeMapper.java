package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username 用户名
     * @return 员工Employee
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 添加员工
     * @param e 员工信息
     */
//    @Insert("insert into employee values(#{e.getUsername()}, #{e.getName()}," +
//            "#{e.getPassword()}, #{e.getPhone()}, #{e.getSex()}, #{e.getIdNumber()}," +
//            "#{e.getStatus()}, #{e.getCreateTime()}, #{e.getUpdateTime()})")
//    @Insert("insert into employee (name, username, password, phone, sex, id_number, " +
//            "create_time, update_time, create_user, update_user, status) values" +
//            "(#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{createTime}, " +
//            "#{updateTime}, #{createUser}, #{updateUser}, #{status})")
    @Insert("insert into employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user)" +
            "values" +
            "(#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    int save(Employee e);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);
}
