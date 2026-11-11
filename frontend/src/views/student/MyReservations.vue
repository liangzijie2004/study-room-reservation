<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getMyReservations,
  cancelReservation,
  checkinReservation,
  checkoutReservation
} from '@/api/reservation'
import { reservationStatusList, reservationStatusLabel, reservationStatusType } from '@/utils/dict'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, status: null })

async function loadData() {
  loading.value = true
  try {
    const params = { page: query.page, size: query.size }
    if (query.status !== null && query.status !== '') params.status = query.status
    const data = await getMyReservations(params)
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

// 取消预约
function handleCancel(row) {
  ElMessageBox.confirm(`确定取消座位「${row.seatNo}」的预约吗？`, '提示', {
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

// 签到
async function handleCheckin(row) {
  await checkinReservation(row.id)
  ElMessage.success('签到成功')
  loadData()
}

// 签退
async function handleCheckout(row) {
  await checkoutReservation(row.id)
  ElMessage.success('签退成功')
  loadData()
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">我的预约</div>
      <div class="toolbar" style="margin-bottom: 0">
        <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 150px"
          @change="handleSearch">
          <el-option v-for="item in reservationStatusList" :key="item.value" :label="item.label"
            :value="item.value" />
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon> 查询
        </el-button>
      </div>
    </div>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="list" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="55" />
        <el-table-column prop="roomName" label="自习室" min-width="130" show-overflow-tooltip />
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button type="primary" link @click="handleCheckin(row)">签到</el-button>
              <el-button type="danger" link @click="handleCancel(row)">取消</el-button>
            </template>
            <el-button v-else-if="row.status === 1" type="success" link @click="handleCheckout(row)">
              签退
            </el-button>
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
.no-action {
  color: #c0c4cc;
}
</style>