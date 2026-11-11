# 接口契约（前后端约定）

统一响应结构：

```json
{ "code": 200, "msg": "success", "data": {} }
```

- 成功 code=200；业务失败 code=500（带 msg）；未登录/令牌失效 code=401；无权限 code=403。
- 分页响应 data：`{ "records": [], "total": 0, "page": 1, "size": 10 }`
- 需登录的接口在请求头带：`Authorization: Bearer <token>`
- 基础路径：`/api`

## 1. 认证

| 方法 | 路径 | 说明 | 请求体/参数 |
|---|---|---|---|
| POST | /api/auth/register | 学生注册 | {username,password,realName,studentNo,phone} |
| POST | /api/auth/login | 登录 | {username,password} → data:{token,user} |
| GET | /api/auth/info | 当前登录用户 | - |

user 对象：`{id,username,realName,studentNo,phone,avatar,role,status}`
role: `STUDENT` / `ADMIN`；status: 1 正常 / 0 禁用

## 2. 用户管理（ADMIN）

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /api/users?page&size&keyword&role | 分页查询用户 |
| PUT | /api/users/{id}/status?status=0|1 | 启用/禁用 |
| PUT | /api/users/{id} | 修改用户 |
| DELETE | /api/users/{id} | 删除用户 |
| PUT | /api/users/profile | 修改本人资料 {realName,phone,avatar} |
| PUT | /api/users/password | 修改本人密码 {oldPassword,newPassword} |

## 3. 自习室

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| GET | /api/rooms?page&size&keyword | 登录 | 分页查询 |
| GET | /api/rooms/all | 登录 | 全部（下拉用）|
| GET | /api/rooms/{id} | 登录 | 详情 |
| POST | /api/rooms | ADMIN | 新增 |
| PUT | /api/rooms/{id} | ADMIN | 修改 |
| DELETE | /api/rooms/{id} | ADMIN | 删除 |

room 对象：`{id,name,building,floor,location,openTime,closeTime,capacity,description,status,seatCount}`
status: 1 开放 / 0 关闭

## 4. 座位

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| GET | /api/seats/room/{roomId} | 登录 | 该自习室座位列表（含 row/col 用于座位图）|
| GET | /api/seats/available?roomId&date&startTime&endTime | 登录 | 指定时段可用座位 id 列表 |
| POST | /api/seats/batch | ADMIN | 批量生成座位 {roomId, rowCount, colCount, area} |
| PUT | /api/seats/{id} | ADMIN | 修改（座位号/区域/状态）|
| DELETE | /api/seats/{id} | ADMIN | 删除 |

seat 对象：`{id,roomId,seatNo,area,rowIndex,colIndex,status}`
status: 1 可用 / 0 维护中

## 5. 预约

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| GET | /api/reservations/slots | 登录 | 固定时段列表（下附）|
| POST | /api/reservations | 学生 | 创建预约 {roomId,seatId,reserveDate,startTime,endTime} |
| GET | /api/reservations/my?page&size&status | 学生 | 我的预约 |
| PUT | /api/reservations/{id}/cancel | 学生 | 取消 |
| PUT | /api/reservations/{id}/checkin | 学生 | 签到 |
| PUT | /api/reservations/{id}/checkout | 学生 | 签退 |
| GET | /api/reservations?page&size&status&keyword&date | ADMIN | 全部预约 |
| GET | /api/reservations/seat-status?roomId&date&startTime&endTime | 登录 | 返回已占用座位号数组 |

reservation 对象：`{id,userId,userName,roomId,roomName,seatId,seatNo,reserveDate,startTime,endTime,status,checkinTime,checkoutTime,createTime}`
status: 0 已预约 / 1 已签到 / 2 已完成 / 3 已取消 / 4 违约

固定时段（/api/reservations/slots 返回）：
```
08:00-10:00, 10:00-12:00, 14:00-16:00, 16:00-18:00, 19:00-21:00
```

## 6. 公告

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| GET | /api/notices?page&size | 登录 | 分页 |
| GET | /api/notices/{id} | 登录 | 详情 |
| POST | /api/notices | ADMIN | 发布 {title,content} |
| PUT | /api/notices/{id} | ADMIN | 修改 |
| DELETE | /api/notices/{id} | ADMIN | 删除 |

## 7. 统计（ADMIN）

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /api/stats/overview | {userCount,roomCount,seatCount,todayReservationCount,occupancyRate} |
| GET | /api/stats/trend?days=7 | [{date,count}] 近 N 天预约趋势 |
| GET | /api/stats/room-ranking | [{roomName,count}] 自习室预约排行 |