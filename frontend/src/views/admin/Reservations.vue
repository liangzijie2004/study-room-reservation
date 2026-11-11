<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllReservations, cancelReservation } from '@/api/reservation'
import { reservationStatusList, reservationStatusLabel, reservationStatusType } from '@/utils/dict'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, status: null, keyword: '', date: '' })

async function loadData() {
  loading.value = true
  try {
    const params = { page: query.page, size: query.size }
    if (query.status !== null && query.status !== '') params.status = query.status
    if (query.keyword) params.keyword = query.keyword
    if (query.date) params.date = query.date
    const data = await getAllReservations(params)
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

function handleReset() {
  query.status = null
  query.keyword = ''
  query.date = ''
  query.page = 1
  loadData()
}

// 取消预约
function handleCancel(row) {
  ElMessageBox.confirm(`确定取消「${row.userName}」在「${row.roomName}」的预约吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
    .then(async () => {
      await cancelReservation(row.id)
      ElMessage.success('已取消预约')
      loadData()
    })
    .catch(() => {})
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">预约管理</div>
    </div>

    <el-card shadow="never" class="filter-card">
      <div class="toolbar" style="margin-bottom: 0">
        <el-input v-model="query.keyword" placeholder="搜索用户名/自习室" clearable style="width: 220px"
          @keyup.enter="handleSearch" @clear="handleSearch">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 140px"
          @change="handleSearch">
          <el-option v-for="item in reservationStatusList" :key="item.value" :label="item.label"
            :value="item.value" />
        </el-select>
        <el-date-picker v-model="query.date" type="date" value-format="YYYY-MM-DD" placeholder="预约日期"
          style="width: 180px" @change="handleSearch" />
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon> 查询
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon> 重置
        </el-button>
      </div>
    </el-card>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="list" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="userName" label="预约人" min-width="100" show-overflow-tooltip />
        <el-table-column prop="roomName" label="自习室" min-width="120" show-overflow-tooltip />
        <el-table-column prop="seatNo" label="座位号" width="100" />
        <el-table-column prop="reserveDate" label="预约日期" width="120" />
        <el-table-column label="时段" width="150">
          <template #default="{ row }">{{ row.startTime }}-{{ row.endTime }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="reservationStatusType(row.status)" effect="light">
              {{ reservationStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" show-overflow-tooltip />
        <el-table-column label="操作" width="110" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0 || row.status === 1" type="danger" link
              @click="handleCancel(row)">取消</el-button>
            <span v-else class="no-action">—</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next" :total="total"
          :current-page="query.page" :page-size="query.size"
          @current-change="(p) => { query.page = p; loadData() }" />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.filter-card {
  margin-bottom: 16px;
}

.no-action {
  color: #c0c4cc;
}
</style>