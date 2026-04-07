<template>
  <div class="front-layout">
    <header class="header">
      <div class="header-content">
        <div class="header-left">
          <router-link to="/home" class="logo-link">
            <div class="logo-wrapper">
              <span class="logo-text">Langtian Blog</span>
            </div>
          </router-link>
        </div>
        <nav class="nav-links">
          <router-link to="/home" class="nav-item">
            <el-icon class="nav-icon"
              ><HomeFilled v-if="$route.path.includes('/home')" /><House v-else
            /></el-icon>
            <span>首页</span>
          </router-link>
          <router-link to="/about" class="nav-item">
            <el-icon class="nav-icon"
              ><Avatar v-if="$route.path.includes('/about')" /><User v-else
            /></el-icon>
            <span>关于 langtian</span>
          </router-link>
        </nav>
        <div class="header-right"></div>
      </div>
    </header>

    <div class="main-container">
      <aside :class="['sidebar-wrapper', { 'is-closed': !isSidebarOpen }]">
        <div class="sidebar-box">
          <div class="author-info">
            <div class="avatar-container" @click="goToAbout">
              <img
                :src="
                  userPic || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
                "
                class="avatar-img"
                alt="Author Avatar"
              />
              <div class="avatar-overlay">
                <el-icon class="id-card-icon"><Postcard /></el-icon>
              </div>
            </div>

            <h2 class="author-name">{{ username }}</h2>
            <p class="author-email">{{ email }}</p>
          </div>

          <el-divider border-style="dashed" />

          <div class="tags-section">
            <h3>个人标签</h3>
            <div class="tags-wrapper">
              <el-tag class="tag-item" type="info">Java</el-tag>
              <el-tag class="tag-item" type="success">Vue3</el-tag>
              <el-tag class="tag-item" type="warning">SpringBoot</el-tag>
            </div>
          </div>
        </div>

        <div class="toggle-btn" @click="toggleSidebar">
          <el-icon>
            <ArrowLeft v-if="isSidebarOpen" />
            <ArrowRight v-else />
          </el-icon>
        </div>
      </aside>

      <main class="content-area">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

import { useRouter } from 'vue-router'
import {
  ArrowLeft,
  ArrowRight,
  HomeFilled,
  House,
  Avatar,
  User,
  Postcard,
} from '@element-plus/icons-vue'

const router = useRouter()
const isSidebarOpen = ref(true)

const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value
}

// ================== 获取博主信息的变量定义 ==================
const userPic = ref('')
const email = ref('')
// 【新增】定义一个变量接收名字，默认给个 langking 防止接口慢时页面闪烁
const username = ref('langking')

// 跳转函数：使用动态获取到的 username
const goToAbout = () => {
  router.push(`/about?username=${username.value}`)
}

const fetchAuthorInfo = async () => {
  try {
    // 这里的请求路径依然写 langking，因为这是查你这个特定博主数据的标识（相当于查身份证号）
    const res = await axios.get('http://localhost:8080/author/public/info/langking')

    if (res.data.code === 200) {
      userPic.value = res.data.data.userPic
      email.value = res.data.data.email || '暂未公开邮箱'
      // 【新增】把数据库里真实的名字存起来
      username.value = res.data.data.username || 'langking'
    } else {
      console.warn('获取博主信息异常:', res.data.message)
    }
  } catch (error) {
    console.error('网络请求失败:', error)
  }
}

onMounted(() => {
  fetchAuthorInfo()
})
</script>

