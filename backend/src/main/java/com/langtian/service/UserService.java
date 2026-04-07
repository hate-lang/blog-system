package com.langtian.service;

import com.langtian.pojo.AuthorInfoDto;
import com.langtian.pojo.Result;
import com.langtian.pojo.UserLoginDTO;
import com.langtian.pojo.UserUpdateDTO;

public interface UserService {
   //登录，只有登录才能用其他的功能，注册除外
   Result<String> login(UserLoginDTO userLoginDTO);

   //更新用户头像
   Result<String> updateAvatar(String avatarUrl);

   //公共获取作者信息
   Result<AuthorInfoDto> getAuthorInfo(String username);

   // 更新用户部分信息
   Result<String> updateUser(UserUpdateDTO userUpdateDTO);
}
