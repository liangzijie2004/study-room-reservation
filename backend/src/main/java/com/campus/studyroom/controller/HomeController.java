package com.campus.studyroom.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提示页：直接访问后端地址（根路径或 /api）时展示服务状态与接口入口，替代默认的 404 页
 */
@RestController
public class HomeController {

    @GetMapping(value = {"/", "/api"}, produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "<!DOCTYPE html>"
                + "<html lang=\"zh-CN\"><head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "<title>自习室座位预约系统 · 后端服务</title>"
                + "<style>"
                + "*{box-sizing:border-box;margin:0;padding:0}"
                + "body{font-family:'Microsoft YaHei','PingFang SC',Arial,sans-serif;"
                + "background:linear-gradient(135deg,#eef3fb 0%,#f7f9fc 100%);min-height:100vh;"
                + "display:flex;align-items:center;justify-content:center;padding:24px;color:#303133}"
                + ".card{background:#fff;border-radius:12px;box-shadow:0 6px 24px rgba(64,158,255,.12);"
                + "max-width:720px;width:100%;padding:36px 40px}"
                + ".badge{display:inline-block;background:#e8f5e9;color:#2e7d32;font-size:13px;"
                + "padding:4px 12px;border-radius:20px;margin-bottom:16px}"
                + "h1{font-size:22px;font-weight:600;margin-bottom:8px}"
                + ".sub{color:#909399;font-size:14px;margin-bottom:24px}"
                + ".tip{background:#ecf5ff;border-left:4px solid #409EFF;padding:12px 16px;"
                + "border-radius:4px;font-size:14px;line-height:1.7;margin-bottom:24px}"
                + ".tip a{color:#409EFF;font-weight:600;text-decoration:none}"
                + "h2{font-size:15px;font-weight:600;margin:0 0 12px;color:#606266}"
                + "table{width:100%;border-collapse:collapse;font-size:13.5px}"
                + "th,td{text-align:left;padding:9px 10px;border-bottom:1px solid #ebeef5}"
                + "th{color:#909399;font-weight:500;background:#fafafa}"
                + "td.m{width:70px;color:#409EFF;font-weight:600}"
                + "</style></head><body><div class=\"card\">"
                + "<div class=\"badge\">● 服务运行中</div>"
                + "<h1>校园共享自习室座位预约系统</h1>"
                + "<p class=\"sub\">后端服务已启动，监听端口 8080 ｜ Spring Boot</p>"
                + "<div class=\"tip\">"
                + "这里只提供 JSON 接口，没有管理界面。请启动前端后访问 "
                + "<a href=\"http://localhost:5173\" target=\"_blank\">http://localhost:5173</a> "
                + "使用系统（默认账号 admin / student，密码 123456）。"
                + "</div>"
                + "<h2>常用接口</h2>"
                + "<table>"
                + "<tr><th>方法</th><th>接口</th><th>说明</th></tr>"
                + "<tr><td class=\"m\">POST</td><td>/api/auth/login</td><td>登录，返回 JWT 令牌</td></tr>"
                + "<tr><td class=\"m\">POST</td><td>/api/auth/register</td><td>学生注册</td></tr>"
                + "<tr><td class=\"m\">GET</td><td>/api/rooms</td><td>自习室列表（需令牌）</td></tr>"
                + "<tr><td class=\"m\">GET</td><td>/api/seats/room/{roomId}</td><td>自习室座位图（需令牌）</td></tr>"
                + "<tr><td class=\"m\">POST</td><td>/api/reservations</td><td>创建预约（需令牌）</td></tr>"
                + "<tr><td class=\"m\">GET</td><td>/api/stats/overview</td><td>数据看板总览（管理员）</td></tr>"
                + "</table>"
                + "<p class=\"sub\" style=\"margin:20px 0 0\">"
                + "调试接口请使用 Postman 或 curl；除登录/注册外均需请求头 "
                + "<code>Authorization: Bearer &lt;token&gt;</code>。</p>"
                + "</div></body></html>";
    }
}