<style scoped>
/* ====== 基础与头部样式 (保持不变) ====== */
.front-layout {
  min-height: 100vh;
  background-color: #f4f5f7;
  display: flex;
  flex-direction: column;
}
.header {
  height: 60px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 20px;
}
.header-left {
  flex: 1;
  display: flex;
  align-items: center;
}
.header-right {
  flex: 1;
}
.logo-link {
  text-decoration: none;
  display: inline-block;
}
.logo-wrapper {
  position: relative;
  display: inline-block;
  padding: 4px 8px 8px 12px;
  z-index: 1;
}
.logo-text {
  font-size: 26px;
  font-weight: 600;
  font-family: 'STXingkai', '华文行楷', 'STKaiti', '楷体', cursive;
  /* 红蓝渐变 */
  /* background: linear-gradient(135deg, #e11d48 0%, #3b82f6 100%); */
  background: linear-gradient(135deg, #0f766e 0%, #10b981 100%);

  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  user-select: none;
  letter-spacing: 1px;
}
.logo-wrapper::before,
.logo-wrapper::after {
  content: '';
  position: absolute;
  left: 0;
  width: 115%;
  height: 15px;
  border-bottom: 2px solid;
  border-radius: 50%;
  z-index: -1;
  -webkit-mask-image: linear-gradient(to right, rgba(0, 0, 0, 1) 10%, rgba(0, 0, 0, 0) 90%);
  mask-image: linear-gradient(to right, rgba(0, 0, 0, 1) 10%, rgba(0, 0, 0, 0) 90%);
}
.logo-wrapper::before {
  bottom: 6px;
  border-color: #be123c;
  transform: rotate(-3deg);
}
.logo-wrapper::after {
  bottom: 2px;
  border-color: #60a5fa;
  transform: rotate(2deg);
  height: 20px;
}
.nav-links {
  display: flex;
  gap: 40px;
  justify-content: center;
}
.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  text-decoration: none;
  color: #909399;
  font-size: 16px;
  transition: all 0.3s;
}
.nav-icon {
  font-size: 20px;
}
.nav-item:hover,
.nav-item.router-link-active {
  color: #303133;
  font-weight: 600;
}

/* ====== 下方主体布局 ====== */
.main-container {
  flex: 1;
  max-width: 1200px;
  margin: 20px auto;
  width: 100%;
  display: flex;
  gap: 20px;
  padding: 0 20px;
  box-sizing: border-box;
  /* 关键修改：让左侧边栏与右侧内容区等高拉伸 */
  align-items: stretch;
}

/* ====== 左侧边栏重构 ====== */
.sidebar-wrapper {
  position: relative;
  /* 基础宽度，随内部盒子变化 */
  width: 280px;
  transition: width 0.3s ease;
  display: flex;
  flex-direction: column;
}

/* 侧边栏收起时的包裹器状态 */
.sidebar-wrapper.is-closed {
  width: 0px;
}

.sidebar-box {
  width: 280px;
  /* 关键修改：高度100%，充满整个拉伸后的父容器 */
  height: 100%;
  background-color: #fff;
  border-radius: 8px;
  padding: 24px 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  box-sizing: border-box;
  overflow: hidden; /* 防止内容挤出 */
  transition: all 0.3s ease;
  opacity: 1;
}

/* 侧边栏收起时，隐藏内部盒子内容，防止文字挤压难看 */
.sidebar-wrapper.is-closed .sidebar-box {
  padding: 24px 0;
  opacity: 0;
  pointer-events: none; /* 禁用点击 */
}

/* === 头像区域重构 === */
.author-info {
  text-align: center;
  margin-bottom: 20px;
}

.avatar-container {
  position: relative;
  width: 90px;
  height: 90px;
  margin: 0 auto;
  /* 圆角方形 */
  border-radius: 18px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

/* 黑色遮罩 */
.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4); /* 半透明变暗 */
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0; /* 默认隐藏 */
  transition: opacity 0.3s ease;
}

.id-card-icon {
  color: #fff;
  font-size: 28px; /* 证件图标大小适中 */
  transform: translateY(10px); /* 初始稍微靠下一点 */
  transition: transform 0.3s ease;
}

/* 鼠标悬浮效果 */
.avatar-container:hover .avatar-overlay {
  opacity: 1;
}
.avatar-container:hover .id-card-icon {
  transform: translateY(0); /* 悬浮时平滑上升 */
}
.avatar-container:hover .avatar-img {
  transform: scale(1.05); /* 头像轻微放大 */
}

/* 文本信息 */
.author-name {
  margin: 16px 0 4px;
  font-size: 18px;
  color: #303133;
  font-weight: 600;
}
.author-email {
  margin: 0;
  font-size: 14px;
  color: #909399;
}
.tags-section h3 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 12px;
}
.tags-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* === 开关按钮重构 === */
.toggle-btn {
  position: absolute;
  /* 固定在边框右侧 */
  right: -15px;
  /* 垂直居中 */
  top: 50%;
  transform: translateY(-50%);
  width: 30px;
  height: 30px;
  background-color: #fff;
  border-radius: 50%;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  z-index: 10;
  color: #909399;
  transition: all 0.3s;
}

.toggle-btn:hover {
  color: #3b82f6; /* 悬浮变成蓝色 */
  transform: translateY(-50%) scale(1.1); /* 稍微放大一点 */
}

/* ====== 右侧内容区 ====== */
.content-area {
  flex: 1;
  min-width: 0;
  /* 当左侧收起时，flex: 1 会让右侧自然地平滑展开占据剩下的空间 */
  transition: width 0.3s ease;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
