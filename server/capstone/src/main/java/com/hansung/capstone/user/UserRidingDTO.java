package com.hansung.capstone.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

public class UserRidingDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordDTO {
        private Long ridingTime;
        private Float ridingDistance;
        private int calorie;
        private Long userId;
    }

    @Builder
    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    public static class HistoryResponseDTO{

        private String createdDate;
        private Long ridingTime;
        private Float ridingDistance;
        private int calorie;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankResponseDTO{
        private Long profileImageId;
        private String userNickname;
        private Float totalDistance;
        private int distanceRank;
    }
}
