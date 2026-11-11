package com.campus.studyroom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.studyroom.common.PageResult;
import com.campus.studyroom.entity.Notice;

public interface NoticeService extends IService<Notice> {

    PageResult<Notice> pageNotices(long page, long size);

    Notice detail(Long id);

    void create(Long publisherId, Notice notice);

    void update(Long id, Notice notice);

    void delete(Long id);
}