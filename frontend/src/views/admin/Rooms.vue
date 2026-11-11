<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getRooms,
  createRoom,
  updateRoom,
  deleteRoom
} from '@/api/room'
import { batchCreateSeats } from '@/api/seat'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, keyword: '' })

// 新增/编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('新增自习室')
const editingId = ref(null)
const formRef = ref()
const form = reactive({
  name: '',
  building: '',
  floor: '',
  location: '',
  openTime: '08:00',
  closeTime: '21:00',
  capacity: 0,
  description: '',
  status: 1
})
const rules = {
  name: [{ required: true, message: '请输入自习室名称', trigger: 'blur' }],
  building: [{ required: true, message: '请输入楼栋', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容量', trigger: 'blur' }]
}

// 批量生成座位弹窗
const seatDialogVisible = ref(false)
const seatFormRef = ref()
const seatForm = reactive({ roomId: null, roomName: '', rowCount: 5, colCount: 6, area: 'A区' })
const seatRules = {
  rowCount: [{ required: true, message: '请输入行数', trigger: 'blur' }],
  colCount: [{ required: true, message: '请输入列数', trigger: 'blur' }],
  area: [{ required: true, message: '请输入区域', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const data = await getRooms({ ...query })
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

function resetForm() {
  Object.assign(form, {
    name: '',
    building: '',
    floor: '',
    location: '',
    openTime: '08:00',
    closeTime: '21:00',
    capacity: 0,
    description: '',
    status: 1
  })
}

// 打开新增
function handleAdd() {
  editingId.value = null
  dialogTitle.value = '新增自习室'
  resetForm()
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

// 打开编辑
function handleEdit(row) {
  editingId.value = row.id
  dialogTitle.value = '编辑自习室'
  Object.assign(form, {
    name: row.name,
    building: row.building,
    floor: row.floor,
    location: row.location,
    openTime: row.openTime,
    closeTime: row.closeTime,
    capacity: row.capacity,
    description: row.description,
    status: row.status
  })
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

// 提交新增/编辑
async function handleSubmit() {
  await formRef.value.validate()
  if (editingId.value) {
    await updateRoom(editingId.value, { ...form })
    ElMessage.success('修改成功')
  } else {
    await createRoom({ ...form })
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

// 删除
function handleDelete(row) {
  ElMessageBox.confirm(`确定删除自习室「${row.name}」吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })
    .then(async () => {
      await deleteRoom(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

// 打开批量生成座位
function handleBatchSeat(row) {
  seatForm.roomId = row.id
  seatForm.roomName = row.name
  seatForm.rowCount = 5
  seatForm.colCount = 6
  seatForm.area = 'A区'
  seatDialogVisible.value = true
  seatFormRef.value?.clearValidate()
}

// 提交批量生成
async function handleBatchSubmit() {
  await seatFormRef.value.validate()
  await batchCreateSeats({
    roomId: seatForm.roomId,
    rowCount: seatForm.rowCount,
    colCount: seatForm.colCount,
    area: seatForm.area
  })
  ElMessage.success('座位生成成功')
  seatDialogVisible.value = false
  loadData()
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">自习室管理</div>
      <div class="toolbar" style="margin-bottom: 0">
        <el-input v-model="query.keyword" placeholder="搜索名称/楼栋" clearable style="width: 220px"
          @keyup.enter="handleSearch" @clear="handleSearch">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon> 查询
        </el-button>
        <el-button type="success" @click="handleAdd">
          <el-icon><Plus /></el-icon> 新增自习室
        </el-button>
      </div>
    </div>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="list" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="55" />
        <el-table-column prop="name" label="名称" min-width="130" show-overflow-tooltip />
        <el-table-column prop="building" label="楼栋" width="100" />
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="location" label="位置" min-width="120" show-overflow-tooltip />
        <el-table-column label="开放时间" width="150">
          <template #default="{ row }">{{ row.openTime }} - {{ row.closeTime }}</template>
        </el-table-column>
        <el-table-column prop="capacity" label="容量" width="80" />
        <el-table-column prop="seatCount" label="座位数" width="90" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="light">
              {{ row.status === 1 ? '开放' : '关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleBatchSeat(row)">批量生成座位</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入自习室名称" />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="楼栋" prop="building">
              <el-input v-model="form.building" placeholder="如：图书馆" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="楼层">
              <el-input v-model="form.floor" placeholder="如：3" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="位置">
          <el-input v-model="form.location" placeholder="如：东侧 301" />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="开放时间">
              <el-time-picker v-model="form.openTime" format="HH:mm" value-format="HH:mm"
                placeholder="开始时间" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关闭时间">
              <el-time-picker v-model="form.closeTime" format="HH:mm" value-format="HH:mm"
                placeholder="结束时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="容量" prop="capacity">
              <el-input-number v-model="form.capacity" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-switch v-model="form.status" :active-value="1" :inactive-value="0"
                active-text="开放" inactive-text="关闭" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量生成座位弹窗 -->
    <el-dialog v-model="seatDialogVisible" title="批量生成座位" width="460px">
      <el-alert :title="`为自习室「${seatForm.roomName}」批量生成座位`" type="info" :closable="false"
        style="margin-bottom: 16px" />
      <el-form ref="seatFormRef" :model="seatForm" :rules="seatRules" label-width="90px">
        <el-form-item label="行数" prop="rowCount">
          <el-input-number v-model="seatForm.rowCount" :min="1" :max="50" style="width: 100%" />
        </el-form-item>
        <el-form-item label="列数" prop="colCount">
          <el-input-number v-model="seatForm.colCount" :min="1" :max="50" style="width: 100%" />
        </el-form-item>
        <el-form-item label="区域" prop="area">
          <el-input v-model="seatForm.area" placeholder="如：A区" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="seatDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchSubmit">生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>