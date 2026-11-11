package com.campus.studyroom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园共享自习室座位预约系统 启动类
 */
@SpringBootApplication
@MapperScan("com.campus.studyroom.mapper")
public class StudyRoomApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyRoomApplication.class, args);
        System.out.println("\n==============================================");
        System.out.println("  自习室座位预约系统后端启动成功！");
        System.out.println("  接口地址: http://localhost:8080/api");
        System.out.println("==============================================\n");
    }
}