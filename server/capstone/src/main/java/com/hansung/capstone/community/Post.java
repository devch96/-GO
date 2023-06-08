package com.hansung.capstone.community;

import com.hansung.capstone.course.UserCourse;
import com.hansung.capstone.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_Id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private PostCategory postCategory;

    @Column(length = 40)
    @NotBlank
    private String title;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String content;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @OneToMany(mappedBy = "post")
    private List<Comment> commentList;

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)
    private List<PostImage> postImages = new ArrayList<>();

    @ManyToMany
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<User> voter = new HashSet<>();

    @ManyToMany
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<User> scraper = new HashSet<>();

    @OneToOne(mappedBy = "post", cascade = CascadeType.PERSIST)
    private UserCourse userCourse;

    @Builder
    public Post(String title, String content, LocalDateTime createdDate, User author, PostCategory postCategory){
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.author = author;
        this.postCategory = postCategory;
    }

    public void modify(String title, String content, LocalDateTime modifiedDate){
        this.title = title;
        this.content = content;
        this.modifiedDate = modifiedDate;
    }

    public void addImage(PostImage postImage){
        this.postImages.add(postImage);

        if(postImage.getPost() != this) {
            postImage.setPost(this);
        }
    }

}
