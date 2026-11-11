import request from './request'

// 自习室接口
export const getRooms = (params) => request.get('/rooms', { params })
export const getAllRooms = () => request.get('/rooms/all')
export const getRoom = (id) => request.get(`/rooms/${id}`)
export const createRoom = (data) => request.post('/rooms', data)
export const updateRoom = (id, data) => request.put(`/rooms/${id}`, data)
export const deleteRoom = (id) => request.delete(`/rooms/${id}`)