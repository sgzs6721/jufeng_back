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
     * 从2025年10月17日18:05开始，每3小时插入一条，到18号06:05
     */
    @PostMapping("/insert-test-data")
    public ApiResponse<List<ActivityRegistration>> insertTestData() {
        try {
            List<ActivityRegistration> insertedRecords = new ArrayList<>();
            
            // 定义测试数据
            String[][] testData = {
                {"测试用户1", "13800000001", "PACKAGE_30", "2025-10-17T18:05:00", "自动测试数据 - 18:05"},
                {"测试用户2", "13800000002", "PACKAGE_60", "2025-10-17T21:05:00", "自动测试数据 - 21:05"},
                {"测试用户3", "13800000003", "PACKAGE_30", "2025-10-18T00:05:00", "自动测试数据 - 00:05"},
                {"测试用户4", "13800000004", "PACKAGE_60", "2025-10-18T03:05:00", "自动测试数据 - 03:05"},
                {"测试用户5", "13800000005", "PACKAGE_30", "2025-10-18T06:05:00", "自动测试数据 - 06:05"}
            };
            
            for (String[] data : testData) {
                ActivityRegistration registration = new ActivityRegistration();
                registration.setName(data[0]);
                registration.setPhone(data[1]);
                registration.setCoursePackage(data[2]);
                registration.setActivityName("10月18日店庆特惠！乒乓球培训超值课包来袭！");
                registration.setActivityDate(LocalDate.of(2025, 10, 18));
                registration.setRegistrationTime(LocalDateTime.parse(data[3]));
                registration.setStatus("PENDING");
                registration.setRemark(data[4]);
                
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

