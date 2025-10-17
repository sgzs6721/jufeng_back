-- 创建活动报名表
CREATE TABLE activity_registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '报名者姓名',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    course_package VARCHAR(50) NOT NULL COMMENT '选择的课程包：PACKAGE_30 或 PACKAGE_60',
    activity_name VARCHAR(200) NOT NULL COMMENT '活动名称',
    activity_date DATE NOT NULL COMMENT '活动日期',
    registration_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '报名状态：PENDING-待处理, CONFIRMED-已确认, CANCELLED-已取消',
    remark TEXT COMMENT '备注信息',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_phone (phone),
    INDEX idx_activity_date (activity_date),
    INDEX idx_status (status),
    INDEX idx_registration_time (registration_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动报名表';

