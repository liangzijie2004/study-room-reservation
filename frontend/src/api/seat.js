import request from './request'

// 座位接口
export const getSeatsByRoom = (roomId) => request.get(`/seats/room/${roomId}`)
export const getAvailableSeats = (params) =>
  request.get('/seats/available', { params })
export const batchCreateSeats = (data) => request.post('/seats/batch', data)
export const updateSeat = (id, data) => request.put(`/seats/${id}`, data)
export const deleteSeat = (id) => request.delete(`/seats/${id}`)