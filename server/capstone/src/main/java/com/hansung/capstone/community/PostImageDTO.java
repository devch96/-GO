package com.hansung.capstone.community;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostImageDTO {
    private String originFileName;
    private String filePath;
    private Long fileSize;

    @Builder
    public PostImageDTO(String originFileName, String filePath, Long fileSize){
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    @Getter
    public static class ResponseDTO{
        private Long fileId;
        public ResponseDTO(PostImage entity){
            this.fileId = entity.getId();
        }
    }

}
