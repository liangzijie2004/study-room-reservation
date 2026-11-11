// 日期时间格式化工具

// 格式化为 YYYY-MM-DD
export function formatDate(date) {
  if (!date) return ''
  if (typeof date === 'string') return date.slice(0, 10)
  const d = new Date(date)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

// 格式化为 YYYY-MM-DD HH:mm:ss
export function formatDateTime(date) {
  if (!date) return ''
  const str = typeof date === 'string' ? date.replace('T', ' ') : ''
  if (str) return str.slice(0, 19)
  const d = new Date(date)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  const ss = String(d.getSeconds()).padStart(2, '0')
  return `${y}-${m}-${day} ${hh}:${mm}:${ss}`
}