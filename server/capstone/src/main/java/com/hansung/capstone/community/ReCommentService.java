package com.hansung.capstone.community;

public interface ReCommentService {

    PostDTO.FreePostResponseDTO createReComment(ReCommentDTO.CreateRequestDTO req);

    PostDTO.FreePostResponseDTO setFavorite(Long userId, Long postId, Long reCommentId);

    PostDTO.FreePostResponseDTO modifyReComment(Long reCommentId, String content);

    void deleteReComment(Long userId, Long reCommentId) throws Exception;
}
