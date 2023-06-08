package com.hansung.capstone.community;

public interface CommentService {

    PostDTO.FreePostResponseDTO createComment(CommentDTO.CreateRequestDTO req);

    PostDTO.FreePostResponseDTO setFavorite(Long userId, Long postId, Long commentId);

    PostDTO.FreePostResponseDTO modifyComment(Long id, String content);

    void deleteComment(Long userId, Long commentId) throws Exception;
}
