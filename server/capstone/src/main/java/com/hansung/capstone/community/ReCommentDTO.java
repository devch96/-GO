package com.hansung.capstone.community;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

public class ReCommentDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "ReCommentCreateDTO")
    public static class CreateRequestDTO{
        @Positive
        Long postId;
        @Positive
        Long commentId;
        @Positive
        Long userId;

        @NotBlank
        String content;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(name = "ReCommentModifyRequestDTO")
    public static class ModifyRequestDTO{
        @Positive
        Long reCommentId;
        @Positive
        Long userId;
        @NotBlank
        String content;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(name = "ReCommentResponseDTO")
    public static class ResponseDTO{
        Long id;
        String content;
        LocalDateTime createdDate;
        LocalDateTime modifiedDate;
        Long userId;
        String userNickname;
        Long userProfileImageId;

        Set<Long> reCommentVoterId;
    }
}
