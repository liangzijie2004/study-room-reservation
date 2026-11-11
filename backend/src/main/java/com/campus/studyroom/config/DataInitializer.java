package com.campus.studyroom.config;

import com.campus.studyroom.common.Constants;
import com.campus.studyroom.entity.Notice;
import com.campus.studyroom.entity.Reservation;
import com.campus.studyroom.entity.Seat;
import com.campus.studyroom.entity.StudyRoom;
import com.campus.studyroom.entity.User;
import com.campus.studyroom.mapper.NoticeMapper;
import com.campus.studyroom.mapper.ReservationMapper;
import com.campus.studyroom.mapper.SeatMapper;
import com.campus.studyroom.mapper.StudyRoomMapper;
import com.campus.studyroom.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 首次启动时初始化演示数据（数据库为空时执行）
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudyRoomMapper roomMapper;

    @Autowired
    private SeatMapper seatMapper;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userMapper.selectCount(null) > 0) {
            log.info("检测到已有数据，跳过初始化");
            return;
        }
        log.info("开始初始化演示数据 ...");

        User admin = createUser("admin", "系统管理员", "0000000", "13800000000", Constants.ROLE_ADMIN);
        User stu1 = createUser("student", "张小明", "2021001", "13800000001", Constants.ROLE_STUDENT);
        User stu2 = createUser("student2", "李思思", "2021002", "13800000002", Constants.ROLE_STUDENT);

        StudyRoom room1 = createRoom("静学苑·一楼自习室", "图书馆", "1层", "图书馆东侧",
                "08:00", "22:00", 40, "安静整洁，配备独立电源与台灯，适合日常自习。");
        StudyRoom room2 = createRoom("静学苑·二楼考研专区", "图书馆", "2层", "图书馆西侧",
                "07:30", "23:00", 40, "专属考研专区，环境安静，开放时间更长。");
        StudyRoom room3 = createRoom("明德楼共享自习室", "明德楼", "3层", "A区",
                "08:00", "21:30", 24, "共享自习室，靠近教学楼，课间自习方便。");
        StudyRoom room4 = createRoom("信息楼数字自习室", "信息楼", "2层", "南侧",
                "08:00", "22:00", 30, "配备无线网络与充电插座，适合携带电脑学习。");

        List<Long> roomSeatIds1 = createSeats(room1.getId(), 5, 8);
        List<Long> roomSeatIds2 = createSeats(room2.getId(), 5, 8);
        List<Long> roomSeatIds3 = createSeats(room3.getId(), 4, 6);
        List<Long> roomSeatIds4 = createSeats(room4.getId(), 5, 6);

        createNotice(admin.getId(), "关于自习室开放时间调整的通知",
                "自本周起，图书馆一楼自习室开放时间调整为 08:00-22:00，二楼考研专区调整为 07:30-23:00。"
                        + "请同学们合理安排学习时间，离馆前请带走个人物品。");
        createNotice(admin.getId(), "座位预约规则说明",
                "1. 每位同学同一时段仅可预约一个座位；\n"
                        + "2. 预约后请在预约当天签到，逾期未签到视为违约；\n"
                        + "3. 如无法按时到馆，请提前取消预约，避免资源浪费。");
        createNotice(admin.getId(), "期末考试期间延时开放公告",
                "期末考试期间，各自习室将视情况延长开放时间，具体安排请关注后续通知。祝同学们取得好成绩！");

        seedReservations(stu1, stu2,
                new Long[]{room1.getId(), room2.getId(), room3.getId(), room4.getId()},
                new List[]{roomSeatIds1, roomSeatIds2, roomSeatIds3, roomSeatIds4});

        log.info("演示数据初始化完成：用户 {} 个，自习室 {} 个", userMapper.selectCount(null), roomMapper.selectCount(null));
    }

    private User createUser(String username, String realName, String studentNo, String phone, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRealName(realName);
        user.setStudentNo(studentNo);
        user.setPhone(phone);
        user.setRole(role);
        user.setStatus(Constants.STATUS_ENABLED);
        userMapper.insert(user);
        return user;
    }

    private StudyRoom createRoom(String name, String building, String floor, String location,
                                 String openTime, String closeTime, int capacity, String description) {
        StudyRoom room = new StudyRoom();
        room.setName(name);
        room.setBuilding(building);
        room.setFloor(floor);
        room.setLocation(location);
        room.setOpenTime(openTime);
        room.setCloseTime(closeTime);
        room.setCapacity(capacity);
        room.setDescription(description);
        room.setStatus(Constants.ROOM_OPEN);
        roomMapper.insert(room);
        return room;
    }

    /**
     * 生成 rows x cols 的座位网格，返回生成的座位 id 列表
     */
    private List<Long> createSeats(Long roomId, int rows, int cols) {
        List<Long> ids = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            char rowLabel = (char) ('A' + row);
            for (int col = 0; col < cols; col++) {
                Seat seat = new Seat();
                seat.setRoomId(roomId);
                seat.setSeatNo(rowLabel + String.format("%02d", col + 1));
                seat.setArea(col < cols / 2 ? "靠窗区" : "中央区");
                seat.setRowIndex(row);
                seat.setColIndex(col);
                seat.setStatus(Constants.SEAT_AVAILABLE);
                seatMapper.insert(seat);
                ids.add(seat.getId());
            }
        }
        return ids;
    }

    private void createNotice(Long publisherId, String title, String content) {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setPublisherId(publisherId);
        noticeMapper.insert(notice);
    }

    /**
     * 生成近 7 天的预约数据，用于看板趋势与排行展示
     */
    private void seedReservations(User stu1, User stu2, Long[] roomIds, List<Long>[] seatIdGroups) {
        int[] perDay = {3, 2, 4, 3, 5, 3, 4}; // 从 6 天前到今天
        Set<String> used = new HashSet<>();
        int counter = 0;
        LocalDate today = LocalDate.now();
        for (int dayOffset = perDay.length - 1; dayOffset >= 0; dayOffset--) {
            LocalDate date = today.minusDays(dayOffset);
            int count = perDay[dayOffset];
            for (int i = 0; i < count; i++) {
                int roomIdx = counter % roomIds.length;
                List<Long> seats = seatIdGroups[roomIdx];
                int slotIdx = counter % Constants.TIME_SLOTS.size();
                int seatIdx = (counter * 3 + 1) % seats.size();
                String key = roomIds[roomIdx] + "-" + seats.get(seatIdx) + "-" + date + "-" + slotIdx;
                counter++;
                if (!used.add(key)) {
                    continue;
                }
                Long userId = (i % 2 == 0) ? stu1.getId() : stu2.getId();
                String[] times = Constants.TIME_SLOTS.get(slotIdx).split("-");

                Reservation reservation = new Reservation();
                reservation.setUserId(userId);
                reservation.setRoomId(roomIds[roomIdx]);
                reservation.setSeatId(seats.get(seatIdx));
                reservation.setReserveDate(date);
                reservation.setStartTime(times[0]);
                reservation.setEndTime(times[1]);
                if (date.isBefore(today)) {
                    reservation.setStatus(Constants.RESERVE_FINISHED);
                } else {
                    reservation.setStatus(i % 2 == 0 ? Constants.RESERVE_BOOKED : Constants.RESERVE_CHECKED_IN);
                }
                reservationMapper.insert(reservation);
            }
        }
    }
}