<template>
  <div class="article-detail-container" v-loading="loading">
    <div class="article-wrapper" v-if="article.title">
      <h1 class="article-title">{{ article.title }}</h1>

      <div class="article-meta">发布时间：{{ formatTime(article.createTime) }}</div>

      <div class="article-content" v-html="formatContent(article.content)"></div>
    </div>

    <el-empty v-if="!loading && !article.title" description="抱歉，文章不见了~" />
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

// 【新增1】引入 highlight.js 核心库
import hljs from 'highlight.js'
import { markedHighlight } from 'marked-highlight'
import 'highlight.js/styles/github.css' // 选择一个喜欢的代码高亮主题

// 1. 获取当前路由信息
const route = useRoute()

// 2. 响应式数据定义
const article = ref({})
const loading = ref(false)

// ================== 【关键修改区】 ==================
// 使用最新的 marked.use() 语法注入高亮插件
marked.use(
  markedHighlight({
    // 注意这里：必须加上 hljs 前缀，这是 CSS 样式生效的灵魂！
    langPrefix: 'hljs language-',
    highlight(code, lang) {
      const language = hljs.getLanguage(lang) ? lang : 'plaintext'
      return hljs.highlight(code, { language }).value
    },
  }),
)
// 3. 核心：发起网络请求获取详情
const fetchArticleDetail = async () => {
  // 从路由中解构出传递过来的 id
  const { id } = route.params
  if (!id) return

  loading.value = true
  try {
    // 发送 GET 请求，拼上 id，无需携带 Token
    const res = await axios.get(`http://localhost:8080/article/public/detail/${id}`)

    if (res.data.code === 200) {
      article.value = res.data.data
    } else {
      ElMessage.error(res.data.message || '获取文章详情失败')
    }
  } catch (error) {
    console.error('详情请求异常:', error)
    ElMessage.error('网络开了个小差，请稍后再试')
  } finally {
    loading.value = false
  }
}

// 4. 工具函数：格式化时间 (把 T 换成空格，看着更舒服)
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ')
}

// 5. 工具函数：处理纯文本的换行和格式
// 如果后端传过来的是纯文本（没有HTML标签），我们需要把它按换行符包上 <p> 标签，方便做首行缩进
const formatContent = (text) => {
  if (!text) return ''
  // 简单判断：如果已经包含 HTML 标签（如 <p> 或 <br>），直接返回即可（以后接富文本编辑器就走这步）
  if (text.includes('<') && text.includes('>')) {
    return text
  }
  /* // 如果是纯文本，将换行符替换为段落，以配合 CSS 做首行缩进
  return text
    .split('\n')
    .map((paragraph) => `<p>${paragraph}</p>`)
    .join('') */

  // 直接使用 marked 库将纯文本转换为 HTML，自动处理换行和特殊字符

  // 第一步：将 Markdown 文本解析为不安全的 HTML 字符串
  const rawHtml = marked.parse(text)
  // 【关键修正】：加上 { ADD_ATTR: ['class'] }，防止高亮样式被安全卫士误杀！
  const cleanHtml = DOMPurify.sanitize(rawHtml, { ADD_ATTR: ['class'] })
  return cleanHtml
  //return marked.parse(text)
}

// 6. 组件挂载时触发请求
onMounted(() => {
  fetchArticleDetail()
})
</script>

<style scoped>
/* 整个详情页的外部容器，居中且限制最大宽度，阅读体验最佳 */
.article-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
  min-height: 80vh;
  background-color: #ffffff; /* 确保背景是干净的白色 */
  border-radius: 8px; /* 稍微加点圆角，呼应你的首页卡片 */
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05); /* 极淡的阴影区分背景 */
}

/* --- 标题样式 --- */
.article-title {
  text-align: center;
  font-size: 28px;
  color: #000000;
  font-weight: bold;
  margin-top: 0;
  margin-bottom: 24px;
  line-height: 1.4;
}

/* --- 时间/元数据样式 --- */
.article-meta {
  text-align: center; /* 居中与标题呼应，如果你想居左可以改成 left */
  color: #888888; /* 灰色 */
  font-size: 14px;
  margin-bottom: 50px; /* 留足边距在内容上面 */
  padding-bottom: 20px;
  border-bottom: 1px solid #eeeeee; /* 加一条极淡的分割线，更显高级 */
}

/* --- 正文内容样式 --- */
.article-content {
  color: #000000; /* 整个文章用黑色 */
  font-size: 16px;
  line-height: 2; /* 增大行高，阅读更舒适 */
}

/* 关键点：使用 :deep() 穿透作用域 
  因为 v-html 渲染出来的标签是不带 scoped data 属性的，
  必须用 :deep 才能选中并施加样式！
*/
.article-content :deep(p) {
  text-indent: 2em; /* 首行缩进两个字符 */
  margin-bottom: 20px; /* 段落之间的间距 */
  word-wrap: break-word; /* 防止长英文或代码撑破页面 */
}

/* 如果后续有图片，防止图片过大撑破容器 */
.article-content :deep(img) {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 20px auto; /* 图片居中不缩进 */
}

/* maked解析只会把# 变成<h1>，需要加上颜色变得更像博客 */
/* --- 针对 Markdown 渲染的 HTML 内容注入样式 --- */
.article-content {
  color: #2c3e50;
  font-size: 16px;
  line-height: 1.8;
}

/* 穿透作用域，给 Markdown 生成的标签写样式 */
.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3) {
  color: #1a1a1a;
  border-bottom: 1px solid #eaecef; /* 标题下方的轻微下划线，经典 GitHub 风格 */
  padding-bottom: 10px;
  margin-top: 35px;
}

.article-content :deep(p) {
  margin-bottom: 16px;
  /* 注意：标准的 Markdown 段落通常不使用首行缩进，而是靠段落间距区分 */
}

.article-content :deep(strong) {
  font-weight: 600;
  color: #34d399; /* 把加粗的字体变成你的主题翠绿色，非常酷！ */
}

/* 简单的代码块背景 */
.article-content :deep(pre) {
  background-color: #f6f8fa;
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
}
.article-content :deep(code) {
  font-family: 'Courier New', Courier, monospace;
}
</style>
