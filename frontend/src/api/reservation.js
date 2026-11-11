import request from './request'

// 预约接口
export const getSlots = () => request.get('/reservations/slots')
export const createReservation = (data) => request.post('/reservations', data)
export const getMyReservations = (params) =>
  request.get('/reservations/my', { params })
export const cancelReservation = (id) =>
  request.put(`/reservations/${id}/cancel`)
export const checkinReservation = (id) =>
  request.put(`/reservations/${id}/checkin`)
export const checkoutReservation = (id) =>
  request.put(`/reservations/${id}/checkout`)
export const getAllReservations = (params) =>
  request.get('/reservations', { params })
export const getSeatStatus = (params) =>
  request.get('/reservations/seat-status', { params })