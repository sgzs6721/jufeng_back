package com.jufeng.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class ActivityRegistrationRequest {
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    @NotBlank(message = "电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入有效的手机号码")
    private String phone;
    
    @NotBlank(message = "请选择课程包")
    private String coursePackage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCoursePackage() {
        return coursePackage;
    }

    public void setCoursePackage(String coursePackage) {
        this.coursePackage = coursePackage;
    }
}

