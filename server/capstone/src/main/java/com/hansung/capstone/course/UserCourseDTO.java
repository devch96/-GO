package com.hansung.capstone.course;

import com.hansung.capstone.community.CommentDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class UserCourseDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "CourseCreateDTO")
    public static class CreateRequestDTO{
        @NotBlank
        private String coordinates;
        @NotBlank
        private String region;
        @NotBlank
        private String originToDestination;
        @Positive
        private Long userId;
        @NotBlank
        private String category;
        @NotBlank
        private String title;
        @NotBlank
        private String content;

        private List<ImageInfo> imageInfoList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(name = "CourseResponseDTO")
    public static class CourseResponseDTO{
        private Long courseId;
        private String coordinates;
        private String region;
        private String originToDestination;
        private Long postId;
        private Long thumbnailId;
        private List<Long> imageId;
        private List<ImageInfo> imageInfoList;
        private int numOfFavorite;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CourseDetailResponseDTO {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;
        private Long authorId;
        private String nickname;
        private Long authorProfileImageId;
        private List<CommentDTO.ResponseDTO> commentList;
        private List<Long> imageId;
        private Set<Long> postVoterId;
        private Set<Long> postScraperId;

        private List<String> waypoint;

    }
}
