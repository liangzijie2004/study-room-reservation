<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllRooms } from '@/api/room'
import { getSeatsByRoom, updateSeat, deleteSeat } from '@/api/seat'
import { SEAT_STATUS } from '@/utils/dict'

const roomList = ref([])
const selectedRoomId = ref(null)
const loading = ref(false)
const list = ref([])

// 编辑弹窗
const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({ id: null, seatNo: '', area: '', status: 1 })
const rules = {
  seatNo: [{ required: true, message: '请输入座位号', trigger: 'blur' }],
  area: [{ required: true, message: '请输入区域', trigger: 'blur' }]
}

async function loadRooms() {
  const data = await getAllRooms()
  roomList.value = Array.isArray(data) ? data : data?.records || []
  if (roomList.value.length && !selectedRoomId.value) {
    selectedRoomId.value = roomList.value[0].id
    loadSeats()
  }
}

async function loadSeats() {
  if (!selectedRoomId.value) {
    list.value = []
    return
  }
  loading.value = true
  try {
    const data = await getSeatsByRoom(selectedRoomId.value)
    list.value = Array.isArray(data) ? data : data?.records || []
  } finally {
    loading.value = false
  }
}

function handleRoomChange() {
  loadSeats()
}

function handleEdit(row) {
  form.id = row.id
  form.seatNo = row.seatNo
  form.area = row.area
  form.status = row.status
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

async function handleSubmit() {
  await formRef.value.validate()
  await updateSeat(form.id, { seatNo: form.seatNo, area: form.area, status: form.status })
  ElMessage.success('修改成功')
  dialogVisible.value = false
  loadSeats()
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除座位「${row.seatNo}」吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
    .then(async () => {
      await deleteSeat(row.id)
      ElMessage.success('删除成功')
      loadSeats()
    })
    .catch(() => {})
}

onMounted(loadRooms)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">座位管理</div>
      <div class="toolbar" style="margin-bottom: 0">
        <span class="toolbar-label">选择自习室：</span>
        <el-select v-model="selectedRoomId" placeholder="请选择自习室" style="width: 240px"
          @change="handleRoomChange">
          <el-option v-for="room in roomList" :key="room.id" :label="room.name" :value="room.id" />
        </el-select>
        <el-button @click="loadSeats">
          <el-icon><Refresh /></el-icon> 刷新
        </el-button>
      </div>
    </div>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="list" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="55" />
        <el-table-column prop="seatNo" label="座位号" width="120" />
        <el-table-column prop="area" label="区域" width="120" />
        <el-table-column prop="rowIndex" label="行" width="80" />
        <el-table-column prop="colIndex" label="列" width="80" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="SEAT_STATUS[row.status]?.type || 'info'" effect="light">
              {{ SEAT_STATUS[row.status]?.label || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑座位" width="440px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="座位号" prop="seatNo">
          <el-input v-model="form.seatNo" placeholder="请输入座位号" />
        </el-form-item>
        <el-form-item label="区域" prop="area">
          <el-input v-model="form.area" placeholder="请输入区域" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">可用</el-radio>
            <el-radio :value="0">维护中</el-radio>
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

<style scoped>
.toolbar-label {
  color: #606266;
  font-size: 14px;
}
</style>