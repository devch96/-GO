package com.hansung.capstone.community;

import com.hansung.capstone.response.*;
import com.hansung.capstone.user.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/community/post")
@RequiredArgsConstructor
public class PostController {

    private final PostServiceImpl postService;

    private final ResponseService responseService;

    private final AuthService authService;

    private final PostRepository postRepository;



    @PostMapping("/create")
    public ResponseEntity<CommonResponse> createPost(
            @RequestPart(value = "requestDTO") PostDTO.CreateRequestDTO req,
            @RequestPart(value = "imageList", required = false) List<MultipartFile> files)  {
        try {
            this.postService.createFreeBoardPost(req, files);
            return new ResponseEntity<>(this.responseService.getSuccessCommonResponse(), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<SingleResponse<PostDTO.FreePostResponseDTO>> modifyPost(
            @RequestPart(value = "requestDTO") PostDTO.ModifyRequestDTO req,
            @RequestPart(value = "imageList", required = false) List<MultipartFile> files ) throws Exception {
        return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(this.postService.modifyPost(req, files)), HttpStatus.OK);
    }

    @GetMapping("/list/all")
    public ResponseEntity<PageResponse<PostDTO.FreePostResponseDTO>> getAllPost(@RequestParam(defaultValue = "0") int page) {
        Page<Post> paging = this.postService.getAllPost(page);
        return new ResponseEntity<>(this.responseService.getPageResponse(paging.getTotalPages(),preProcess(paging)), HttpStatus.OK);
    }

    @GetMapping("/list/free")
    public ResponseEntity<PageResponse<PostDTO.FreePostResponseDTO>> getFreeBoardPost(@RequestParam(defaultValue = "0") int page){
        Page<Post> paging = this.postService.getBoardPost(page,"FREE");
        return new ResponseEntity<>(this.responseService.getPageResponse(paging.getTotalPages(),preProcess(paging)), HttpStatus.OK);
    }

    @GetMapping("/list/course")
    public ResponseEntity<PageResponse<PostDTO.FreePostResponseDTO>> getCourseBoardPost(@RequestParam(defaultValue = "0") int page){
        Page<Post> paging = this.postService.getBoardPost(page,"COURSE");
        return new ResponseEntity<>(this.responseService.getPageResponse(paging.getTotalPages(),preProcess(paging)), HttpStatus.OK);
    }


    @GetMapping("/detail")
    public ResponseEntity<SingleResponse> getDetailPost(@RequestParam Long id){
        String category = this.postRepository.findById(id).get().getPostCategory().getKey();
        if(category.equals("FREE")) {
            return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(this.postService.getFreeBoardDetailPost(id)), HttpStatus.OK);
        }else if(category.equals("COURSE")){
            return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(this.postService.getCourseBoardDetailPost(id)), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(this.responseService.getFailureSingleResponse(null), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/test")
    public String coursePost(@RequestBody String coordinates){
        return coordinates.replaceAll("\\\\","");
    }

    @GetMapping("/list/nickname")
    public ResponseEntity<CommonResponse> getUserNickNamePost(
            @RequestParam String nickname,
            @RequestParam(defaultValue = "0") int page) {
        Page<Post> paging = this.postService.getUserNickNamePost(nickname, page);
        return new ResponseEntity<>(this.responseService.getPageResponse(paging.getTotalPages(),preProcess(paging)), HttpStatus.OK);
    }

    @GetMapping("/list/title-or-content")
    public ResponseEntity<CommonResponse> getTitleOrContentPost(
            @RequestParam String titleOrContent,
            @RequestParam(defaultValue = "0") int page) {
        try{
            Page<Post> paging = this.postService.getTitleOrContentPost(titleOrContent, page);
            return new ResponseEntity<>(this.responseService.getPageResponse(paging.getTotalPages(),preProcess(paging)), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(this.responseService.getFailureSingleResponse(e.getMessage()), HttpStatus.OK);
        }
    }

    @GetMapping("/list/scrap")
    public ResponseEntity<CommonResponse> getScrapPost(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page){
        try{
            Page<Post> paging = this.postService.getScrapPost(userId,page);
            return new ResponseEntity<>(this.responseService.getPageResponse(paging.getTotalPages(),preProcess(paging)), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(this.responseService.getFailureSingleResponse(e.getMessage()), HttpStatus.OK);
        }
    }

    @GetMapping("/favorite")
    public ResponseEntity<CommonResponse> postFavorite(
            @RequestParam Long userId,
            @RequestParam Long postId){
        return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(this.postService.setFavorite(userId,postId)), HttpStatus.OK);
    }

    @GetMapping("/scrap")
    public ResponseEntity<CommonResponse> postScrap(
            @RequestParam Long userId,
            @RequestParam Long postId){
        return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(this.postService.setScrap(userId, postId)), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CommonResponse> deletePost(
            @RequestParam Long userId,
            @RequestParam Long postId
    ){
        try{
            this.postService.deletePost(userId,postId);
            return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(null), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(this.responseService.getFailureSingleResponse(null), HttpStatus.BAD_REQUEST);
        }
    }

    private List<PostDTO.FreePostResponseDTO> preProcess(Page<Post> paging){
        List<PostDTO.FreePostResponseDTO> res = new ArrayList<>();
        for(Post post : paging){
            res.add(this.postService.createFreeBoardResponse(post));
        }
        return res;
    }
}
