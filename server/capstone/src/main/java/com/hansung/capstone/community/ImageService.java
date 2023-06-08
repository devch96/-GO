package com.hansung.capstone.community;

import com.hansung.capstone.DataNotFoundException;
import com.hansung.capstone.user.ProfileImage;
import com.hansung.capstone.user.ProfileImageDTO;
import com.hansung.capstone.user.ProfileImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final PostImageRepository postImageRepository;

    private final ProfileImageRepository profileImageRepository;


    /*
        이미지 개별 조회
     */
    @Transactional(readOnly = true)
    public PostImageDTO findByFileId(Long id){
        PostImage postImage = this.postImageRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 파일이 존재하지 않습니다.")
        );

        PostImageDTO postImageDTO = PostImageDTO.builder()
                .originFileName(postImage.getOriginFileName())
                .filePath(postImage.getFilePath())
                .fileSize(postImage.getFileSize()).build();

        return postImageDTO;
    }

    @Transactional(readOnly = true)
    public void deleteImage(Long id){
        PostImage postImage = this.postImageRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("해당 파일이 존재하지 않습니다.")
        );
        File file = new File(postImage.getFilePath());
        file.delete();
        this.postImageRepository.delete(postImage);
    }

    @Transactional(readOnly = true)
    public List<PostImageDTO.ResponseDTO> findAllByPostId(Long postId){
        List<PostImage> postImageList = this.postImageRepository.findAllByPostId(postId);
        return postImageList.stream()
                .map(PostImageDTO.ResponseDTO::new)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public ProfileImageDTO findByImageId(Long id){
        ProfileImage profileImage = this.profileImageRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 파일이 존재하지 않습니다.")
        );

        ProfileImageDTO profileImageDTO = ProfileImageDTO.builder()
                .originFileName(profileImage.getOriginFileName())
                .filePath(profileImage.getFilePath())
                .fileSize(profileImage.getFileSize()).build();

        return profileImageDTO;
    }

}
