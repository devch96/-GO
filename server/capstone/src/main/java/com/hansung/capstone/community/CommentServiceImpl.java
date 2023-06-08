package com.hansung.capstone.community;

import com.hansung.capstone.user.AuthService;
import com.hansung.capstone.user.User;
import com.hansung.capstone.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.sasl.AuthenticationException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final PostServiceImpl postService;

    private final AuthService authService;

    @Override
    public PostDTO.FreePostResponseDTO createComment(CommentDTO.CreateRequestDTO req) {
        Comment comment = Comment.builder()
                .content(req.getContent())
                .createdDate(LocalDateTime.now())
                .post(this.postRepository.findById(req.getPostId()).get())
                .author(this.userRepository.findById(req.getUserId()).get()).build();
        this.commentRepository.save(comment);
        Post post = this.postRepository.findById(req.getPostId()).get();
        return this.postService.createFreeBoardResponse(post);
    }

    @Override
    @Transactional
    public PostDTO.FreePostResponseDTO modifyComment(Long commentId, String content) {
        Optional<Comment> comment = this.commentRepository.findById(commentId);
        comment.ifPresent(s->{
            comment.get().modify(content, LocalDateTime.now());
                }
        );
        return this.postService.createFreeBoardResponse(comment.get().getPost());
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) throws Exception {
        Comment comment = this.commentRepository.findById(commentId).orElseThrow( () ->
                new IllegalArgumentException("댓글이 존재하지 않습니다.")
        );
        if(authService.checkIdAndToken(userId) && userId.equals(comment.getAuthor().getId())){
            if(comment.getReCommentList().isEmpty()){
                this.commentRepository.deleteById(commentId);
            }
            else{
                comment.modify("<--!Has Been Deleted!-->", LocalDateTime.now(), Boolean.TRUE);
            }
        }else{
            throw new AuthenticationException("유저 정보와 토큰 값이 일치하지 않습니다.");
        }
    }

    @Override
    @Transactional
    public PostDTO.FreePostResponseDTO setFavorite(Long userId, Long postId, Long commentId) {
        Post post = this.postRepository.findById(postId).orElseThrow( () ->
                new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );

        User user = this.userRepository.findById(userId).orElseThrow( () ->
                new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        Comment comment = this.commentRepository.findById(commentId).orElseThrow( () ->
                new IllegalArgumentException("댓글이 존재하지 않습니다.")
        );
        if(comment.getVoter().contains(user)){
            comment.getVoter().remove(user);
        }else{
            comment.getVoter().add(user);
        }
        return this.postService.createFreeBoardResponse(post);
    }
}
