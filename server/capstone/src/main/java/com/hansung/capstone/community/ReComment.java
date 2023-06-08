package com.hansung.capstone.community;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hansung.capstone.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionException;

@Entity
@Getter
@NoArgsConstructor
public class ReComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "re_comment_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String content;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "comment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<User> voter = new HashSet<>();

    @Builder
    public ReComment(String content, LocalDateTime createdDate, Comment comment, User author){
        this.content = content;
        this.createdDate = createdDate;
        this.comment = comment;
        this.author = author;
    }

    public void modify(String content, LocalDateTime modifiedDate){
        this.content = content;
        this.modifiedDate = modifiedDate;
    }
}
