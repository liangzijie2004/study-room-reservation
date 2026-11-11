package com.campus.studyroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.studyroom.common.BusinessException;
import com.campus.studyroom.common.PageResult;
import com.campus.studyroom.entity.Notice;
import com.campus.studyroom.entity.User;
import com.campus.studyroom.mapper.NoticeMapper;
import com.campus.studyroom.mapper.UserMapper;
import com.campus.studyroom.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResult<Notice> pageNotices(long page, long size) {
        Page<Notice> result = page(new Page<>(page, size),
                new LambdaQueryWrapper<Notice>().orderByDesc(Notice::getCreateTime));
        fillPublisherName(result.getRecords());
        return PageResult.of(result);
    }

    @Override
    public Notice detail(Long id) {
        Notice notice = getById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        return notice;
    }

    @Override
    public void create(Long publisherId, Notice notice) {
        notice.setId(null);
        notice.setPublisherId(publisherId);
        save(notice);
    }

    @Override
    public void update(Long id, Notice notice) {
        if (getById(id) == null) {
            throw new BusinessException("公告不存在");
        }
        notice.setId(id);
        updateById(notice);
    }

    @Override
    public void delete(Long id) {
        if (getById(id) == null) {
            throw new BusinessException("公告不存在");
        }
        removeById(id);
    }

    private void fillPublisherName(List<Notice> notices) {
        if (notices == null || notices.isEmpty()) {
            return;
        }
        List<Long> ids = notices.stream().map(Notice::getPublisherId)
                .filter(java.util.Objects::nonNull).distinct().collect(Collectors.toList());
        if (ids.isEmpty()) {
            return;
        }
        Map<Long, User> userMap = userMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        for (Notice notice : notices) {
            User user = userMap.get(notice.getPublisherId());
            if (user != null) {
                notice.setPublisherName(user.getRealName());
            }
        }
    }
}