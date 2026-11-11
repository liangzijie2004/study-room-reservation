<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 当前激活菜单
const activeMenu = computed(() => route.path)
const username = computed(() => userStore.user?.realName || userStore.user?.username || '用户')

// 退出登录
function handleLogout() {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      userStore.logout()
      ElMessage.success('已退出登录')
      router.replace('/login')
    })
    .catch(() => {})
}
</script>

<template>
  <el-container class="layout">
    <el-aside width="210px" class="aside">
      <div class="logo">
        <el-icon :size="22"><School /></el-icon>
        <span>自习室预约</span>
      </div>
      <el-menu :default-active="activeMenu" router class="menu" background-color="#1f2d3d"
        text-color="#c0c4cc" active-text-color="#ffffff">
        <el-menu-item index="/student/rooms">
          <el-icon><OfficeBuilding /></el-icon>
          <span>自习室浏览</span>
        </el-menu-item>
        <el-menu-item index="/student/reserve">
          <el-icon><Calendar /></el-icon>
          <span>座位预约</span>
        </el-menu-item>
        <el-menu-item index="/student/my-reservations">
          <el-icon><List /></el-icon>
          <span>我的预约</span>
        </el-menu-item>
        <el-menu-item index="/student/notices">
          <el-icon><Bell /></el-icon>
          <span>公告</span>
        </el-menu-item>
        <el-menu-item index="/student/profile">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-title">{{ route.meta.title }}</div>
        <div class="header-right">
          <el-tag type="success" effect="plain" class="role-tag">学生</el-tag>
          <el-dropdown @command="(cmd) => cmd === 'logout' && handleLogout()">
            <span class="user-info">
              <el-avatar :size="30" :src="userStore.user?.avatar">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <span class="username">{{ username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/student/profile')">
                  <el-icon><Setting /></el-icon> 个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout {
  height: 100vh;
}

.aside {
  background-color: #1f2d3d;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 17px;
  font-weight: 600;
  letter-spacing: 1px;
}

.menu {
  border-right: none;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.role-tag {
  font-weight: 500;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}

.username {
  font-size: 14px;
  color: #303133;
}

.main {
  background-color: #f0f2f5;
  padding: 0;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>