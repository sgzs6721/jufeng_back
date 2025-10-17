package com.jufeng.controller;

import com.jufeng.dto.ActivityRegistrationRequest;
import com.jufeng.dto.ApiResponse;
import com.jufeng.entity.ActivityRegistration;
import com.jufeng.service.ActivityRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/registrations")
public class ActivityRegistrationController {

    @Autowired
    private ActivityRegistrationService registrationService;

    @PostMapping
    public ApiResponse<ActivityRegistration> register(@Validated @RequestBody ActivityRegistrationRequest request) {
        try {
            ActivityRegistration registration = registrationService.register(request);
            return ApiResponse.success("报名成功！我们会尽快与您联系。", registration);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/remaining-slots")
    public ApiResponse<Map<String, Object>> getRemainingSlots() {
        long remaining = registrationService.getRemainingSlots();
        Map<String, Object> result = new HashMap<>();
        result.put("remainingSlots", remaining);
        result.put("totalSlots", 10);
        result.put("isFull", remaining <= 0);
        return ApiResponse.success(result);
    }

    @GetMapping
    public ApiResponse<List<ActivityRegistration>> getAllRegistrations() {
        List<ActivityRegistration> registrations = registrationService.getAllRegistrations();
        return ApiResponse.success(registrations);
    }

    @GetMapping("/date/{date}")
    public ApiResponse<List<ActivityRegistration>> getRegistrationsByDate(@PathVariable String date) {
        LocalDate activityDate = LocalDate.parse(date);
        List<ActivityRegistration> registrations = registrationService.getRegistrationsByDate(activityDate);
        return ApiResponse.success(registrations);
    }
}

