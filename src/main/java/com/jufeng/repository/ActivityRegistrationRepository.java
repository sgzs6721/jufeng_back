package com.jufeng.repository;

import com.jufeng.entity.ActivityRegistration;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.impl.DSL.*;

@Repository
public class ActivityRegistrationRepository {

    @Autowired
    private DSLContext dsl;

    public Long save(ActivityRegistration registration) {
        try {
            // MySQL 不支持 RETURNING，使用 execute() + LAST_INSERT_ID() 方式
            dsl.insertInto(table("activity_registrations"))
                    .set(field("name"), registration.getName())
                    .set(field("phone"), registration.getPhone())
                    .set(field("course_package"), registration.getCoursePackage())
                    .set(field("activity_name"), registration.getActivityName())
                    .set(field("activity_date"), registration.getActivityDate())
                    .set(field("registration_time"), registration.getRegistrationTime())
                    .set(field("status"), registration.getStatus())
                    .set(field("remark"), registration.getRemark())
                    .execute();
            
            // 获取最后插入的ID
            Long id = dsl.select(field("LAST_INSERT_ID()", Long.class))
                    .fetchOne(0, Long.class);
            
            if (id == null || id == 0) {
                throw new RuntimeException("插入记录失败：未获取到自增ID");
            }
            
            return id;
        } catch (Exception e) {
            throw new RuntimeException("保存报名记录失败: " + e.getMessage(), e);
        }
    }

    public List<ActivityRegistration> findByActivityDate(LocalDate activityDate) {
        return dsl.select()
                .from(table("activity_registrations"))
                .where(field("activity_date").eq(activityDate))
                .orderBy(field("registration_time").desc())
                .fetchInto(ActivityRegistration.class);
    }

    public long countByActivityDateAndStatus(LocalDate activityDate, String status) {
        return dsl.selectCount()
                .from(table("activity_registrations"))
                .where(field("activity_date").eq(activityDate))
                .and(field("status").eq(status))
                .fetchOne(0, Long.class);
    }

    public ActivityRegistration findById(Long id) {
        return dsl.select()
                .from(table("activity_registrations"))
                .where(field("id").eq(id))
                .fetchOneInto(ActivityRegistration.class);
    }

    public List<ActivityRegistration> findAll() {
        return dsl.select()
                .from(table("activity_registrations"))
                .orderBy(field("registration_time").desc())
                .fetchInto(ActivityRegistration.class);
    }
}



