import request from './request'

// 用户管理（ADMIN）+ 个人中心
export const getUsers = (params) => request.get('/users', { params })
export const updateUserStatus = (id, status) =>
  request.put(`/users/${id}/status`, null, { params: { status } })
export const updateUser = (id, data) => request.put(`/users/${id}`, data)
export const deleteUser = (id) => request.delete(`/users/${id}`)
export const updateProfile = (data) => request.put('/users/profile', data)
export const updatePassword = (data) => request.put('/users/password', data)