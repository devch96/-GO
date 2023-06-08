package com.hansung.capstone.community;

import com.hansung.capstone.ErrorHandler;
import com.hansung.capstone.response.CommonResponse;
import com.hansung.capstone.response.ResponseService;
import com.hansung.capstone.response.SingleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/recomment")
public class ReCommentController {

    private final ReCommentService reCommentService;

    private final PostService postService;

    private final ResponseService responseService;

    private final ErrorHandler errorHandler;

    @PostMapping("/create")
    private ResponseEntity<CommonResponse> createReComment(@RequestBody ReCommentDTO.CreateRequestDTO req){
        return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(
                this.reCommentService.createReComment(req)), HttpStatus.CREATED);
    }

    @GetMapping("/favorite")
    public ResponseEntity<CommonResponse> reCommentFavorite(
            @RequestParam Long userId,
            @RequestParam Long postId,
            @RequestParam Long reCommentId){
        return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(
                this.reCommentService.setFavorite(userId,postId,reCommentId)), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CommonResponse> deleteReComment(
            @RequestParam Long userId,
            @RequestParam Long reCommentId
    ){
        try{
            this.reCommentService.deleteReComment(userId,reCommentId);
            return new ResponseEntity<>(this.responseService.getSuccessCommonResponse(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(this.responseService.getFailureCommonResponse(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<CommonResponse> modifyReComment(@RequestBody @Valid ReCommentDTO.ModifyRequestDTO req, BindingResult bindingResult){
        try{
            this.reCommentService.modifyReComment(req.getReCommentId(), req.getContent());
            return new ResponseEntity<>(this.responseService.getSuccessCommonResponse(), HttpStatus.OK);
        }catch (Exception e){
            return this.errorHandler.bindingResultErrorCode(bindingResult);
        }
    }
}
