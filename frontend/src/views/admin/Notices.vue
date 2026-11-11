<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getNotices,
  createNotice,
  updateNotice,
  deleteNotice
} from '@/api/notice'
import { formatDateTime } from '@/utils/format'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10 })

// 新增/编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('发布公告')
const editingId = ref(null)
const formRef = ref()
const form = reactive({ title: '', content: '' })
const rules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const data = await getNotices({ ...query })
    list.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  editingId.value = null
  dialogTitle.value = '发布公告'
  form.title = ''
  form.content = ''
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

function handleEdit(row) {
  editingId.value = row.id
  dialogTitle.value = '编辑公告'
  form.title = row.title
  form.content = row.content
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

async function handleSubmit() {
  await formRef.value.validate()
  if (editingId.value) {
    await updateNotice(editingId.value, { ...form })
    ElMessage.success('修改成功')
  } else {
    await createNotice({ ...form })
    ElMessage.success('发布成功')
  }
  dialogVisible.value = false
  loadData()
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除公告「${row.title}」吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
    .then(async () => {
      await deleteNotice(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">公告管理</div>
      <el-button type="success" @click="handleAdd">
        <el-icon><Plus /></el-icon> 发布公告
      </el-button>
    </div>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="list" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="createTime" label="发布时间" width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="total"
          :current-page="query.page" :page-size="query.size"
          @current-change="(p) => { query.page = p; loadData() }" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="70px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入公告内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>