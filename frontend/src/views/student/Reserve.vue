<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAllRooms } from '@/api/room'
import { getSeatsByRoom } from '@/api/seat'
import {
  getSlots,
  getSeatStatus,
  createReservation
} from '@/api/reservation'
import { formatDate } from '@/utils/format'

const route = useRoute()

const roomList = ref([])
const slots = ref([])
const seats = ref([])
const occupiedSeatNos = ref([])
const selectedSeat = ref(null)
const loadingSeats = ref(false)
const submitting = ref(false)

const form = reactive({
  roomId: null,
  reserveDate: formatDate(new Date()),
  slotIndex: null
})

// 今日日期，禁用过去日期
const today = formatDate(new Date())

const currentSlot = computed(() =>
  form.slotIndex === null ? null : slots.value[form.slotIndex]
)

// 将时段统一为 { label, startTime, endTime }
function normalizeSlot(item) {
  if (typeof item === 'string') {
    const [startTime, endTime] = item.split('-')
    return { label: item, startTime, endTime }
  }
  return {
    label: item.label || `${item.startTime}-${item.endTime}`,
    startTime: item.startTime,
    endTime: item.endTime
  }
}

// 座位图：按 rowIndex / colIndex 构建网格
const rowKeys = computed(() =>
  [...new Set(seats.value.map((s) => s.rowIndex))].sort((a, b) => a - b)
)
const colKeys = computed(() =>
  [...new Set(seats.value.map((s) => s.colIndex))].sort((a, b) => a - b)
)
const gridStyle = computed(() => ({
  gridTemplateColumns: `repeat(${colKeys.value.length || 1}, 44px)`
}))

// 已占用座位号集合
const occupiedSet = computed(() => new Set(occupiedSeatNos.value.map(String)))

// 判断座位状态：occupied / maintenance / available
function seatState(seat) {
  if (occupiedSet.value.has(String(seat.seatNo))) return 'occupied'
  if (seat.status === 0) return 'maintenance'
  return 'available'
}

function seatClass(seat) {
  if (selectedSeat.value && selectedSeat.value.id === seat.id) return 'seat-selected'
  const state = seatState(seat)
  if (state === 'occupied') return 'seat-occupied'
  if (state === 'maintenance') return 'seat-maintenance'
  return 'seat-available'
}

// 点击座位
function onSeatClick(seat) {
  const state = seatState(seat)
  if (state !== 'available') return
  selectedSeat.value = seat.id === selectedSeat.value?.id ? null : seat
}

// 加载时段
async function loadSlots() {
  const data = await getSlots()
  slots.value = (data || []).map(normalizeSlot)
}

// 加载自习室下拉
async function loadRooms() {
  const data = await getAllRooms()
  roomList.value = Array.isArray(data) ? data : data?.records || []
}

// 加载座位与占用情况
async function loadSeats() {
  if (!form.roomId || !currentSlot.value) {
    seats.value = []
    return
  }
  loadingSeats.value = true
  selectedSeat.value = null
  try {
    const [seatData, statusData] = await Promise.all([
      getSeatsByRoom(form.roomId),
      getSeatStatus({
        roomId: form.roomId,
        date: form.reserveDate,
        startTime: currentSlot.value.startTime,
        endTime: currentSlot.value.endTime
      })
    ])
    seats.value = Array.isArray(seatData) ? seatData : seatData?.records || []
    // 已占用座位号数组
    occupiedSeatNos.value = Array.isArray(statusData) ? statusData : statusData?.seats || []
  } catch (e) {
    seats.value = []
  } finally {
    loadingSeats.value = false
  }
}

// 确认预约
async function handleConfirm() {
  if (!form.roomId) return ElMessage.warning('请选择自习室')
  if (!form.reserveDate) return ElMessage.warning('请选择预约日期')
  if (!currentSlot.value) return ElMessage.warning('请选择预约时段')
  if (!selectedSeat.value) return ElMessage.warning('请选择座位')

  submitting.value = true
  try {
    await createReservation({
      roomId: form.roomId,
      seatId: selectedSeat.value.id,
      reserveDate: form.reserveDate,
      startTime: currentSlot.value.startTime,
      endTime: currentSlot.value.endTime
    })
    ElMessage.success('预约成功')
    // 刷新占用状态
    await loadSeats()
  } catch (e) {
    // 冲突等错误已由拦截器提示 msg
  } finally {
    submitting.value = false
  }
}

