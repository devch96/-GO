package com.hansung.capstone.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class UserRiding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ridingTime;

    private Float ridingDistance;

    private int calorie;

    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public UserRiding(Long ridingTime, Float ridingDistance, int calorie, User user){
        this.ridingTime = ridingTime;
        this.ridingDistance = ridingDistance;
        this.calorie = calorie;
        this.user = user;
        this.createdDate = LocalDateTime.now();
    }
}
