package com.hansung.capstone.course;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppCourseRepository extends JpaRepository<AppCourse, Long> {

    List<AppCourse> findAll();
}
