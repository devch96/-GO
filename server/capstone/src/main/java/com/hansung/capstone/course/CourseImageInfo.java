package com.hansung.capstone.course;

import com.hansung.capstone.community.PostImage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CourseImageInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String coordinate;

    private String placeName;

    private String placeLink;

    @OneToOne(cascade = CascadeType.PERSIST)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "post_image_id")
    private PostImage postImage;

    @Builder
    public CourseImageInfo(String coordinate, String placeName, String placeLink, PostImage postImage){
        this.coordinate = coordinate;
        this.placeName = placeName;
        this.placeLink = placeLink;
        this.postImage = postImage;
    }
}
