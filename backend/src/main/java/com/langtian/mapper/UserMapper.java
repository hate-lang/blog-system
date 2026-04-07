package com.langtian.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.langtian.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    //什么都不用写，mybatis-plus底层已经完成
}
