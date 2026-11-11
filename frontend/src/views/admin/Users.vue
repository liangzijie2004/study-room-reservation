<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getUsers,
  updateUserStatus,
  updateUser,
  deleteUser
} from '@/api/user'
import { roleLabel } from '@/utils/dict'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, keyword: '', role: '' })

// 编辑弹窗
const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({
  id: null,
  username: '',
  realName: '',
  studentNo: '',
  phone: '',
  role: 'STUDENT',
  status: 1
})
const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

async function loadData() {
  loading.value = true
  try {
    const params = { page: query.page, size: query.size }
    if (query.keyword) params.keyword = query.keyword
    if (query.role) params.role = query.role
    const data = await getUsers(params)
    list.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadData()
}

// 启用/禁用切换
async function handleStatusChange(row) {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 1 ? '启用' : '禁用'
  ElMessageBox.confirm(`确定${actionText}用户「${row.username}」吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
    .then(async () => {
      await updateUserStatus(row.id, newStatus)
      ElMessage.success(`${actionText}成功`)
      loadData()
    })
    .catch(() => {
      // 取消则回滚开关
      row.status = row.status
    })
}

function handleEdit(row) {
  Object.assign(form, {
    id: row.id,
    username: row.username,
    realName: row.realName,
    studentNo: row.studentNo,
    phone: row.phone,
    role: row.role,
    status: row.status
  })
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

async function handleSubmit() {
  await formRef.value.validate()
  await updateUser(form.id, {
    realName: form.realName,
    studentNo: form.studentNo,
    phone: form.phone,
    role: form.role,
    status: form.status
  })
  ElMessage.success('修改成功')
  dialogVisible.value = false
  loadData()
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除用户「${row.username}」吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
    .then(async () => {
      await deleteUser(row.id)
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
      <div class="page-title">用户管理</div>
      <div class="toolbar" style="margin-bottom: 0">
        <el-input v-model="query.keyword" placeholder="搜索用户名/姓名/学号" clearable style="width: 240px"
          @keyup.enter="handleSearch" @clear="handleSearch">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="query.role" placeholder="全部角色" clearable style="width: 140px"
          @change="handleSearch">
          <el-option label="学生" value="STUDENT" />
          <el-option label="管理员" value="ADMIN" />
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon> 查询
        </el-button>
      </div>
    </div>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="list" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="realName" label="真实姓名" min-width="110" show-overflow-tooltip />
        <el-table-column prop="studentNo" label="学号" min-width="120" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'" effect="light">
              {{ roleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0"
              @change="handleStatusChange(row)" />
          </template>
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

    <el-dialog v-model="dialogVisible" title="编辑用户" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="form.studentNo" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="学生" value="STUDENT" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>