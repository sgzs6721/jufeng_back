package com.jufeng.service;

import com.jufeng.dto.ActivityRegistrationRequest;
import com.jufeng.entity.ActivityRegistration;
import com.jufeng.repository.ActivityRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityRegistrationService {

    @Autowired
    private ActivityRegistrationRepository registrationRepository;

    private static final int MAX_REGISTRATIONS = 10;

    @Transactional
    public ActivityRegistration register(ActivityRegistrationRequest request) {
        LocalDate activityDate = LocalDate.of(2025, 10, 18);
        
        long currentCount = registrationRepository.countByActivityDateAndStatus(activityDate, "PENDING");
        if (currentCount >= MAX_REGISTRATIONS) {
            throw new RuntimeException("报名名额已满，仅限10个名额");
        }

        ActivityRegistration registration = new ActivityRegistration();
        registration.setName(request.getName());
        registration.setPhone(request.getPhone());
        registration.setCoursePackage(request.getCoursePackage());
        registration.setActivityName("10月18日店庆特惠！乒乓球培训超值课包来袭！");
        registration.setActivityDate(activityDate);
        registration.setRegistrationTime(LocalDateTime.now());
        registration.setStatus("PENDING");

        Long id = registrationRepository.save(registration);
        registration.setId(id);
        
        return registration;
    }

    public List<ActivityRegistration> getRegistrationsByDate(LocalDate activityDate) {
        return registrationRepository.findByActivityDate(activityDate);
    }

    public List<ActivityRegistration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    public long getRemainingSlots() {
        LocalDate activityDate = LocalDate.of(2025, 10, 18);
        long currentCount = registrationRepository.countByActivityDateAndStatus(activityDate, "PENDING");
        return Math.max(0, MAX_REGISTRATIONS - currentCount);
    }
}

