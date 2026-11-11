<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { updateProfile, updatePassword } from '@/api/user'

const userStore = useUserStore()

const profileForm = reactive({
  realName: '',
  phone: '',
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRef = ref()
const passwordRef = ref()
const profileLoading = ref(false)
const passwordLoading = ref(false)

const profileRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码不少于 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, cb) => {
        if (value !== passwordForm.newPassword) cb(new Error('两次输入的密码不一致'))
        else cb()
      },
      trigger: 'blur'
    }
  ]
}

// 填充当前用户信息
function fillForm() {
  profileForm.realName = userStore.user?.realName || ''
  profileForm.phone = userStore.user?.phone || ''
  profileForm.avatar = userStore.user?.avatar || ''
}

// 保存资料
async function handleProfile() {
  await profileRef.value.validate()
  profileLoading.value = true
  try {
    await updateProfile({ ...profileForm })
    // 同步本地用户信息
    userStore.setUser({ ...userStore.user, ...profileForm })
    ElMessage.success('资料已更新')
  } finally {
    profileLoading.value = false
  }
}

// 修改密码
async function handlePassword() {
  await passwordRef.value.validate()
  passwordLoading.value = true
  try {
    await updatePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    passwordRef.value.resetFields()
  } finally {
    passwordLoading.value = false
  }
}

onMounted(fillForm)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">个人中心</div>
    </div>

    <el-row :gutter="16">
      <el-col :xs="24" :md="9">
        <el-card shadow="never" class="info-card">
          <div class="user-avatar">
            <el-avatar :size="88" :src="userStore.user?.avatar">
              <el-icon :size="40"><UserFilled /></el-icon>
            </el-avatar>
            <div class="user-name">{{ userStore.user?.realName || userStore.user?.username }}</div>
            <el-tag type="success" effect="light">{{ userStore.user?.role === 'ADMIN' ? '管理员' : '学生' }}</el-tag>
          </div>
          <el-divider />
          <div class="info-line">
            <span class="info-label">用户名</span>
            <span>{{ userStore.user?.username }}</span>
          </div>
          <div class="info-line">
            <span class="info-label">学号</span>
            <span>{{ userStore.user?.studentNo || '—' }}</span>
          </div>
          <div class="info-line">
            <span class="info-label">手机号</span>
            <span>{{ userStore.user?.phone || '—' }}</span>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="15">
        <el-card shadow="never" class="form-card">
          <template #header><span class="card-title">修改资料</span></template>
          <el-form ref="profileRef" :model="profileForm" :rules="profileRules" label-width="90px">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="头像地址" prop="avatar">
              <el-input v-model="profileForm.avatar" placeholder="请输入头像图片 URL（可选）" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="profileLoading" @click="handleProfile">保存资料</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never" class="form-card">
          <template #header><span class="card-title">修改密码</span></template>
          <el-form ref="passwordRef" :model="passwordForm" :rules="passwordRules" label-width="90px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password
                placeholder="请输入原密码" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password
                placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password
                placeholder="请再次输入新密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="passwordLoading" @click="handlePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.info-card,
.form-card {
  margin-bottom: 16px;
}

.user-avatar {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
}

.user-name {
  font-size: 18px;
  font-weight: 600;
}

.info-line {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  font-size: 14px;
  color: #303133;
}

.info-label {
  color: #909399;
}

.card-title {
  font-weight: 600;
}
</style>