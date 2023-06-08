package com.hansung.capstone.course;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CourseImageInfoDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "CourseImageInfoResponseDTO")
    public static class responseDTO{
        private String coordinate;
        private String placeName;
        private String placeLink;
    }
}
