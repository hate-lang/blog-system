-- 创建数据库 (如果不存在的话)
CREATE DATABASE IF NOT EXISTS `my_blog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 切换到该数据库
USE `my_blog`;

create table articles
(
    id          int auto_increment comment '文章ID'
        primary key,
    title       varchar(100)                       not null comment '文章标题',
    content     text                               not null comment '文章正文(支持超长文本)',
    author_id   int                                not null comment '作者ID(对应users表的id)',
    create_time datetime default CURRENT_TIMESTAMP null comment '发布时间',
    summary     varchar(200)                       null comment '文章概括',
    cover_img   varchar(255)                       null
)
    comment '文章表';

create table users
(
    id          int auto_increment comment '主键ID'
        primary key,
    username    varchar(50)                        not null comment '账号(必须唯一)',
    password    varchar(100)                       not null comment '密码',
    status      tinyint  default 0                 null comment '状态：0正常，1危险，2封禁',
    create_time datetime default CURRENT_TIMESTAMP null comment '注册时间',
    user_pic    varchar(255)                       null comment '用户头像地址',
    email       varchar(100)                       null comment '用户邮箱',
    user_bio    text                               null comment '作者详细介绍',
    constraint username
        unique (username)
)
    comment '用户表';
