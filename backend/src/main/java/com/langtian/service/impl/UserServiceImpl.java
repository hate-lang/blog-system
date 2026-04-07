package com.langtian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.langtian.mapper.UserMapper;
import com.langtian.pojo.*;
import com.langtian.service.UserService;
import com.langtian.utils.JwtUtils;
import com.langtian.utils.ThreadLocalUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<String> login(UserLoginDTO loginDTO) {
        //一个空白的盒子，专门用了装User，Mybatis-plus中的类
        QueryWrapper<User> wrapper = new QueryWrapper<>();

        //在这个盒子上写下sql语句，相当于用Java实现sql
        wrapper.eq("username", loginDTO.getUsername());

        //把这个写上sql语句的盒子交给userMapper，它按照语句找到并且返回一个user
        User user = userMapper.selectOne(wrapper);

        //判断用户是否存在
        if (user == null) {
            return Result.error("用户不存在！");
        }

        //判断用户密码与前端（loginDTO）传入的密码一致
        if (!((user.getPassword()).equals((loginDTO.getPassword())))) {
            return Result.error("密码不正确");
        }

        //判断用户状态
        if (user.getStatus() == 2) {
            return Result.error("账号已经被封禁，请联系客服！");
        }

        //全部通过，返回成功代码
        //以下是更高级的写法，发jwt令牌 return Result.success("登录成功！");

        //找个记录条讲用户信息存入
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());

        //把纸条塞进JwtUtils中，换取那段乱码
        String token = JwtUtils.generateToken(claims);

        //把乱码打包道data中返回给前端。
        return Result.success(token);

    }

    @Override
    public Result<String> updateAvatar(String avatarUrl) {
        // 1. 经典操作：从储物柜里拿出当前登录用户的 ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer currentUserID = (Integer) map.get("id");

        // 2.  MyBatis-Plus 局部更新神技！
        // 我们不需要把用户的全套信息查出来，只需要 new 一个空对象
        User user = new User();
        user.setId(currentUserID); // 告诉框架：我要改哪个用户
        user.setUserPic(avatarUrl);  // 告诉框架：我要把他的头像改成什么

        // 3. 调用 updateById！
        // MyBatis-Plus 极其聪明，它发现 user 对象里只有 id 和 userPic 有值，其它(密码、状态)都是 null
        // 它就会在底层生成 SQL: UPDATE users SET user_pic = ? WHERE id = ?
        // 绝对不会把你的密码错误地覆盖成空！
        userMapper.updateById(user);


        return Result.success("头像更新成功！");
    }

    @Override
    public Result<AuthorInfoDto> getAuthorInfo(String username) {
        AuthorInfoDto authorInfoDto = new AuthorInfoDto();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);

        //使用Spring直接把user转换为AuthorInfoDto，避免写一堆set
        BeanUtils.copyProperties(user, authorInfoDto);

        return Result.success(authorInfoDto);
    }

    @Override
    public Result<String> updateUser(UserUpdateDTO userUpdateDTO) {

        Map<String,Object> map=ThreadLocalUtil.get();
        Integer currentID=(Integer) map.get("id");

        User user=new User();
        user.setId(currentID);
        user.setEmail(userUpdateDTO.getEmail());
        user.setUserBio(userUpdateDTO.getUserBio());

        userMapper.updateById(user);
        return Result.success("信息更新成功!");
    }
}
