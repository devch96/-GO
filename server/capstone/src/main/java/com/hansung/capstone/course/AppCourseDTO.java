package com.hansung.capstone.course;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AppCourseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "AppCourseCreateDTO")
    public static class createDTO{
        private String coordinates;
        private String title;
    }


}
