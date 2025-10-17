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
            org.jooq.Record1<Long> result = dsl.insertInto(table("activity_registrations"))
                    .set(field("name"), registration.getName())
                    .set(field("phone"), registration.getPhone())
                    .set(field("course_package"), registration.getCoursePackage())
                    .set(field("activity_name"), registration.getActivityName())
                    .set(field("activity_date"), registration.getActivityDate())
                    .set(field("registration_time"), registration.getRegistrationTime())
                    .set(field("status"), registration.getStatus())
                    .set(field("remark"), registration.getRemark())
                    .returningResult(field("id", Long.class))
                    .fetchOne();
            
            if (result == null) {
                throw new RuntimeException("插入记录失败：未返回结果");
            }
            
            Long id = result.value1();
            if (id == null) {
                throw new RuntimeException("插入记录失败：未获取到ID");
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



