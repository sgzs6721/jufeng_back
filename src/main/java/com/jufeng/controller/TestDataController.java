package com.jufeng.controller;

import com.jufeng.dto.ApiResponse;
import com.jufeng.entity.ActivityRegistration;
import com.jufeng.repository.ActivityRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestDataController {

    @Autowired
    private ActivityRegistrationRepository registrationRepository;

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
            return ApiResponse.success("成功删除" + deletedCount + "条测试数据");
        } catch (Exception e) {
            return ApiResponse.error("删除测试数据失败: " + e.getMessage());
        }
    }
}

