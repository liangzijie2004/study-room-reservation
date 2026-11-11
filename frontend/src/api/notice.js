import request from './request'

// 公告接口
export const getNotices = (params) => request.get('/notices', { params })
export const getNotice = (id) => request.get(`/notices/${id}`)
export const createNotice = (data) => request.post('/notices', data)
export const updateNotice = (id, data) => request.put(`/notices/${id}`, data)
export const deleteNotice = (id) => request.delete(`/notices/${id}`)