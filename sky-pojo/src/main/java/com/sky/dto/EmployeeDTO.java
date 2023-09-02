package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
/*
封装前端提交的数据格式
 */
public class EmployeeDTO implements Serializable {

    private Long id;

    private String username;

    private String name;

    private String phone;

    private String sex;

    private String idNumber;

}
