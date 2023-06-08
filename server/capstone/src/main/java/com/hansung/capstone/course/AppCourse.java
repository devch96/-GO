package com.hansung.capstone.course;

import com.hansung.capstone.community.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class AppCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String coordinates;

    @NotBlank
    private String title;

    @Builder
    public AppCourse(String coordinates, String title){
        this.coordinates = coordinates;
        this.title = title;
    }

}
