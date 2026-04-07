<template>
  <div class="article-manage">
    <div class="toolbar">
      <el-button type="primary" @click="openDrawer('add')">新增文章</el-button>
    </div>

    <el-table :data="articleList" style="width: 100%; margin-top: 16px">
      <el-table-column prop="title" label="文章标题" min-width="180" />
      <el-table-column prop="summary" label="摘要" min-width="220" />
      <el-table-column prop="createTime" label="发布时间" min-width="160" />
      <el-table-column label="操作" min-width="120">
        <template #default="scope">
          <el-button size="small" type="primary" text @click="openDrawer('edit', scope.row)"
            >编辑</el-button
          >
          <el-button size="small" type="danger" text @click="handleDelete(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-drawer
      v-model="drawerVisible"
      :title="drawerTitle"
      size="480px"
      direction="rtl"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入文章标题" />
        </el-form-item>

        <el-form-item label="封面图" prop="coverImg">
          <el-upload
            class="avatar-uploader"
            action="http://localhost:8080/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            name="file"
          >
            <img v-if="form.coverImg" :src="form.coverImg" class="cover-preview" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="文章摘要" prop="summary">
          <el-input
            v-model="form.summary"
            type="textarea"
            :rows="3"
            maxlength="200"
            show-word-limit
            placeholder="请输入文章摘要（选填，不填则默认无）"
          />
        </el-form-item>

        <el-form-item label="文章正文" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入文章内容" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">提交</el-button>
          <el-button @click="drawerVisible = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>

    <div class="pagination-wrapper">
      <el-pagination
        background
        layout="prev, pager, next, jumper, ->, total"
        :current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '../utils/request'

const articleList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const drawerVisible = ref(false)
const drawerTitle = ref('新增文章')
const formRef = ref(null)

// 【修改点】：表单对象中增加 summary 字段
const form = reactive({
  id: null,
  title: '',
  coverImg: '',
  summary: '',
  content: '',
})

const rules = {
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
  coverImg: [{ required: true, message: '请上传封面图', trigger: 'change' }],
  // summary 是选填项，所以不需要在这里加 required 规则
  content: [{ required: true, message: '请输入文章内容', trigger: 'blur' }],
}

const token = localStorage.getItem('token') || ''

const uploadHeaders = { Authorization: token }

const fetchArticles = async (page = 1) => {
  try {
    const res = await request.get('/article/list', {
      params: {
        pageNum: page,
        pageSize: pageSize.value,
      },
    })
    articleList.value = res.data.data.records || []
    total.value = res.data.data.total || 0
    pageNum.value = page
  } catch (err) {
    console.error(err)
    ElMessage.error('获取文章列表失败')
  }
}

const openDrawer = (mode, row = null) => {
  if (mode === 'add') {
    drawerTitle.value = '新增文章'
    resetForm()
  } else if (mode === 'edit' && row) {
    drawerTitle.value = '编辑文章'
    form.id = row.id
    form.title = row.title
    form.coverImg = row.coverImg
    // 【修改点】：编辑时，把行数据中的 summary 回显到表单
    // 使用 || '' 防止后端传回 null 导致输入框显示异常
    form.summary = row.summary || ''
    form.content = row.content
  }
  drawerVisible.value = true
  nextTick(() => {
    formRef.value && formRef.value.clearValidate()
  })
}

const resetForm = () => {
  form.id = null
  form.title = ''
  form.coverImg = ''
  // 【修改点】：重置表单时清空 summary
  form.summary = ''
  form.content = ''
  formRef.value && formRef.value.clearValidate()
}

// 包含了上一轮修复的图片上传逻辑
const handleUploadSuccess = (res) => {
  if (res && res.code === 200 && res.data) {
    form.coverImg = res.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(res.message || '图片上传失败')
  }
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
  }
  return isImage
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (form.id) {
        await request.put('/article/update', form)
        ElMessage.success('编辑成功')
      } else {
        await request.post('/article/add', form)
        ElMessage.success('新增成功')
      }
      drawerVisible.value = false
      fetchArticles(1)
    } catch (err) {
      console.error(err)
      ElMessage.error('提交失败')
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除文章「${row.title}」吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  })
    .then(async () => {
      try {
        await request.delete(`/article/delete/${row.id}`)
        ElMessage.success('删除成功')
        fetchArticles(1)
      } catch (err) {
        console.error(err)
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

const handlePageChange = (page) => {
  fetchArticles(page)
}

onMounted(() => {
  fetchArticles()
})
</script>

<style scoped>
.article-manage {
  padding: 24px;
  background: #fff;
  border-radius: 8px;
}

.toolbar {
  display: flex;
  align-items: center;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.cover-preview {
  width: 120px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #eee;
  margin-bottom: 8px;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader-icon {
  font-size: 32px;
  color: #8c939d;
  width: 120px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
}
</style>
