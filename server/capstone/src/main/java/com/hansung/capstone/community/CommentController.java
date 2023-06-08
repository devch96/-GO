package com.hansung.capstone.community;

import com.hansung.capstone.ErrorHandler;
import com.hansung.capstone.response.CommonResponse;
import com.hansung.capstone.response.ResponseService;
import com.hansung.capstone.response.SingleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/comment")
public class CommentController {

    private final CommentService commentService;

    private final PostService postService;

    private final ResponseService responseService;

    private final ErrorHandler errorHandler;

    @PostMapping("/create")
    private ResponseEntity<CommonResponse> createComment(@RequestBody @Valid CommentDTO.CreateRequestDTO req, BindingResult bindingResult){
        try {
            this.commentService.createComment(req);
            return new ResponseEntity<>(this.responseService.getSuccessCommonResponse(), HttpStatus.CREATED);
        }catch (Exception e){
            return this.errorHandler.bindingResultErrorCode(bindingResult);
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<CommonResponse> modifyComment(@RequestBody @Valid CommentDTO.ModifyRequestDTO req, BindingResult bindingResult){

        try {
            this.commentService.modifyComment(req.getCommentId(), req.getContent());
            return new ResponseEntity<>(this.responseService.getSuccessCommonResponse(), HttpStatus.OK);
        }catch (Exception e){
            return this.errorHandler.bindingResultErrorCode(bindingResult);
        }
    }

    @GetMapping("/favorite")
    public ResponseEntity<CommonResponse> commentFavorite(
            @RequestParam Long userId,
            @RequestParam Long postId,
            @RequestParam Long commentId){
        return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(this.commentService.setFavorite(userId,postId,commentId)), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CommonResponse> deleteComment(
            @RequestParam Long userId,
            @RequestParam Long commentId
    ){
        try{
            this.commentService.deleteComment(userId,commentId);
            return new ResponseEntity<>(this.responseService.getSuccessCommonResponse(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(this.responseService.getFailureSingleResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


}
