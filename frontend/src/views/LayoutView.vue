<template>
  <div class="common-layout">
    <el-container class="layout-container">
      <el-aside width="200px" class="aside">
        <div class="logo">Langtian Blog</div>
        <el-menu router default-active="/admin/article" class="el-menu-vertical">
          <el-menu-item index="/admin/article">
            <el-icon><Document /></el-icon>
            <span>文章管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/profile">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="header-right">
            <span>欢迎回来，Admin</span>
            <el-button type="danger" link @click="logout">退出登录</el-button>
          </div>
        </el-header>

        <el-main class="main">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { Document, User } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 退出登录的逻辑
const logout = () => {
  localStorage.removeItem('token') // 清除 token
  router.push('/login') // 跳回登录页
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.aside {
  background-color: #304156;
  color: white;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  border-bottom: 1px solid #1f2d3d;
}
.el-menu-vertical {
  border-right: none;
  background-color: transparent;
}
/* 覆盖 menu 的默认文字颜色 */
:deep(.el-menu-item) {
  color: #bfcbd9;
}
:deep(.el-menu-item.is-active) {
  color: #409eff;
}
.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: flex-end;
  align-items: center;
}
.main {
  background-color: #f0f2f5;
}
</style>
