package com.hansung.capstone.course;

import com.hansung.capstone.community.Post;
import com.hansung.capstone.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class UserCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String coordinates;

    private String region;

    private String originToDestination;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    private LocalDateTime createdDate;

    @ManyToMany
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<User> scraper = new HashSet<>();

    @Builder
    public UserCourse(String coordinates, Post post, String region, String originToDestination){
        this.coordinates = coordinates;
        this.post = post;
        this.region = region;
        this.originToDestination = originToDestination;
        this.createdDate = LocalDateTime.now();
    }
}
