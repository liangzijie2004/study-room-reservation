// 通用字典与格式化工具

// 预约状态：0 已预约 / 1 已签到 / 2 已完成 / 3 已取消 / 4 违约
export const RESERVATION_STATUS = {
  0: { label: '已预约', type: 'primary' },
  1: { label: '已签到', type: 'success' },
  2: { label: '已完成', type: 'info' },
  3: { label: '已取消', type: 'warning' },
  4: { label: '违约', type: 'danger' }
}

export const reservationStatusList = Object.entries(RESERVATION_STATUS).map(
  ([value, item]) => ({ value: Number(value), label: item.label })
)

export function reservationStatusLabel(status) {
  return RESERVATION_STATUS[status]?.label || '未知'
}

export function reservationStatusType(status) {
  return RESERVATION_STATUS[status]?.type || 'info'
}

// 座位状态：1 可用 / 0 维护中
export const SEAT_STATUS = {
  1: { label: '可用', type: 'success' },
  0: { label: '维护中', type: 'info' }
}

// 用户状态：1 正常 / 0 禁用
export const USER_STATUS = {
  1: { label: '正常', type: 'success' },
  0: { label: '禁用', type: 'danger' }
}

// 用户角色
export const USER_ROLE = {
  STUDENT: '学生',
  ADMIN: '管理员'
}

export function roleLabel(role) {
  return USER_ROLE[role] || role
}