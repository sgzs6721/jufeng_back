package com.jufeng.controller;

import com.jufeng.dto.ApiResponse;
import com.jufeng.entity.ActivityRegistration;
import com.jufeng.repository.ActivityRegistrationRepository;
import com.jufeng.task.TestDataScheduledTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestDataController {

    @Autowired
    private ActivityRegistrationRepository registrationRepository;

    @Autowired
    private TestDataScheduledTask testDataScheduledTask;

    /**
     * 插入测试数据
     * 从2025年10月17日18:15开始，每10分钟插入一条，共10条
     */
    @PostMapping("/insert-test-data")
    public ApiResponse<List<ActivityRegistration>> insertTestData() {
        try {
            List<ActivityRegistration> insertedRecords = new ArrayList<>();
            
            // 起始时间：2025-10-17 18:15
            LocalDateTime startTime = LocalDateTime.of(2025, 10, 17, 18, 15, 0);
            
            // 插入10条测试数据，每10分钟一条
            for (int i = 0; i < 10; i++) {
                LocalDateTime registrationTime = startTime.plusMinutes(i * 10);
                
                ActivityRegistration registration = new ActivityRegistration();
                registration.setName("测试用户" + (i + 1));
                registration.setPhone(String.format("138000000%02d", i + 1));
                // 交替选择课程包
                registration.setCoursePackage(i % 2 == 0 ? "PACKAGE_30" : "PACKAGE_60");
                registration.setActivityName("10月18日店庆特惠！乒乓球培训超值课包来袭！");
                registration.setActivityDate(LocalDate.of(2025, 10, 18));
                registration.setRegistrationTime(registrationTime);
                registration.setStatus("PENDING");
                registration.setRemark(String.format("自动测试数据 - %02d:%02d", 
                    registrationTime.getHour(), registrationTime.getMinute()));
                
                Long id = registrationRepository.save(registration);
                registration.setId(id);
                insertedRecords.add(registration);
            }
            
            return ApiResponse.success("成功插入" + insertedRecords.size() + "条测试数据", insertedRecords);
        } catch (Exception e) {
            return ApiResponse.error("插入测试数据失败: " + e.getMessage());
        }
    }

    /**
     * 清除测试数据
     */
    @DeleteMapping("/clear-test-data")
    public ApiResponse<String> clearTestData() {
        try {
            int deletedCount = registrationRepository.deleteTestData();
            // 清除数据后重置计数器
            testDataScheduledTask.resetCounter();
            return ApiResponse.success("成功删除" + deletedCount + "条测试数据，并重置计数器");
        } catch (Exception e) {
            return ApiResponse.error("删除测试数据失败: " + e.getMessage());
        }
    }

    /**
     * 查看定时任务状态
     */
    @GetMapping("/scheduled-task-status")
    public ApiResponse<Map<String, Object>> getScheduledTaskStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("insertedCount", testDataScheduledTask.getInsertedCount());
            status.put("maxCount", 5);
            status.put("isRunning", testDataScheduledTask.getInsertedCount() < 5);
            status.put("activityStartTime", "第一阶段: 17号22:30, 18号01:30; 第二阶段: 18号09:00, 13:00, 17:00");
            status.put("activityEndTime", "2025-10-18 23:59:59");
            status.put("interval", "第一阶段每3小时，第二阶段每4小时");
            status.put("description", "定时任务在指定时间点自动插入测试数据，共5条");
            return ApiResponse.success("获取定时任务状态成功", status);
        } catch (Exception e) {
            return ApiResponse.error("获取定时任务状态失败: " + e.getMessage());
        }
    }

    /**
     * 重置定时任务计数器
     */
    @PostMapping("/reset-scheduled-counter")
    public ApiResponse<String> resetScheduledCounter() {
        try {
            testDataScheduledTask.resetCounter();
            return ApiResponse.success("定时任务计数器已重置");
        } catch (Exception e) {
            return ApiResponse.error("重置计数器失败: " + e.getMessage());
        }
    }

    /**
     * 手动触发一次定时任务（用于测试）
     */
    @PostMapping("/trigger-scheduled-task")
    public ApiResponse<String> triggerScheduledTask() {
        try {
            testDataScheduledTask.insertTestDataPeriodically();
            return ApiResponse.success("手动触发定时任务成功，已插入数量：" + testDataScheduledTask.getInsertedCount());
        } catch (Exception e) {
            return ApiResponse.error("手动触发定时任务失败: " + e.getMessage());
        }
    }
}

