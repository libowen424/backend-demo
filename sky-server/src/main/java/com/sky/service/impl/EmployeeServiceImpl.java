package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
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
        System.out.println("当前线程的id："+Thread.currentThread().getId());


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
        // 进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());

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
     * 新增员工业务方法
     * @param employeeDTO
     */
    @Override
    @AutoFill(value = OperationType.INSERT) // 传入操作类型
    public void addEmployee(EmployeeDTO employeeDTO) {
        // 最好转成实体类
        Employee employee = new Employee();
        // 对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        // 设置状态
        employee.setStatus(StatusConstant.ENABLE);
        // 设置密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //log.info("当前时间： {}", LocalDateTime.now()); // 返回结果2023-09-02T21:31:48.428098800

        // 从ThreadLocal中取出解析的id
        //employee.setCreateUser(BaseContext.getCurrentId());
        //employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);
    }

    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // 分页查询: select * from employee limit 0,10

        // 用Mybatis的PageHelper插件来辅助分页查询，将page和pagesize通过ThreatLocal保存并传递
         PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
         // 这个page是pagehelper的
         Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);// 无需在sql中指定page和pagesize
         long total = page.getTotal();
         List<Employee> records = page.getResult();


        // 使用mybatis-plus来分页查询
        //QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        //if(employeePageQueryDTO.getName() != null & employeePageQueryDTO.getName()!="") {
        //    wrapper.like("name", employeePageQueryDTO.getName());
        //}
        // 这个page是mybatisplus的
        //Page<Employee> employeeIPage = new Page<>(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        //employeeIPage = employeeMapper.selectPage(employeeIPage, wrapper);
        //long total = employeeIPage.getTotal();
        //List<Employee> records = employeeIPage.getRecords();


        return new PageResult(total, records);
    }

    @Override
    public void statusChange(Integer status, Long id) {
        // 更改状态：update employee set status = ? where id = ?

        // mybatisplus方法1
        //Employee employee = new Employee();
        //employee.setId(id);
        //employee.setStatus(status);
        //employeeMapper.updateById(employee);


        // mybatisplus方法2
        //UpdateWrapper updateWrapper = new UpdateWrapper();
        //updateWrapper.eq("id", id);
        //updateWrapper.set("status", status);
        //employeeMapper.update(null, updateWrapper);


        // mybatis配置文件方法
        Employee employee = Employee.builder().status(status).id(id).build();
        employeeMapper.updateEmployee(employee);

    }

    @Override
    public Employee getInfoById(Long id) {

        // mybatis-plus提供的查询接口
        //Employee employee = employeeMapper.selectById(id);

        Employee employee = employeeMapper.getInfoById(id);

        employee.setPassword("*******");

        return employee;
    }

    @Override
    @AutoFill(value = OperationType.UPDATE) // 传入操作类型
    public void updateEmployee(EmployeeDTO employeeDTO) {
       Employee employee = new Employee();
       BeanUtils.copyProperties(employeeDTO, employee);

       // 公共字段用注解解决
       //employee.setUpdateTime(LocalDateTime.now());
       //employee.setUpdateUser(BaseContext.getCurrentId());

        // mybatisplus方法1
        //employeeMapper.updateById(employee);


        // mybatisplus方法2
        //UpdateWrapper updateWrapper = new UpdateWrapper();
        //updateWrapper.eq("id", employeeDTO.getId());
        //employeeMapper.update(employee, updateWrapper);


        // mybatis配置文件方法
       employeeMapper.updateEmployee(employee);
    }

}
