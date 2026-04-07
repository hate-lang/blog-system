# blog-system

一款采用 B/C 端分离架构的现代化博客平台。包含面向公众读者的前台文章展示系统，以及具备严格权限校验的后台内容管理系统。

# 🌟 Langtian Blog (个人全栈博客系统)

![Vue3](https://img.shields.io/badge/Vue.js-3.x-4FC08D?style=flat-square&logo=vue.js)
![Spring Boot](https://img.shields.io/badge/SpringBoot-2.x/3.x-6DB33F?style=flat-square&logo=spring-boot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql)
![License](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)

> 一个基于 Spring Boot + Vue 3 + Element-Plus 开发的前后端分离企业级个人博客系统。从零到一独立设计，具备完善的 B 端后台管理与 C 端前台展示功能。

👉 **在线体验：** 以后更新

## 核心特性 (Features)

### 🛡️ 架构与安全 (后端亮点)

- **无状态鉴权**：采用 `JWT` Token 机制，结合自定义 `Interceptor` 拦截器实现优雅的全局请求鉴权。
- **上下文无感流转**：基于 `ThreadLocal` 封装用户上下文，在请求全链路中高效获取当前登录用户信息。
- **业务级安全防御**：底层 CRUD 深度处理，在文章修改/删除接口强制校验 `author_id`，彻底斩断**水平越权漏洞**。
- **全局兜底机制**：利用 `@RestControllerAdvice` 构建全局异常处理器，统一标准化 `Result` 响应体，杜绝报错堆栈外溢。
- **云端存储打通**：深度集成 **阿里云 OSS**，实现博客封面图、作者头像的高速云端直传与读取。

### 交互与展示 (前端亮点)

- **B/C 端物理隔离**：基于 `Vue-Router` 实现前台展示栏目与后台管理系统的路由解耦。
- **智能路由守卫**：配置全局 `beforeEach` 守卫，针对 `/admin` 路径实现精准的 401 拦截与未登录状态强制踢出。
- **网络层封装**：利用 `Axios` 请求拦截器自动注入 Token，响应拦截器统一处理错误弹窗与状态鉴权。
- **响应式 UI**：基于 `Element-Plus` 构建现代化、极简风的后台数据表格与前台瀑布流卡片。

---

## 技术栈 (Tech Stack)

### 前端 (Frontend)

- **核心框架**：Vue 3 (Composition API) + Vite
- **UI 组件库**：Element-Plus
- **路由与状态**：Vue-Router 4 + Pinia (可选)
- **网络请求**：Axios

### 后端 (Backend)

- **核心框架**：Spring Boot
- **持久层框架**：MyBatis-Plus
- **数据库**：MySQL 8.0
- **云服务**：阿里云 OSS (对象存储)
- **其他工具**：Hutool (工具类), JWT (身份认证)

---

## 快速开始 (Getting Started)

### 1. 环境准备

请确保您的电脑上已安装以下环境：

- Node.js (v16+)
- JDK (v17+ 推荐v21)
- MySQL (v8.0)
- Maven

### 2. 后端启动 (Backend Setup)
