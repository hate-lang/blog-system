package com.langtian.pojo;

import lombok.Data;

@Data   //提供前端页面展示作者信息
public class AuthorInfoDto {

    private String username;
    private  String userPic;
    private  String email;
    private String userBio;
}
