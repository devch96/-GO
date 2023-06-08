package com.hansung.capstone.community;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hansung.capstone.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String content;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @OneToMany(mappedBy = "comment")
    private List<ReComment> reCommentList;

    @ManyToMany
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Column(name = "comment_id")
    private Set<User> voter = new HashSet<>();

    @Builder
    public Comment(String content, LocalDateTime createdDate, Post post, User author){
        this.content = content;
        this.createdDate = createdDate;
        this.post = post;
        this.author = author;
    }


    public void modify(String content, LocalDateTime modifiedDate){
        this.content = content;
        this.modifiedDate = modifiedDate;
    }

    public void modify(String content, LocalDateTime modifiedDate, Boolean deleted){
        this.content = content;
        this.modifiedDate = modifiedDate;
        if (deleted == true){
            this.author = null;
        }
    }

}
