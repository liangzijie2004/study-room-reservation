package com.campus.studyroom.controller;

import com.campus.studyroom.annotation.AdminOnly;
import com.campus.studyroom.common.PageResult;
import com.campus.studyroom.common.Result;
import com.campus.studyroom.entity.Notice;
import com.campus.studyroom.service.NoticeService;
import com.campus.studyroom.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公告
 */
@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping
    public Result<PageResult<Notice>> page(@RequestParam(defaultValue = "1") long page,
                                           @RequestParam(defaultValue = "10") long size) {
        return Result.ok(noticeService.pageNotices(page, size));
    }

    @GetMapping("/{id}")
    public Result<Notice> detail(@PathVariable Long id) {
        return Result.ok(noticeService.detail(id));
    }

    @PostMapping
    @AdminOnly
    public Result<Void> create(@RequestBody Notice notice) {
        noticeService.create(UserContext.getUserId(), notice);
        return Result.ok("发布成功", null);
    }

    @PutMapping("/{id}")
    @AdminOnly
    public Result<Void> update(@PathVariable Long id, @RequestBody Notice notice) {
        noticeService.update(id, notice);
        return Result.ok("修改成功", null);
    }

    @DeleteMapping("/{id}")
    @AdminOnly
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.ok();
    }
}