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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试数据定时任务
 * 第一阶段：17号22:30, 18号01:30（每3小时）
 * 第二阶段：18号09:00, 13:00, 17:00（每4小时）
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
    
    // 活动结束时间：2025-10-18 23:59:59
    private static final LocalDateTime ACTIVITY_END = LocalDateTime.of(2025, 10, 18, 23, 59, 59);

    // 定义所有插入时间点
    private static final List<LocalDateTime> SCHEDULED_TIMES = Arrays.asList(
        // 第一阶段：每3小时
        LocalDateTime.of(2025, 10, 17, 22, 30, 0),  // 17号 22:30
        LocalDateTime.of(2025, 10, 18, 1, 30, 0),   // 18号 01:30
        
        // 第二阶段：每4小时
        LocalDateTime.of(2025, 10, 18, 9, 0, 0),    // 18号 09:00
        LocalDateTime.of(2025, 10, 18, 13, 0, 0),   // 18号 13:00
        LocalDateTime.of(2025, 10, 18, 17, 0, 0)    // 18号 17:00
    );

    // 记录已执行的时间点，避免重复执行
    private final Set<LocalDateTime> executedTimes = new HashSet<>();

    // 最大插入数量
    private static final int MAX_INSERT_COUNT = SCHEDULED_TIMES.size();

    /**
     * 定时任务：每分钟检查一次是否需要插入数据
     * 在指定的时间点执行插入操作
     */
    @Scheduled(cron = "0 * * * * ?")
    public void insertTestDataPeriodically() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 检查1：是否已达到最大插入数量
            int currentCount = insertedCount.get();
            if (currentCount >= MAX_INSERT_COUNT) {
                return; // 静默返回，不记录日志
            }
            
            // 检查2：当前时间是否匹配任意一个预定时间点（精确到分钟）
            LocalDateTime currentMinute = now.withSecond(0).withNano(0);
            
            boolean shouldInsert = false;
            LocalDateTime matchedTime = null;
            
            for (LocalDateTime scheduledTime : SCHEDULED_TIMES) {
                LocalDateTime scheduledMinute = scheduledTime.withSecond(0).withNano(0);
                if (currentMinute.equals(scheduledMinute) && !executedTimes.contains(scheduledMinute)) {
                    shouldInsert = true;
                    matchedTime = scheduledTime;
                    executedTimes.add(scheduledMinute);
                    break;
                }
            }
            
            if (!shouldInsert) {
                return; // 不是预定时间，静默返回
            }
            
            // 检查3：是否已过活动结束时间
            if (now.isAfter(ACTIVITY_END)) {
                logger.info("活动已结束（结束时间：{}），当前时间：{}", ACTIVITY_END, now);
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
            registration.setRegistrationTime(matchedTime);
            registration.setStatus("PENDING");
            registration.setRemark(String.format("定时任务自动插入 - %02d:%02d", 
                matchedTime.getHour(), matchedTime.getMinute()));
            
            Long id = registrationRepository.save(registration);
            insertedCount.incrementAndGet();
            
            logger.info("✅ 定时任务成功插入测试数据 [{}]：ID={}, 姓名={}, 预定时间={}, 已插入数量={}/{}",
                    nextIndex, id, registration.getName(), matchedTime, insertedCount.get(), MAX_INSERT_COUNT);
            
        } catch (Exception e) {
            logger.error("❌ 定时任务插入测试数据失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 重置计数器（可选，用于测试）
     */
    public void resetCounter() {
        insertedCount.set(0);
        executedTimes.clear();
        logger.info("计数器和执行记录已重置");
    }

    /**
     * 获取当前已插入数量
     */
    public int getInsertedCount() {
        return insertedCount.get();
    }
}