// 选择变化后重新加载座位
watch(
  () => [form.roomId, form.reserveDate, form.slotIndex],
  () => {
    loadSeats()
  }
)

onMounted(async () => {
  await Promise.all([loadRooms(), loadSlots()])
  // 支持从自习室浏览页携带 roomId 进入
  const qRoomId = Number(route.query.roomId)
  if (qRoomId) {
    form.roomId = qRoomId
  }
})
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">座位预约</div>
    </div>

    <el-card shadow="never" class="filter-card">
      <el-form :inline="true">
        <el-form-item label="自习室">
          <el-select v-model="form.roomId" placeholder="请选择自习室" style="width: 220px" clearable>
            <el-option v-for="room in roomList" :key="room.id" :label="room.name" :value="room.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="form.reserveDate" type="date" value-format="YYYY-MM-DD"
            placeholder="选择日期" :disabled-date="(d) => formatDate(d) < today" style="width: 180px" />
        </el-form-item>
        <el-form-item label="时段">
          <el-radio-group v-model="form.slotIndex">
            <el-radio-button v-for="(slot, index) in slots" :key="index" :value="index">
              {{ slot.label }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="seat-card" v-loading="loadingSeats">
      <template #header>
        <div class="seat-header">
          <span>座位图</span>
          <div class="seat-legend">
            <span><i class="legend-dot" style="background:#ecf5ff;border:1px solid #a0cfff"></i>可用</span>
            <span><i class="legend-dot" style="background:#fef0f0;border:1px solid #fab6b6"></i>已占用</span>
            <span><i class="legend-dot" style="background:#f4f4f5;border:1px solid #dcdfe6"></i>维护中</span>
            <span><i class="legend-dot" style="background:#409eff"></i>已选</span>
          </div>
        </div>
      </template>

      <el-empty v-if="!form.roomId" description="请选择自习室" />
      <el-empty v-else-if="!loadingSeats && seats.length === 0" description="该自习室暂无座位" />
      <div v-else class="seat-grid" :style="gridStyle">
        <template v-for="row in rowKeys" :key="row">
          <template v-for="col in colKeys" :key="`${row}-${col}`">
            <div v-if="seats.find((s) => s.rowIndex === row && s.colIndex === col)"
              class="seat-cell" :class="seatClass(seats.find((s) => s.rowIndex === row && s.colIndex === col))"
              :title="seats.find((s) => s.rowIndex === row && s.colIndex === col).seatNo"
              @click="onSeatClick(seats.find((s) => s.rowIndex === row && s.colIndex === col))">
              {{ seats.find((s) => s.rowIndex === row && s.colIndex === col).seatNo }}
            </div>
            <div v-else class="seat-cell" style="border: none"></div>
          </template>
        </template>
      </div>
    </el-card>

    <el-card shadow="never" class="confirm-card">
      <div class="confirm-bar">
        <div class="confirm-info">
          <el-icon><Select /></el-icon>
          <span v-if="selectedSeat">
            已选座位：<b>{{ selectedSeat.seatNo }}</b>（区域：{{ selectedSeat.area || '—' }}）
          </span>
          <span v-else>尚未选择座位</span>
        </div>
        <el-button type="primary" :loading="submitting" @click="handleConfirm">
          确认预约
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.filter-card {
  margin-bottom: 16px;
}

.filter-card :deep(.el-form-item) {
  margin-bottom: 0;
}

.seat-card {
  margin-bottom: 16px;
  min-height: 200px;
}

.seat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.seat-header .seat-legend {
  margin: 0;
}

.confirm-card :deep(.el-card__body) {
  padding: 16px 20px;
}

.confirm-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.confirm-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  font-size: 14px;
}

.confirm-info b {
  color: #409eff;
}
</style>