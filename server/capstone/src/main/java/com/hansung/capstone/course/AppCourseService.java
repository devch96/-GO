package com.hansung.capstone.course;

import java.util.List;

public interface AppCourseService {

    void createAppCourse(AppCourseDTO.createDTO req);

    List<AppCourse> getAppCourseList();
}
