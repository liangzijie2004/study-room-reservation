package com.campus.studyroom.controller;

import com.campus.studyroom.annotation.AdminOnly;
import com.campus.studyroom.common.PageResult;
import com.campus.studyroom.common.Result;
import com.campus.studyroom.entity.StudyRoom;
import com.campus.studyroom.service.RoomService;
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

import java.util.List;

/**
 * 自习室
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public Result<PageResult<StudyRoom>> page(@RequestParam(defaultValue = "1") long page,
                                              @RequestParam(defaultValue = "8") long size,
                                              @RequestParam(required = false) String keyword) {
        return Result.ok(roomService.pageRooms(page, size, keyword));
    }

    @GetMapping("/all")
    public Result<List<StudyRoom>> all() {
        return Result.ok(roomService.listAll());
    }

    @GetMapping("/{id}")
    public Result<StudyRoom> detail(@PathVariable Long id) {
        return Result.ok(roomService.detail(id));
    }

    @PostMapping
    @AdminOnly
    public Result<Void> create(@RequestBody StudyRoom room) {
        roomService.createRoom(room);
        return Result.ok("新增成功", null);
    }

    @PutMapping("/{id}")
    @AdminOnly
    public Result<Void> update(@PathVariable Long id, @RequestBody StudyRoom room) {
        roomService.updateRoom(id, room);
        return Result.ok("修改成功", null);
    }

    @DeleteMapping("/{id}")
    @AdminOnly
    public Result<Void> delete(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return Result.ok();
    }
}