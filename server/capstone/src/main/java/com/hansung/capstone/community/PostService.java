package com.hansung.capstone.community;

import com.hansung.capstone.course.UserCourseDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    PostDTO.FreePostResponseDTO createFreeBoardPost(PostDTO.CreateRequestDTO req, List<MultipartFile> files) throws Exception;

    Post createCourseBoardPost(UserCourseDTO.CreateRequestDTO req, List<MultipartFile> files, MultipartFile thumbnail) throws Exception;

    PostDTO.FreePostResponseDTO modifyPost(PostDTO.ModifyRequestDTO req, List<MultipartFile> files) throws Exception;

    Page<Post> getAllPost(int page);

    Page<Post> getBoardPost(int page, String board);


    Page<Post> getUserNickNamePost(String nickname, int page);

    Page<Post> getTitleOrContentPost(String titleOrContent, int page);

    Page<Post> getScrapPost(Long userId, int page);

    PostDTO.FreePostResponseDTO getFreeBoardDetailPost(Long id);

    PostDTO.CoursePostResponseDTO getCourseBoardDetailPost(Long id);

    PostDTO.FreePostResponseDTO setFavorite(Long userId, Long postId);

    PostDTO.FreePostResponseDTO setScrap(Long userId, Long postId);

    void deletePost(Long userId, Long postId) throws Exception;
}
