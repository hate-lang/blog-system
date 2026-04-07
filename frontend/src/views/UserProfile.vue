<template>
  <div class="profile-container">
    <el-card class="profile-card" shadow="hover">
      <div class="card-content">
        <div class="avatar-section">
          <el-avatar :size="120" :src="userInfo.userPic || defaultAvatar" class="user-avatar" />
          <el-upload
            class="avatar-uploader"
            action="http://localhost:8080/upload"
            :headers="uploadHeaders"
            name="file"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <el-button type="primary" plain size="small" class="change-btn"> 更换头像 </el-button>
          </el-upload>
        </div>

        <div class="info-section">
          <div class="info-header">
            <h3 class="info-title">个人详细信息</h3>
            <el-button
              type="info"
              plain
              round
              size="small"
              :icon="Edit"
              class="edit-btn"
              @click="openEditDialog"
            >
              编辑
            </el-button>
          </div>

          <el-descriptions :column="1" border class="info-desc">
            <el-descriptions-item label="用户 ID">
              {{ userInfo.id }}
            </el-descriptions-item>
            <el-descriptions-item label="用户名">
              {{ userInfo.username }}
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">
              {{ userInfo.createTime }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              <el-tag v-if="userInfo.email" type="info" disable-transitions>
                {{ userInfo.email }}
              </el-tag>
              <span v-else class="text-placeholder">未设置</span>
            </el-descriptions-item>
            <el-descriptions-item label="个人简介">
              <span v-if="userInfo.userBio">{{ userInfo.userBio }}</span>
              <span v-else class="text-placeholder">这个人很懒，什么都没留下~</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑个人信息" width="450px" destroy-on-close>
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" placeholder="请输入您的邮箱" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input
            v-model="editForm.userBio"
            type="textarea"
            :rows="4"
            placeholder="写点什么介绍一下自己吧..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitEditInfo"
            >确定</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue' // 引入编辑图标
import request from '../utils/request'

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 用户信息响应式对象 (新增 userBio)
const userInfo = ref({
  id: '',
  username: '',
  userPic: '',
  email: '',
  createTime: '',
  userBio: '',
})

// === 新增：弹窗与编辑相关的状态 ===
const dialogVisible = ref(false)
const submitLoading = ref(false)
const editForm = ref({
  email: '',
  userBio: '',
})

const token = localStorage.getItem('token') || ''
const uploadHeaders = {
  Authorization: token,
}

// 1. 获取用户详细信息
const fetchUserInfo = async () => {
  try {
    const res = await request.get('/user/info')
    if (res.data.code === 200) {
      userInfo.value = res.data.data
    } else {
      ElMessage.error(res.data.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络请求失败，请稍后重试')
  }
}

// 2. 头像上传前的校验
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('上传头像只能是图片格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isImage && isLt2M
}

// 3. 上传成功的回调
const handleUploadSuccess = async (response) => {
  if (response.code === 200 && response.data) {
    const newAvatarUrl = response.data
    await updateAvatar(newAvatarUrl)
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

// 4. 更新头像到数据库
const updateAvatar = async (avatarUrl) => {
  try {
    const res = await request.patch('/updateAvatar', null, {
      params: { avatarUrl },
    })

    if (res.data.code === 200) {
      ElMessage.success('头像更新成功！')
      fetchUserInfo()
    } else {
      ElMessage.error(res.data.message || '头像更新失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('头像更新请求失败')
  }
}

// === 新增：打开编辑弹窗（回显数据） ===
const openEditDialog = () => {
  editForm.value.email = userInfo.value.email || ''
  editForm.value.userBio = userInfo.value.userBio || ''
  dialogVisible.value = true
}

// === 新增：提交个人信息修改 ===
const submitEditInfo = async () => {
  submitLoading.value = true
  try {
    const res = await request.put('/user/update', {
      email: editForm.value.email,
      userBio: editForm.value.userBio,
    })

    if (res.data.code === 200) {
      ElMessage.success('信息更新成功！')
      dialogVisible.value = false
      // 更新成功后，重新拉取最新信息刷新页面
      fetchUserInfo()
    } else {
      ElMessage.error(res.data.message || '更新失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络请求失败，请稍后重试')
  } finally {
    submitLoading.value = false
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.profile-container {
  min-height: calc(100vh - 120px);
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.profile-card {
  width: 100%;
  max-width: 800px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08) !important;
}

.card-content {
  display: flex;
  align-items: flex-start;
  gap: 40px;
  padding: 20px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 160px;
}

/* 优化：头像加入丝滑的悬浮放大和阴影动效 */
.user-avatar {
  border: 4px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  cursor: pointer;
}

.user-avatar:hover {
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.change-btn {
  margin-top: 16px;
  border-radius: 20px;
}

.info-section {
  flex: 1;
}

/* 优化：标题栏 flex 布局，让编辑按钮靠右对齐 */
.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.info-title {
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

/* 优化：编辑按钮特定的浅灰色调 */
.edit-btn {
  background-color: #f4f4f5;
  border-color: #e9e9eb;
  color: #909399;
  transition: all 0.3s;
}

.edit-btn:hover {
  background-color: #e9e9eb;
  color: #606266;
}

.text-placeholder {
  color: #909399;
  font-style: italic;
}

@media (max-width: 600px) {
  .card-content {
    flex-direction: column;
    align-items: center;
  }
  .info-section {
    width: 100%;
  }
}
</style>
