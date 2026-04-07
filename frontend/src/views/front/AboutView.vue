<template>
  <div class="about-container" v-loading="loading">
    <div v-if="authorInfo.username" class="about-content">
      <div class="top-section">
        <div class="bio-content">
          <h2 class="greeting">Hello World, and Hello You. 👋</h2>
          <div class="bio-text article-content" v-html="formatBio(authorInfo.userBio)"></div>
        </div>

        <div class="avatar-wrapper">
          <img
            :src="authorInfo.userPic || defaultAvatar"
            alt="Author Avatar"
            class="author-avatar"
          />
        </div>
      </div>

      <div class="divider-line"></div>

      <div class="card-section">
        <div class="info-card">
          <div class="card-item username">
            <span class="label">博主：</span>
            <span class="value">{{ authorInfo.username }}</span>
          </div>
          <div class="card-item email" v-if="authorInfo.email">
            <span class="label">邮箱：</span>
            <span class="value">{{ authorInfo.email }}</span>
          </div>
          <div class="card-item email" v-else>
            <span class="label">邮箱：</span>
            <span class="value empty-text">暂未公开邮箱</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

//新增引入 marked 库，用于将 Markdown 转换为 HTML
import { marked } from 'marked'
import DOMPurify from 'dompurify' // 引入 DOMPurify 用于 XSS 防护

const route = useRoute()

// 状态定义
const loading = ref(false)
const authorInfo = ref({})
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 【新增】专门用来格式化简介的函数
const formatBio = (text) => {
  if (!text) return '这个人很懒，什么都没写...'
  // 解析 + 净化
  return DOMPurify.sanitize(marked.parse(text))
}

// 获取作者信息
const fetchAuthorInfo = async () => {
  loading.value = true

  // 核心逻辑：如果路由里传了 username (比如 /about?username=xxx)，就用路由的；
  // 否则，作为个人博客，直接写死默认的站长用户名（比如 'langking'）
  const targetUsername = route.query.username || 'langking'

  try {
    const res = await axios.get(`http://localhost:8080/author/public/info/${targetUsername}`)

    if (res.data.code === 200) {
      authorInfo.value = res.data.data
    } else {
      ElMessage.error(res.data.message || '获取博主信息失败')
    }
  } catch (error) {
    console.error('获取关于信息失败:', error)
    ElMessage.error('网络开了个小差，请稍后再试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchAuthorInfo()
})
</script>

<style scoped>
/* === 基础容器：优雅的内边距，无背景图 === */
.about-container {
  max-width: 860px; /* 限制最大宽度，保证阅读体验 */
  margin: 0 auto;
  padding: 60px 40px; /* 优雅的内边距 */
  background-color: transparent; /* 确保无背景图 */
  min-height: 70vh;
}

/* === 顶部区域：左右布局 === */
.top-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-start; /* 顶部对齐 */
  gap: 60px; /* 控制左右两个部分的距离，不太远也不太近 */
}

/* 左侧简介 */
.bio-content {
  flex: 1; /* 占据剩余空间 */
}

.greeting {
  font-size: 28px;
  color: #333;
  margin-top: 0;
  margin-bottom: 20px;
}

.bio-text {
  font-size: 16px;
  color: #555;
  line-height: 1.8;
  /* 如果你想让 markdown 的符号不那么突兀，可以换个稍微活泼的字体 */
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 右侧头像 */
.avatar-wrapper {
  flex-shrink: 0; /* 防止头像被挤压 */
}

.author-avatar {
  width: 160px;
  height: 160px;
  object-fit: cover;
  border-radius: 20px; /* 正方形圆角 */
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1); /* 给头像加一点淡淡的高级阴影 */
  transition: transform 0.3s ease;
}

.author-avatar:hover {
  transform: translateY(-5px); /* 鼠标悬浮时微微上浮 */
}

/* === 分割线：两端渐隐，长度略小 === */
.divider-line {
  height: 1px;
  width: 85%; /* 长度小于内容区 */
  margin: 50px auto; /* 上下外边距，同时居中 */
  /* 核心：使用线性渐变实现两端透明、中间灰色的效果 */
  background: linear-gradient(
    to right,
    rgba(200, 200, 200, 0) 0%,
    rgba(200, 200, 200, 0.8) 50%,
    rgba(200, 200, 200, 0) 100%
  );
}

/* === 底部信息卡片 === */
.card-section {
  display: flex;
  justify-content: center; /* 居中显示 */
}

.info-card {
  /* 核心：自上而下动态变弱的绿色渐变 */
  background: linear-gradient(180deg, rgba(52, 211, 153, 0.8) 0%, rgba(52, 211, 153, 0.1) 100%);
  border-radius: 16px; /* 圆角长方形 */
  padding: 30px 50px; /* 始终保持可见的内边距 */
  min-width: 300px;
  box-shadow: 0 8px 20px rgba(52, 211, 153, 0.15); /* 呼应绿色的阴影 */
  border: 1px solid rgba(52, 211, 153, 0.2); /* 极淡的边框增强质感 */
}

/* 卡片内部文字排版 */
.card-item {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #2c3e50;
}

.card-item:not(:last-child) {
  margin-bottom: 16px; /* 行与行的间距 */
}

.card-item .label {
  font-weight: bold;
  margin-right: 10px;
  color: #1a4f3b; /* 深绿色，增强对比度 */
}

.card-item .value {
  font-weight: 500;
}

.empty-text {
  color: #888;
  font-style: italic;
  font-size: 15px;
}
</style>
