package com.hansung.capstone.course;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseImageInfoRepository extends JpaRepository<CourseImageInfo, Long> {

    Optional<CourseImageInfo> findByPostImageId(Long imageId);
}
