import request from './request'

// 统计接口（ADMIN）
export const getOverview = () => request.get('/stats/overview')
export const getTrend = (days = 7) =>
  request.get('/stats/trend', { params: { days } })
export const getRoomRanking = () => request.get('/stats/room-ranking')