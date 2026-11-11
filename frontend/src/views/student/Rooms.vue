<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getRooms } from '@/api/room'

const router = useRouter()

const loading = ref(false)
const list = ref([])
const query = reactive({ page: 1, size: 8, keyword: '' })
const total = ref(0)

// 加载自习室列表
async function loadData() {
  loading.value = true
  try {
    const data = await getRooms({ ...query })
    // 兼容分页结构
    if (Array.isArray(data)) {
      list.value = data
      total.value = data.length
    } else {
      list.value = data.records || []
      total.value = data.total || 0
    }
  } finally {
    loading.value = false
  }
}

// 搜索
function handleSearch() {
  query.page = 1
  loadData()
}

// 重置
function handleReset() {
  query.keyword = ''
  query.page = 1
  loadData()
}

// 点击卡片进入预约
function goReserve(room) {
  router.push({ path: '/student/reserve', query: { roomId: room.id } })
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">自习室浏览</div>
      <div class="toolbar" style="margin-bottom: 0">
        <el-input v-model="query.keyword" placeholder="搜索自习室名称/楼栋" clearable style="width: 240px"
          @keyup.enter="handleSearch" @clear="handleSearch">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon> 搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon> 重置
        </el-button>
      </div>
    </div>

    <div v-loading="loading" class="room-list">
      <el-empty v-if="!loading && list.length === 0" description="暂无自习室" />
      <el-row :gutter="16">
        <el-col v-for="room in list" :key="room.id" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card class="room-card" shadow="hover" :body-style="{ padding: '0' }" @click="goReserve(room)">
            <div class="room-cover">
              <el-icon :size="42"><OfficeBuilding /></el-icon>
              <el-tag class="room-status" :type="room.status === 1 ? 'success' : 'info'" effect="dark" size="small">
                {{ room.status === 1 ? '开放中' : '已关闭' }}
              </el-tag>
            </div>
            <div class="room-body">
              <div class="room-name">{{ room.name }}</div>
              <div class="room-meta">
                <el-icon><Location /></el-icon>
                <span>{{ room.building }}{{ room.floor ? ' ' + room.floor + '层' : '' }} · {{ room.location || '—' }}</span>
              </div>
              <div class="room-meta">
                <el-icon><Clock /></el-icon>
                <span>{{ room.openTime || '08:00' }} - {{ room.closeTime || '21:00' }}</span>
              </div>
              <div class="room-foot">
                <span class="capacity">
                  <el-icon><Grid /></el-icon> 容量 {{ room.capacity ?? room.seatCount ?? 0 }}
                </span>
                <el-button type="primary" link>
                  去预约 <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="pagination-wrap">
      <el-pagination background layout="total, prev, pager, next" :total="total"
        :current-page="query.page" :page-size="query.size"
        @current-change="(p) => { query.page = p; loadData() }" />
    </div>
  </div>
</template>

<style scoped>
.room-list {
  min-height: 200px;
}

.room-card {
  margin-bottom: 16px;
}

.room-cover {
  height: 110px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: linear-gradient(135deg, #409eff 0%, #79bbff 100%);
  position: relative;
}

.room-status {
  position: absolute;
  top: 10px;
  right: 10px;
}

.room-body {
  padding: 14px 16px 16px;
}

.room-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 10px;
}

.room-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}

.room-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.capacity {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #606266;
}
</style>