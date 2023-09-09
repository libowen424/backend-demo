package com.sky.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data // 提供类的get、set、equals、hashCode、canEqual、toString方法
@Builder
@NoArgsConstructor // 提供类的无参构造
@AllArgsConstructor // 提供类的全参构造
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    //@JsonFormat(pattern = "yyyy-MM-dd") // 设置时间数据格式，但是这样必须一个一个加，无法全局设置 // 注意，加不加，数据库插入的值不会变化，只是后端返回前端的数据发生了变化
    private LocalDateTime createTime;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;

}
