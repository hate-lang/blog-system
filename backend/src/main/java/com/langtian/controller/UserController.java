package com.langtian.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.langtian.mapper.UserMapper;
import com.langtian.pojo.*;
import com.langtian.service.UserService;
import com.langtian.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @GetMapping("/test/user")
    public Result<List<User>> getAllUsers() {
        //return userMapper.selectList(null);
        //先把mybatis查询的数据放到集合中
        List<User> userList = userMapper.selectList(null);
        //通过调用sucess(T data)返回Result对象
        return Result.success(userList);
    }

    @GetMapping("/test/secrets")
    public Result<String> secret() {
        return Result.success("成功看到");
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.login(userLoginDTO);
    }

    @GetMapping("/user/info")
    public Result<User> getUserInfo() {
        // 1. 见证奇迹的时刻：前端没传任何参数，我们直接从储物柜里拿取当前用户的信息！
        Map<String, Object> map = ThreadLocalUtil.get();

        // 2. 把刚才存进去的 username 拿出来
        String username = (String) map.get("username");

        // 3. 呼叫主厨（Service/Mapper），去数据库里查这个用户的详细信息（比如注册时间、状态等）
        // 这里用 MyBatis-Plus 的老套路，构造一个查询条件
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);
        // 4. 大厂脱敏规范：绝对不能把密码返回给前端！把密码设为空或打个码！
        user.setPassword("******");

        // 5. 穿上 Result 包装盒，返回给前端
        return Result.success(user);

    }

    @GetMapping("/author/public/info/{username}")
    public Result<AuthorInfoDto> getAuthorInfo(@PathVariable String username){
        return userService.getAuthorInfo(username);
    }

    @PatchMapping("/updateAvatar")
    public Result<String> updateAvatar(@RequestParam String avatarUrl){
        return userService.updateAvatar(avatarUrl);
    }

    @PutMapping("/user/update")
    public Result<String> updateUser(@RequestBody UserUpdateDTO userUpdateDTO){
        return userService.updateUser(userUpdateDTO);
    }
}
