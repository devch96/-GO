package com.hansung.capstone.user;

import com.hansung.capstone.community.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class ProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_image_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String originFileName;

    private String filePath;

    private Long fileSize;

    @Builder
    public ProfileImage(String originFileName, String filePath, Long fileSize){
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    public void setUser(User user){
        this.user = user;
        if(user.getProfileImage() != this){
            user.addProfileImage(this);
        }
    }


}
