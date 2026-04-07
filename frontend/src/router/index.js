/* import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      // 当访问 /loginView 时，加载 views 文件夹下的 LoginView.vue
      component: () => import('../views/LoginView.vue'),
    },
    {
      path: '/',
      name: 'layout',
      component: () => import('../views/LayoutView.vue'),
      redirect: '/article', // 访问 / 时自动重定向到 /article
      children: [
        {
          path: '/article',
          name: 'article',
          component: () => import('../views/ArticleManage.vue'),
        },
        {
          path: '/profile',
          name: 'profile',
          component: () => import('../views/UserProfile.vue'),
        },
      ],
    },
  ],
})

// 添加全局前置路由守卫
router.beforeEach((to, from, next) => {
  // 获取本地存储的 token
  const token = localStorage.getItem('token')

  // 如果访问的是非登录页，且没有 token，强制打回登录页
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    // 其他情况，正常放行
    next()
  }
})

export default router
 */

import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // ================== C端 前台公开路由 (不需要登录) ==================
    {
      path: '/',
      name: 'frontLayout',
      component: () => import('../views/front/FrontLayout.vue'), // 前台的壳子
      redirect: '/home',
      children: [
        {
          path: 'home',
          name: 'home',
          component: () => import('../views/front/HomeView.vue'), // 首页文章列表
        },
        {
          path: 'article/:id', // 动态路由，接收文章ID
          name: 'articleDetail',
          component: () => import('../views/front/ArticleDetail.vue'), // 文章阅读页
        },
        {
          path: 'about',
          name: 'about',
          component: () => import('../views/front/AboutView.vue'),
        },
      ],
    },

    // ================== B端 后台管理路由 (必须登录) ==================
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
    },
    {
      path: '/admin', //  后台地址全改到了 /admin 下面
      name: 'adminLayout',
      component: () => import('../views/LayoutView.vue'),
      redirect: '/admin/article',
      children: [
        {
          path: 'article',
          name: 'adminArticle',
          component: () => import('../views/ArticleManage.vue'),
        },
        // 以后还可以加 /admin/profile 等等
        {
          path: 'profile',
          name: 'profile',
          component: () => import('../views/UserProfile.vue'),
        },
      ],
    },
  ],
})

//  终极智能路由守卫 (Router Guard)
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 核心逻辑：只要你访问的网址是以 '/admin' 开头的，就必须查验 Token！
  if (to.path.startsWith('/admin') && !token) {
    ElMessage.warning('后台重地，请先登录！')
    next('/login') // 一脚踢回登录页
  } else {
    next() // 其它公开页面，或者已登录状态，全部放行！
  }
})

export default router
