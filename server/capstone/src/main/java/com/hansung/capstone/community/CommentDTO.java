package com.hansung.capstone.community;

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

public class CommentDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "CommentCreateDTO")
    public static class CreateRequestDTO {
        @Positive
        Long postId;
        @Positive
        Long userId;
        @NotBlank
        String content;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "CommentModifyRequestDTO")
    public static class ModifyRequestDTO {
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
    @Schema(name = "CommentResponseDTO")
    public static class ResponseDTO{
        Long id;
        String content;
        LocalDateTime createdDate;
        LocalDateTime modifiedDate;
        Long userId;
        String userNickname;
        Long userProfileImageId;
        List<ReCommentDTO.ResponseDTO> reCommentList;
        Set<Long> commentVoterId;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class commentResponseDTO{
        private Long id;
        private String title;
        private String content;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;
        private Long authorId;
        private String nickname;
        private List<ResponseDTO> commentList;
    }
}
