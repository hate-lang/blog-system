<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2 class="title">博客后台登录</h2>
        <p class="subtitle">欢迎回来，请登录您的账号</p>
      </div>

      <el-form
        ref="formRef"
        :model="loginForm"
        :rules="rules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="submit-btn"
            size="large"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
})

const rules = reactive({
  username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
})

const handleLogin = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await fetch('http://localhost:8080/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            username: loginForm.username,
            password: loginForm.password,
          }),
        })

        const res = await response.json()

        if (res.code === 200) {
          ElMessage.success('登录成功')
          localStorage.setItem('token', res.data)
          router.push('/admin')
        } else {
          ElMessage.error(res.message || '登录失败，请检查账号密码')
        }
      } catch (error) {
        console.error('登录请求报错详情：', error) // 只要用到了，报错就消失了
        ElMessage.error('网络请求异常，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
/* 容器：锁定视口，防止雾气动画导致出现滚动条 */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  /* 基础底色：清晨雨后的微灰白色，带极浅的绿意 */
  background-color: #f2f7f4;
  position: relative;
  overflow: hidden;
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  z-index: 1;
}

/* 顶部伪元素：实现绿色映射的微光垂落 */
.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 50vh;
  /* 从浅绿渐变到透明 */
  background: linear-gradient(180deg, rgba(162, 217, 175, 0.15) 0%, rgba(242, 247, 244, 0) 100%);
  z-index: -2;
}

/* 底部伪元素：制造如烟如雾的动态绿意 */
.login-container::after {
  content: '';
  position: absolute;
  bottom: -15vh;
  left: -20vw;
  width: 140vw;
  height: 90vh;
  /* 画出三个不同浓度、不同大小的绿色光斑 */
  background:
    radial-gradient(ellipse at center, rgba(121, 192, 136, 0.45) 0%, transparent 55%),
    radial-gradient(ellipse at center, rgba(163, 218, 176, 0.35) 0%, transparent 60%),
    radial-gradient(ellipse at center, rgba(142, 204, 155, 0.5) 0%, transparent 65%);
  background-size:
    70vw 70vh,
    50vw 60vh,
    90vw 50vh;
  background-repeat: no-repeat;
  /* 核心魔法：70px的高斯模糊让光斑变成边缘消失的雾气 */
  filter: blur(70px);
  opacity: 0.8;
  z-index: -1;
  /* 让雾气极其缓慢地游动，周期20秒 */
  animation: morningMist 20s ease-in-out infinite alternate;
}

/* 动画：控制三个绿色光斑的位置产生随机的流转交错感 */
@keyframes morningMist {
  0% {
    background-position:
      10vw 100vh,
      80vw 90vh,
      40vw 110vh;
  }
  50% {
    background-position:
      25vw 70vh,
      60vw 75vh,
      20vw 85vh;
  }
  100% {
    background-position:
      5vw 85vh,
      90vw 65vh,
      55vw 95vh;
  }
}

/* 登录卡片：增加了一点毛玻璃质感，让背景雾气透过来更显高级 */
.login-card {
  width: 100%;
  max-width: 400px;
  padding: 40px 35px;
  background: rgba(255, 255, 255, 0.85); /* 半透明白 */
  backdrop-filter: blur(12px); /* 磨砂玻璃效果 */
  border: 1px solid rgba(255, 255, 255, 0.6); /* 增加边缘高光 */
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  box-sizing: border-box;
  z-index: 2; /* 确保在雾气上方 */
}

.login-header {
  text-align: center;
  margin-bottom: 35px;
}

.login-header .title {
  margin: 0;
  font-size: 26px;
  color: #2c3e30; /* 标题颜色稍微偏一点点极暗的绿，呼应主题 */
  font-weight: 600;
  letter-spacing: 1px;
}

.login-header .subtitle {
  margin: 10px 0 0;
  font-size: 14px;
  color: #7b8e81; /* 副标题也带一点灰绿 */
}

.login-form {
  margin-top: 20px;
}

.submit-btn {
  width: 100%;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 2px;
  /* 配合绿意主题，按钮也稍微改成了生机勃勃的绿色渐变色系（如果不喜欢可删） */
  background: linear-gradient(135deg, #5fb375 0%, #46965a 100%);
  border: none;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #6ec083 0%, #51a366 100%);
  box-shadow: 0 4px 12px rgba(95, 179, 117, 0.3);
  transform: translateY(-1px);
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  background: rgba(255, 255, 255, 0.9);
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #8dbb99 inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #5fb375 inset !important;
}
</style>
