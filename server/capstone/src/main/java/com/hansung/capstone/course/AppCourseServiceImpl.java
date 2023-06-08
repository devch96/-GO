package com.hansung.capstone.course;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppCourseServiceImpl implements AppCourseService{

    private final AppCourseRepository appCourseRepository;
    @Override
    public void createAppCourse(AppCourseDTO.createDTO req) {
        AppCourse appCourse =  AppCourse.builder()
                .title(req.getTitle())
                .coordinates(req.getCoordinates())
                .build();

        this.appCourseRepository.save(appCourse);
    }

    @Override
    public List<AppCourse> getAppCourseList() {
        return this.appCourseRepository.findAll();
    }
}
