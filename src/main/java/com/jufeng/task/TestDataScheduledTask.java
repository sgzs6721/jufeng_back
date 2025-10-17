package com.jufeng.task;

import com.jufeng.entity.ActivityRegistration;
import com.jufeng.repository.ActivityRegistrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试数据定时任务
 * 从今晚18:30开始，每5分钟插入一条测试数据，最多10条
 */
@Component
public class TestDataScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(TestDataScheduledTask.class);

    @Autowired
    private ActivityRegistrationRepository registrationRepository;

    // 计数器：记录已插入的测试数据数量
    private final AtomicInteger insertedCount = new AtomicInteger(0);

    // 活动日期
    private static final LocalDate ACTIVITY_DATE = LocalDate.of(2025, 10, 18);
    
    // 活动开始时间：2025-10-17 18:30
    private static final LocalDateTime ACTIVITY_START = LocalDateTime.of(2025, 10, 17, 18, 30, 0);
    
    // 活动结束时间：2025-10-18 23:59:59
    private static final LocalDateTime ACTIVITY_END = LocalDateTime.of(2025, 10, 18, 23, 59, 59);

    // 最大插入数量
    private static final int MAX_INSERT_COUNT = 10;

    /**
     * 定时任务：每5分钟执行一次
     * cron表达式：0 星斜杠5 * * * ? 表示每5分钟的整数倍执行（如18:00, 18:05, 18:10...）
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void insertTestDataPeriodically() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 检查1：是否在活动时间范围内
            if (now.isBefore(ACTIVITY_START)) {
                logger.info("活动尚未开始（开始时间：{}），当前时间：{}", ACTIVITY_START, now);
                return;
            }
            
            if (now.isAfter(ACTIVITY_END)) {
                logger.info("活动已结束（结束时间：{}），当前时间：{}", ACTIVITY_END, now);
                return;
            }
            
            // 检查2：是否已达到最大插入数量
            int currentCount = insertedCount.get();
            if (currentCount >= MAX_INSERT_COUNT) {
                logger.info("已达到最大插入数量（{}条），停止插入", MAX_INSERT_COUNT);
                return;
            }
            
            // 插入测试数据
            int nextIndex = currentCount + 1;
            
            ActivityRegistration registration = new ActivityRegistration();
            registration.setName("测试用户" + nextIndex);
            registration.setPhone(String.format("138000000%02d", nextIndex));
            // 交替选择课程包
            registration.setCoursePackage(nextIndex % 2 == 1 ? "PACKAGE_30" : "PACKAGE_60");
            registration.setActivityName("10月18日店庆特惠！乒乓球培训超值课包来袭！");
            registration.setActivityDate(ACTIVITY_DATE);
            registration.setRegistrationTime(now);
            registration.setStatus("PENDING");
            registration.setRemark(String.format("定时任务自动插入 - %02d:%02d", now.getHour(), now.getMinute()));
            
            Long id = registrationRepository.save(registration);
            insertedCount.incrementAndGet();
            
            logger.info("✅ 定时任务成功插入测试数据 [{}]：ID={}, 姓名={}, 时间={}, 已插入数量={}/{}",
                    nextIndex, id, registration.getName(), now, insertedCount.get(), MAX_INSERT_COUNT);
            
        } catch (Exception e) {
            logger.error("❌ 定时任务插入测试数据失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 重置计数器（可选，用于测试）
     */
    public void resetCounter() {
        insertedCount.set(0);
        logger.info("计数器已重置");
    }

    /**
     * 获取当前已插入数量
     */
    public int getInsertedCount() {
        return insertedCount.get();
    }
}

