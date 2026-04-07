package com.langtian.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users")   //告诉mybatis-plus这个类对应users表
public class User {

    //告诉mybatis-plus主键是id，而且自增
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;
    private String password;
    private Integer status;
    private LocalDateTime createTime;

    //新增字段
    private  String userPic;
    private  String email;

    //作者介绍
    private String userBio;

}
