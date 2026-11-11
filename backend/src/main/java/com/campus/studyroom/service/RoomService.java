package com.campus.studyroom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.studyroom.common.PageResult;
import com.campus.studyroom.entity.StudyRoom;

import java.util.List;

public interface RoomService extends IService<StudyRoom> {

    PageResult<StudyRoom> pageRooms(long page, long size, String keyword);

    List<StudyRoom> listAll();

    StudyRoom detail(Long id);

    void createRoom(StudyRoom room);

    void updateRoom(Long id, StudyRoom room);

    void deleteRoom(Long id);
}