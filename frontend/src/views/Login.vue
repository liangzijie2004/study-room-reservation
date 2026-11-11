<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { register as registerApi } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('login')
const loading = ref(false)

// 登录表单
const loginFormRef = ref()
const loginForm = reactive({
  username: '',
  password: ''
})
const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 注册表单
const registerFormRef = ref()
const registerForm = reactive({
  username: '',
  password: '',
  realName: '',
  studentNo: '',
  phone: ''
})
const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码不少于 6 位', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

// 处理登录
async function handleLogin() {
  await loginFormRef.value.validate()
  loading.value = true
  try {
    const data = await userStore.login({ ...loginForm })
    ElMessage.success('登录成功')
    router.replace(data.user?.role === 'ADMIN' ? '/admin/dashboard' : '/student/rooms')
  } catch (e) {
    // 错误已由拦截器提示
  } finally {
    loading.value = false
  }
}

// 处理注册
async function handleRegister() {
  await registerFormRef.value.validate()
  loading.value = true
  try {
    await registerApi({ ...registerForm })
    ElMessage.success('注册成功，请登录')
    // 注册字段带入登录表单
    loginForm.username = registerForm.username
    loginForm.password = ''
    activeTab.value = 'login'
  } catch (e) {
    // 错误已由拦截器提示
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-banner">
      <div class="banner-content">
        <el-icon :size="52" class="banner-icon"><School /></el-icon>
        <h1>校园共享自习室</h1>
        <h2>座位预约系统</h2>
        <p>在线选座 · 高效自习 · 智慧管理</p>
      </div>
    </div>

    <div class="login-panel">
      <div class="panel-card">
        <el-tabs v-model="activeTab" stretch class="login-tabs">
          <el-tab-pane label="登录" name="login">
            <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large"
              @keyup.enter="handleLogin">
              <el-form-item prop="username">
                <el-input v-model="loginForm.username" placeholder="请输入用户名" clearable>
                  <template #prefix><el-icon><User /></el-icon></template>
                </el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"
                  show-password clearable>
                  <template #prefix><el-icon><Lock /></el-icon></template>
                </el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" class="submit-btn" :loading="loading" @click="handleLogin">
                  登 录
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="注册" name="register">
            <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" size="large"
              label-position="top" class="register-form">
              <el-form-item prop="username">
                <el-input v-model="registerForm.username" placeholder="用户名" clearable>
                  <template #prefix><el-icon><User /></el-icon></template>
                </el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="registerForm.password" type="password" placeholder="密码（不少于 6 位）"
                  show-password clearable>
                  <template #prefix><el-icon><Lock /></el-icon></template>
                </el-input>
              </el-form-item>
              <el-form-item prop="realName">
                <el-input v-model="registerForm.realName" placeholder="真实姓名" clearable>
                  <template #prefix><el-icon><Postcard /></el-icon></template>
                </el-input>
              </el-form-item>
              <el-form-item prop="studentNo">
                <el-input v-model="registerForm.studentNo" placeholder="学号" clearable>
                  <template #prefix><el-icon><Ticket /></el-icon></template>
                </el-input>
              </el-form-item>
              <el-form-item prop="phone">
                <el-input v-model="registerForm.phone" placeholder="手机号" clearable>
                  <template #prefix><el-icon><Iphone /></el-icon></template>
                </el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" class="submit-btn" :loading="loading" @click="handleRegister">
                  注 册
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
      <div class="panel-footer">© 2026 校园共享自习室座位预约系统</div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  height: 100vh;
  background: linear-gradient(135deg, #e6f0fb 0%, #f5f7fa 100%);
}

.login-banner {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #409eff 0%, #2b6fd6 100%);
  color: #fff;
}

.banner-content {
  text-align: center;
  padding: 40px;
}

.banner-icon {
  margin-bottom: 16px;
}

.banner-content h1 {
  font-size: 34px;
  margin: 0 0 8px;
  letter-spacing: 2px;
}

.banner-content h2 {
  font-size: 22px;
  font-weight: 400;
  margin: 0 0 20px;
  opacity: 0.9;
}

.banner-content p {
  font-size: 15px;
  opacity: 0.85;
  letter-spacing: 3px;
}

.login-panel {
  width: 460px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.panel-card {
  width: 100%;
  background: #fff;
  border-radius: 12px;
  padding: 28px 32px 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
}

.login-tabs :deep(.el-tabs__item) {
  font-size: 16px;
}

.register-form {
  max-height: 420px;
  overflow-y: auto;
  padding-right: 4px;
}

.submit-btn {
  width: 100%;
  letter-spacing: 6px;
}

.panel-footer {
  margin-top: 20px;
  font-size: 12px;
  color: #909399;
}

@media (max-width: 900px) {
  .login-banner {
    display: none;
  }

  .login-panel {
    width: 100%;
  }
}
</style>