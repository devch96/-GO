package com.hansung.capstone.user;

import com.hansung.capstone.community.PostImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileImageDTO {
    private String originFileName;
    private String filePath;
    private Long fileSize;

    @Builder
    public ProfileImageDTO(String originFileName, String filePath, Long fileSize){
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    @Getter
    public static class ResponseDTO{
        private Long fileId;
        public ResponseDTO(ProfileImage entity){
            this.fileId = entity.getId();
        }
    }

}
