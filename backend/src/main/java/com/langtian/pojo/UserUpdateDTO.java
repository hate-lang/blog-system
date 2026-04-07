package com.langtian.pojo;

import lombok.Data;

@Data // 用于更新用户部分信息
public class UserUpdateDTO {
    private String email;
    private String userBio;
}
