<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { getOverview, getTrend, getRoomRanking } from '@/api/stats'

// 注册 ECharts 组件
use([CanvasRenderer, LineChart, BarChart, GridComponent, TooltipComponent, LegendComponent])

const loading = ref(false)
const overview = reactive({
  userCount: 0,
  roomCount: 0,
  seatCount: 0,
  todayReservationCount: 0,
  occupancyRate: 0
})

const trendOption = ref({})
const rankingOption = ref({})

// 统计卡片配置
const cards = computed(() => [
  { label: '用户数', value: overview.userCount, icon: 'User', color: '#409eff' },
  { label: '自习室数', value: overview.roomCount, icon: 'OfficeBuilding', color: '#67c23a' },
  { label: '座位数', value: overview.seatCount, icon: 'Grid', color: '#e6a23c' },
  { label: '今日预约数', value: overview.todayReservationCount, icon: 'Calendar', color: '#f56c6c' },
  { label: '座位使用率', value: overview.occupancyRate, icon: 'PieChart', color: '#909399', percent: true }
])

// 使用率展示：兼容 0-1 与 0-100
function displayValue(card) {
  if (card.percent) {
    const r = Number(card.value) || 0
    const p = r <= 1 ? r * 100 : r
    return `${p.toFixed(1)}%`
  }
  return card.value ?? 0
}

// 加载概览
async function loadOverview() {
  const data = await getOverview()
  Object.assign(overview, data || {})
}

// 加载趋势
async function loadTrend() {
  const data = await getTrend(7)
  const arr = Array.isArray(data) ? data : []
  trendOption.value = {
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 30, bottom: 30 },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: arr.map((i) => i.date)
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: '预约数',
        type: 'line',
        smooth: true,
        data: arr.map((i) => i.count),
        areaStyle: { opacity: 0.15 },
        itemStyle: { color: '#409eff' },
        lineStyle: { width: 3 }
      }
    ]
  }
}

// 加载排行
async function loadRanking() {
  const data = await getRoomRanking()
  const arr = Array.isArray(data) ? data : []
  rankingOption.value = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 40, right: 20, top: 30, bottom: 60 },
    xAxis: {
      type: 'category',
      data: arr.map((i) => i.roomName),
      axisLabel: { interval: 0, rotate: 25 }
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: '预约数',
        type: 'bar',
        barMaxWidth: 40,
        data: arr.map((i) => i.count),
        itemStyle: { color: '#67c23a', borderRadius: [4, 4, 0, 0] }
      }
    ]
  }
}

async function loadAll() {
  loading.value = true
  try {
    await Promise.all([loadOverview(), loadTrend(), loadRanking()])
  } finally {
    loading.value = false
  }
}

onMounted(loadAll)
</script>

<template>
  <div class="page-container" v-loading="loading">
    <div class="page-header">
      <div class="page-title">数据看板</div>
      <el-button @click="loadAll">
        <el-icon><Refresh /></el-icon> 刷新
      </el-button>
    </div>

    <el-row :gutter="16" class="stat-row">
      <el-col v-for="card in cards" :key="card.label" :xs="12" :sm="12" :md="8" :lg="4" :xl="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon" :style="{ backgroundColor: card.color }">
              <el-icon><component :is="card.icon" /></el-icon>
            </div>
            <div>
              <div class="stat-value">{{ displayValue(card) }}</div>
              <div class="stat-label">{{ card.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="14">
        <el-card shadow="never" class="chart-card">
          <template #header><span class="card-title">近 7 天预约趋势</span></template>
          <v-chart class="chart" :option="trendOption" autoresize />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card shadow="never" class="chart-card">
          <template #header><span class="card-title">自习室预约排行</span></template>
          <v-chart class="chart" :option="rankingOption" autoresize />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.stat-row {
  margin-bottom: 8px;
}

.stat-card {
  margin-bottom: 16px;
}

.stat-inner {
  display: flex;
  align-items: center;
  gap: 14px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  flex-shrink: 0;
}

.chart-card {
  margin-bottom: 16px;
}

.card-title {
  font-weight: 600;
}

.chart {
  height: 340px;
  width: 100%;
}
</style>