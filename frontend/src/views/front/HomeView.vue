<template>
  <div
    class="home-container"
    v-infinite-scroll="loadMore"
    :infinite-scroll-disabled="isDisabled"
    :infinite-scroll-distance="50"
    :infinite-scroll-immediate="true"
  >
    <div class="card-grid">
      <div
        v-for="(article, index) in articles"
        :key="article.id"
        class="article-card"
        :style="getCardStyle(article, index)"
      >
        <div class="card-overlay">
          <div class="title-wrapper" @click="handleNavigate(article.id)">
            <h2 class="card-title">
              {{ article.title }}
              <el-icon class="title-arrow"><Right /></el-icon>
            </h2>
          </div>

          <p class="card-summary">
            {{ article.summary || '作者很懒，没有留下任何摘要...' }}
          </p>
        </div>
      </div>
    </div>

    <footer class="loading-status">
      <el-icon v-if="loading" class="is-loading"><Loading /></el-icon>
      <span v-if="loading">正在加载更多文章...</span>
      <span v-if="finished" class="finished-text">—— 已经到底啦，去看看别的吧 ——</span>
    </footer>

    <el-backtop target=".home-container" :right="40" :bottom="40" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Loading, Right } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const articles = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const finished = ref(false)

const DEFAULT_COVER =
  'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80'

const isDisabled = computed(() => loading.value || finished.value)

const loadMore = async () => {
  if (isDisabled.value) return
  loading.value = true
  try {
    const { data: res } = await axios.get('http://localhost:8080/article/public/list', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value },
    })
    if (res.code === 200) {
      const { records, pages } = res.data
      articles.value.push(...records)
      if (pageNum.value >= pages) finished.value = true
      else pageNum.value++
    }
  } catch (error) {
    console.error('获取文章失败:', error)
  } finally {
    loading.value = false
  }
}

const getCardStyle = (article, index) => ({
  backgroundImage: `url(${article.coverImg || DEFAULT_COVER})`,
  animationDelay: `${(index % 10) * 0.1}s`,
})

const handleNavigate = (id) => {
  router.push(`/article/${id}`)
}
</script>

<style scoped>
.home-container {
  padding: 24px;
  height: 100vh;
  overflow-y: auto;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

/* === 卡片主体 === */
.article-card {
  height: 320px;
  border-radius: 16px;
  background-size: cover;
  background-position: center;
  position: relative;
  overflow: visible; /* 确保外扩散阴影可见 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  opacity: 0;
  transform: translateY(40px);
  animation: slideUpFade 0.7s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
  transition: all 0.4s ease;
}

/* 鼠标移动到整张卡片：仅触发外扩散灰色边框和轻微上浮 */
.article-card:hover {
  transform: translateY(-4px);
  box-shadow:
    0 0 0 6px rgba(200, 200, 200, 0.3),
    0 12px 24px rgba(0, 0, 0, 0.15);
}

@keyframes slideUpFade {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-overlay {
  position: absolute;
  inset: 0;
  border-radius: 16px;
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.5) 0%,
    transparent 50%,
    rgba(0, 0, 0, 0.7) 100%
  );
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 24px;
}

/* === 标题包装层：控制局部触发范围 === */
.title-wrapper {
  display: inline-block; /* 仅包裹内容宽度 */
  cursor: pointer;
  transition: transform 0.3s ease;
  transform-origin: left center;
}

.card-title {
  color: #ffffff;
  font-size: 22px;
  font-weight: 600;
  margin: 0;
  line-height: 1.4;
  display: inline;
  word-break: break-all;
  transition: color 0.3s ease;
}

.title-arrow {
  display: inline-flex;
  vertical-align: middle;
  margin-left: 8px;
  font-size: 18px;
  color: #ffffff;
  transition: all 0.3s ease;
}

/* === 关键修改：仅当鼠标滑过 title-wrapper 时触发放大和变色 === */
.title-wrapper:hover {
  transform: scale(1.05); /* 标题和箭头同步放大 5% */
}

.title-wrapper:hover .card-title {
  color: #34d399; /* 翠绿色 */
}

.title-wrapper:hover .title-arrow {
  color: #34d399;
  transform: translateX(8px); /* 放大同时保持向右位移 */
}

.card-summary {
  color: #e5e7eb;
  font-size: 14px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  pointer-events: none; /* 防止摘要干扰标题的 hover 判定 */
}

.loading-status {
  text-align: center;
  padding: 30px 0;
  color: #909399;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
</style>
