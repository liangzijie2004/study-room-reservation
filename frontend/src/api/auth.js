import request from './request'

// 认证相关接口
export const register = (data) => request.post('/auth/register', data)
export const login = (data) => request.post('/auth/login', data)
export const getInfo = () => request.get('/auth/info')