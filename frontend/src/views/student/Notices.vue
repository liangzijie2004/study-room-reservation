<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getNotices, getNotice } from '@/api/notice'
import { formatDateTime } from '@/utils/format'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10 })

const dialogVisible = ref(false)
const current = ref(null)
const detailLoading = ref(false)

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

// 查看详情
async function handleView(row) {
  dialogVisible.value = true
  detailLoading.value = true
  current.value = row
  try {
    const data = await getNotice(row.id)
    current.value = data || row
  } finally {
    detailLoading.value = false
  }
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">公告</div>
    </div>

    <el-card shadow="never">
      <div v-loading="loading">
        <el-empty v-if="!loading && list.length === 0" description="暂无公告" />
        <div v-for="item in list" :key="item.id" class="notice-item" @click="handleView(item)">
          <div class="notice-icon">
            <el-icon :size="20"><Bell /></el-icon>
          </div>
          <div class="notice-main">
            <div class="notice-title">{{ item.title }}</div>
            <div class="notice-time">
              <el-icon><Clock /></el-icon> {{ formatDateTime(item.createTime) }}
            </div>
          </div>
          <el-icon class="notice-arrow"><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="total"
          :current-page="query.page" :page-size="query.size"
          @current-change="(p) => { query.page = p; loadData() }" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="current?.title" width="600px">
      <div v-loading="detailLoading" class="notice-detail">
        <div class="notice-detail-time">
          <el-icon><Clock /></el-icon> 发布时间：{{ formatDateTime(current?.createTime) }}
        </div>
        <el-divider />
        <div class="notice-detail-content">{{ current?.content }}</div>
      </div>
      <template #footer>
        <el-button type="primary" @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.notice-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 8px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.notice-item:hover {
  background-color: #f5f7fa;
}

.notice-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #ecf5ff;
  color: #409eff;
  flex-shrink: 0;
}

.notice-main {
  flex: 1;
  min-width: 0;
}

.notice-title {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 6px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.notice-arrow {
  color: #c0c4cc;
}

.notice-detail-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
}

.notice-detail-content {
  min-height: 120px;
  line-height: 1.8;
  color: #303133;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>