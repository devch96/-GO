package com.hansung.capstone.community;

import com.hansung.capstone.user.ProfileImage;
import com.hansung.capstone.user.ProfileImageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ImageHandler {
    private final ImageService imageService;

    public ImageHandler(ImageService imageService) {
        this.imageService = imageService;
    }

    public List<PostImage> parsePostImageInfo(List<MultipartFile> multipartFiles) throws Exception {
        List<PostImage> postImageList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(multipartFiles)) {
            for (MultipartFile multipartFile : multipartFiles) {
                postImageList.add(parseImageInfo(multipartFile));
            }
        }
        return postImageList;
    }

    public PostImage parseImageInfo(MultipartFile multipartFile) throws Exception {
        if (multipartFile != null) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter =
                    DateTimeFormatter.ofPattern("yyyyMMdd");
            String current_date = now.format(dateTimeFormatter);

            String absolutePath = new File("").getAbsolutePath() + File.separator;

            String path = "images/post" + File.separator + current_date;
            File file = new File(path);

            if (!file.exists()) {
                boolean wasSuccessful = file.mkdirs();
                if (!wasSuccessful) {
                    log.info("file: was not successful");
                }
            }

            String contentType = multipartFile.getContentType();
            String extension;
            String originalFileName = multipartFile.getOriginalFilename();
            String originalFileExtension = FilenameUtils.getExtension(originalFileName);
            if (ObjectUtils.isEmpty(contentType)) {
                throw new RuntimeException();
            } else {  // 확장자가 jpeg, png인 파일들만 받아서 처리
                extension = fileExtensionCheck(originalFileExtension);
                if (extension == null) {
                    throw new RuntimeException();
                }
            }

            String new_file_name = System.nanoTime() + extension;

            PostImageDTO postImageDTO = PostImageDTO.builder()
                    .originFileName(multipartFile.getOriginalFilename())
                    .filePath(path + File.separator + new_file_name)
                    .fileSize(multipartFile.getSize()).build();

            PostImage postImage = new PostImage(
                    postImageDTO.getOriginFileName(),
                    postImageDTO.getFilePath(),
                    postImageDTO.getFileSize()
            );

            file = new File(absolutePath + path + File.separator + new_file_name);
            multipartFile.transferTo(file);

            file.setWritable(true);
            file.setReadable(true);
            return postImage;
        } else {
            throw new RuntimeException();
        }
    }

    public ProfileImage parseProfileImageInfo(MultipartFile multipartFile) throws Exception {
        if(multipartFile != null){
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter =
                    DateTimeFormatter.ofPattern("yyyyMMdd");
            String current_date = now.format(dateTimeFormatter);

            String absolutePath = new File("").getAbsolutePath() + File.separator;

            String path = "images/profile" + File.separator + current_date;
            File file = new File(path);

            if(!file.exists()) {
                boolean wasSuccessful = file.mkdirs();
                if(!wasSuccessful) {
                    log.info("file: was not successful");
                }
            }

                String contentType = multipartFile.getContentType();
                String extension;
                String originalFileName = multipartFile.getOriginalFilename();
                String originalFileExtension = FilenameUtils.getExtension(originalFileName);
                if(ObjectUtils.isEmpty(contentType)){
                    throw new RuntimeException();
                }
                else {  // 확장자가 jpeg, png인 파일들만 받아서 처리
                    extension = fileExtensionCheck(originalFileExtension);
                    if (extension == null){
                        throw new RuntimeException();
                    }
                }

                String new_file_name = System.nanoTime() + extension;

                ProfileImageDTO profileImageDTO = ProfileImageDTO.builder()
                        .originFileName(multipartFile.getOriginalFilename())
                        .filePath(path + File.separator + new_file_name)
                        .fileSize(multipartFile.getSize()).build();

                ProfileImage profileImage = new ProfileImage(
                        profileImageDTO.getOriginFileName(),
                        profileImageDTO.getFilePath(),
                        profileImageDTO.getFileSize()
                );

                file = new File(absolutePath + path + File.separator + new_file_name);
                multipartFile.transferTo(file);

                file.setWritable(true);
                file.setReadable(true);
                return profileImage;
            }else{
            throw new RuntimeException();
        }
    }

    private String fileExtensionCheck(String originFileExtension){
        if(originFileExtension.contains("jpeg") || originFileExtension.contains("jpg")){
            return ".jpeg";
        } else if (originFileExtension.contains("png")) {
            return ".png";
        }
        return null;
    }
}
