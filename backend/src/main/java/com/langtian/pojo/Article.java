package com.langtian.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("articles")  //让mybatis-plus知道它对应articles这张表
public class Article {

    //告诉mybatis-plus主键是id而且自增
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title;
    private String content;

    private Integer authorId;
    private LocalDateTime createTime;

    //新增字段
    private String summary;
    private String coverImg;
}
