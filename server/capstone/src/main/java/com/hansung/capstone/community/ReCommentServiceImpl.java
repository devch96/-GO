package com.hansung.capstone.community;

import com.hansung.capstone.DataNotFoundException;
import com.hansung.capstone.user.AuthService;
import com.hansung.capstone.user.User;
import com.hansung.capstone.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReCommentServiceImpl implements ReCommentService{

    private final CommentRepository commentRepository;

    private final ReCommentRepository reCommentRepository;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final AuthService authService;

    private final PostServiceImpl postService;
    @Override
    public PostDTO.FreePostResponseDTO createReComment(ReCommentDTO.CreateRequestDTO req) {
        ReComment reComment = ReComment.builder()
                .content(req.getContent())
                .createdDate(LocalDateTime.now())
                .comment(this.commentRepository.findById(req.getCommentId()).orElseThrow( () ->
                        new IllegalArgumentException("댓글이 존재하지 않습니다.")
                ))
                .author(this.userRepository.findById(req.getUserId()).orElseThrow( () ->
                        new IllegalArgumentException("유저가 존재하지 않습니다.")
                )).build();
        this.reCommentRepository.save(reComment);
        Post post = this.postRepository.findById(req.getPostId()).orElseThrow( () ->
                new IllegalArgumentException("게시글이 존재하지 않습니다.")
                );
        return this.postService.createFreeBoardResponse(post);
    }

    @Override
    @Transactional
    public PostDTO.FreePostResponseDTO setFavorite(Long userId, Long postId, Long reCommentId) {
        Post post = this.postRepository.findById(postId).orElseThrow( () ->
                new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );

        User user = this.userRepository.findById(userId).orElseThrow( () ->
                new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        ReComment reComment = this.reCommentRepository.findById(reCommentId).orElseThrow( () ->
                new IllegalArgumentException("댓글이 존재하지 않습니다.")
        );
        if(reComment.getVoter().contains(user)){
            reComment.getVoter().remove(user);
        }else{
            reComment.getVoter().add(user);
        }
        return this.postService.createFreeBoardResponse(post);
    }

    @Override
    @Transactional
    public PostDTO.FreePostResponseDTO modifyReComment(Long reCommentId, String content) {
        ReComment reComment = this.reCommentRepository.findById(reCommentId).orElseThrow(() ->
                new DataNotFoundException("댓글이 존재하지 않습니다."));
        reComment.modify(content, LocalDateTime.now());
        return this.postService.createFreeBoardResponse(reComment.getComment().getPost());
    }

    @Override
    public void deleteReComment(Long userId, Long reCommentId) throws Exception {
        ReComment reComment = this.reCommentRepository.findById(reCommentId).orElseThrow( () ->
                new IllegalArgumentException("댓글이 존재하지 않습니다.")
        );
        if(authService.checkIdAndToken(userId) && reCommentId.equals(reComment.getId())){
            if (reComment.getComment().getReCommentList().size() == 1 && reComment.getComment().getContent().equals("<--!Has Been Deleted!-->")){
                this.commentRepository.deleteById(reComment.getComment().getId());
            }
            else{
                this.reCommentRepository.deleteById(reCommentId);

            }
        }else{
            throw new AuthenticationException();
        }
    }
}
