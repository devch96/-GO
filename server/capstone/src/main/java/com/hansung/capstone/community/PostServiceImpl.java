package com.hansung.capstone.community;

import com.hansung.capstone.DataNotFoundException;
import com.hansung.capstone.course.*;
import com.hansung.capstone.user.AuthService;
import com.hansung.capstone.user.User;
import com.hansung.capstone.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final PostImageRepository postImageRepository;

    private final AuthService authService;

    private final ImageHandler imageHandler;

    private final ImageService imageService;

    private final CourseImageInfoRepository courseImageInfoRepository;

    private final UserCourseRepository userCourseRepository;


    @Transactional
    @Override
    public PostDTO.FreePostResponseDTO createFreeBoardPost(PostDTO.CreateRequestDTO req, List<MultipartFile> files) throws Exception {
        if (req.getCategory().equals("FREE")) {
            Post newPost = Post.builder()
                    .title(req.getTitle())
                    .content(req.getContent())
                    .createdDate(LocalDateTime.now())
                    .author(this.userRepository.findById(req.getUserId()).orElseThrow(() ->
                            new DataNotFoundException("존재하지 않는 사용자입니다.")))
                    .postCategory(PostCategory.FREE)
                    .build();
            List<PostImage> postImageList = imageHandler.parsePostImageInfo(files);

            if (!postImageList.isEmpty()) {
                for (PostImage postImage : postImageList) {
                    newPost.addImage(postImageRepository.save(postImage));
                }
            }
            return createFreeBoardResponse(this.postRepository.save(newPost));
        } else {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
    }

    @Override
    public Post createCourseBoardPost(UserCourseDTO.CreateRequestDTO req, List<MultipartFile> files, MultipartFile thumbnail) throws Exception {
        if (req.getCategory().equals("COURSE")) {
            Post newPost = Post.builder()
                    .title(req.getTitle())
                    .content(req.getContent())
                    .createdDate(LocalDateTime.now())
                    .author(this.userRepository.findById(req.getUserId()).orElseThrow(() ->
                            new DataNotFoundException("존재하지 않는 사용자입니다.")))
                    .postCategory(PostCategory.COURSE)
                    .build();
            PostImage thumbnailImage = this.imageHandler.parseImageInfo(thumbnail);
            this.postImageRepository.save(thumbnailImage);
            newPost.addImage(thumbnailImage);
            List<PostImage> postImageList = this.imageHandler.parsePostImageInfo(files);

            if (!postImageList.isEmpty()) {
                for (PostImage postImage : postImageList) {
                    if (!req.getImageInfoList().isEmpty()) {
                        PostImage image = this.postImageRepository.save(postImage);
                        ImageInfo imageInfo = req.getImageInfoList().remove(0);
                        CourseImageInfo courseImageInfo = CourseImageInfo.builder()
                                .coordinate(imageInfo.getCoordinate())
                                .placeLink(imageInfo.getPlaceLink())
                                .placeName(imageInfo.getPlaceName())
                                .postImage(image).build();
                        this.courseImageInfoRepository.save(courseImageInfo);
                        newPost.addImage(image);
                    }
                }
            }
            return this.postRepository.save(newPost);
        } else {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
    }

    @Transactional
    @Override
    public PostDTO.FreePostResponseDTO modifyPost(PostDTO.ModifyRequestDTO req, List<MultipartFile> files) throws Exception {
        Post modifyPost = this.postRepository.findById(req.getPostId()).orElseThrow(
                () -> new DataNotFoundException("게시글이 존재하지 않습니다.")
        );
        List<PostImage> dbPostImageList = this.postImageRepository.findAllByPostId(req.getPostId());
        List<Long> dbPostImageId = new ArrayList<>();
        for (PostImage postImage : dbPostImageList) {
            dbPostImageId.add(postImage.getId());
        }

        dbPostImageId.removeAll(req.getImageId());
        if (!dbPostImageId.isEmpty()) {
            for (Long id : dbPostImageId) {
                this.imageService.deleteImage(id);
            }
        }


//        List<MultipartFile> addFileList = new ArrayList<>();
//        if(CollectionUtils.isEmpty(dbPostImageList)){ // db에 존재 x
//            if(!CollectionUtils.isEmpty(files)){ // 전달 file 존재
//                for (MultipartFile multipartFile : files){
//                    addFileList.add(multipartFile);
//                }
//            }
//        }
//        else{ // DB에 한장이상 존재
//            if(CollectionUtils.isEmpty(files)){ // 전달 file x
//                for(PostImage dbPostImage : dbPostImageList){
//                    this.postImageRepository.deleteById(dbPostImage.getId());
//                }
//            }
//            else{
//                List<String> dbOriginNameList = new ArrayList<>();
//                for(PostImage dbPostImage : dbPostImageList){
//                    PostImageDTO dbPostImageDTO = this.imageService.findByFileId(dbPostImage.getId());
//                    String dbOriginName = dbPostImageDTO.getOriginFileName();
//                    if(!files.contains(dbOriginName)){
//                        this.postImageRepository.deleteById(dbPostImage.getId());
//                    } else{
//                        dbOriginNameList.add(dbOriginName);
//                    }
//                }
//                for (MultipartFile multipartFile : files){
//                    String multipartOriginName = multipartFile.getOriginalFilename();
//                    if(!dbOriginNameList.contains(multipartOriginName)){
//                        addFileList.add(multipartFile);
//                    }
//                }
//            }
//        }
        List<PostImage> postImageList = imageHandler.parsePostImageInfo(files);

        if (!postImageList.isEmpty()) {
            for (PostImage postImage : postImageList) {
                modifyPost.addImage(postImageRepository.save(postImage));
            }
        }

        modifyPost.modify(req.getTitle(), req.getContent(), LocalDateTime.now());
        return createFreeBoardResponse(this.postRepository.findById(req.getPostId()).get());
    }

    @Override
    public void deletePost(Long userId, Long postId) throws Exception {
        Long postAuthorId = this.postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글이 존재하지 않습니다.")
        ).getAuthor().getId();
        if (authService.checkIdAndToken(userId) && userId.equals(postAuthorId)) {
            this.postRepository.deleteById(postId);
        } else {
            throw new AuthenticationException();
        }
    }

    @Override
    public Page<Post> getAllPost(int page) {
        return this.postRepository.findAll(sortBy(page, "createdDate"));
    }

    @Override
    public Page<Post> getBoardPost(int page, String board) {
        if (board.equals("FREE")) {
            return this.postRepository.findAllByPostCategory(sortBy(page, "createdDate"), PostCategory.FREE);
        } else {
            return this.postRepository.findAllByPostCategory(sortBy(page, "createdDate"), PostCategory.COURSE);
        }
    }

    @Override
    public Page<Post> getUserNickNamePost(String nickname, int page) {
        User user = this.userRepository.findByNickname(nickname).orElseThrow(() ->
                new IllegalArgumentException("사용자가 존재하지 않습니다."));
        return this.postRepository.findAllByAuthor(user, sortBy(page, "createdDate"));
    }

    @Override
    public Page<Post> getTitleOrContentPost(String titleOrContent, int page) {
        return this.postRepository.findAllSearch(titleOrContent, sortBy(page, "createdDate"));
    }

    @Override
    public Page<Post> getScrapPost(Long userId, int page) {
        return this.postRepository.findAllScrap(userId, sortBy(page, "created_date"));
    }

    @Override
    public PostDTO.FreePostResponseDTO getFreeBoardDetailPost(Long id) {
        return createFreeBoardResponse(this.postRepository.findById(id).get());
    }

    @Override
    public PostDTO.CoursePostResponseDTO getCourseBoardDetailPost(Long id) {
        return createCourseBoardResponse(this.postRepository.findById(id).get());
    }

    @Transactional
    @Override
    public PostDTO.FreePostResponseDTO setFavorite(Long userId, Long postId) {
        Post post = this.postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );

        User user = this.userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        if (post.getVoter().contains(user)) {
            post.getVoter().remove(user);
        } else {
            post.getVoter().add(user);
        }
        return createFreeBoardResponse(this.postRepository.findById(postId).get());
    }

    @Transactional
    @Override
    public PostDTO.FreePostResponseDTO setScrap(Long userId, Long postId) {
        Post post = this.postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글이 존재하지 않습니다."));

        User user = this.userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("유저가 존재하지 않습니다."));
        if (post.getScraper().contains(user)) {
            post.getScraper().remove(user);
        } else {
            post.getScraper().add(user);
        }
        return createFreeBoardResponse(this.postRepository.findById(postId).get());
    }

    public PostDTO.FreePostResponseDTO createFreeBoardResponse(Post req) {
        List<CommentDTO.ResponseDTO> comments = new ArrayList<>();
        Long profileImageId;
        if (req.getAuthor().getProfileImage() != null) {
            profileImageId = req.getAuthor().getProfileImage().getId();
        } else {
            profileImageId = -1L;
        }
        if (req.getCommentList() != null) {
            for (int i = 0; i < req.getCommentList().size(); i++) {
                Comment comment = req.getCommentList().get(i);
                List<ReCommentDTO.ResponseDTO> reCommentList = new ArrayList<>();
                Set<Long> commentVoterId = new HashSet<>();

                if (comment.getVoter() != null) {
                    Set<User> commentVoter = comment.getVoter();
                    for (User user : commentVoter) {
                        commentVoterId.add(user.getId());
                    }
                }
                if (comment.getReCommentList() != null) {
                    for (int j = 0; j < comment.getReCommentList().size(); j++) {
                        ReComment reComment = comment.getReCommentList().get(j);
                        Long reCommentProfileImageId;
                        if (reComment.getAuthor().getProfileImage() != null) {
                            reCommentProfileImageId = reComment.getAuthor().getProfileImage().getId();
                        } else {
                            reCommentProfileImageId = -1L;
                        }
                        Set<Long> reCommentVoterId = new HashSet<>();
                        if (reComment.getVoter() != null) {
                            Set<User> reCommentVoter = reComment.getVoter();
                            for (User user : reCommentVoter) {
                                reCommentVoterId.add(user.getId());
                            }
                        }
                        ReCommentDTO.ResponseDTO reCommentRes = ReCommentDTO.ResponseDTO.builder()
                                .id(reComment.getId())
                                .content(reComment.getContent())
                                .createdDate(reComment.getCreatedDate())
                                .modifiedDate(reComment.getModifiedDate())
                                .userId(reComment.getAuthor().getId())
                                .userNickname(reComment.getAuthor().getNickname())
                                .userProfileImageId(reCommentProfileImageId)
                                .reCommentVoterId(reCommentVoterId)
                                .build();
                        reCommentList.add(reCommentRes);
                    }
                }
                Long commentProfileImageId = -1L;
                Long commentAuthorId = -1L;
                String commentAuthorNickname = "***";
                if (comment.getAuthor() != null) {
                    commentAuthorId = comment.getAuthor().getId();
                    commentAuthorNickname = comment.getAuthor().getNickname();
                    if (comment.getAuthor().getProfileImage() != null) {
                        commentProfileImageId = comment.getAuthor().getProfileImage().getId();
                    }
                }
                CommentDTO.ResponseDTO commentRes = CommentDTO.ResponseDTO.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .createdDate(comment.getCreatedDate())
                        .modifiedDate(comment.getModifiedDate())
                        .userId(commentAuthorId)
                        .userNickname(commentAuthorNickname)
                        .userProfileImageId(commentProfileImageId)
                        .reCommentList(reCommentList)
                        .commentVoterId((commentVoterId))
                        .build();
                comments.add(commentRes);
            }
        }

        List<PostImageDTO.ResponseDTO> imageIdList = this.imageService.findAllByPostId(req.getId());
        List<Long> images = new ArrayList<>();
        for (PostImageDTO.ResponseDTO id : imageIdList) {
            images.add(id.getFileId());
        }

        Set<User> postVoter = req.getVoter();
        Set<Long> postVoterId = new HashSet<>();
        if (!postVoter.isEmpty()) {
            for (User user : postVoter) {
                postVoterId.add(user.getId());
            }
        }

        Set<User> postScraper = req.getScraper();
        Set<Long> postScraperId = new HashSet<>();
        if (!postScraper.isEmpty()) {
            for (User user : postScraper) {
                postScraperId.add(user.getId());
            }
        }
        PostDTO.FreePostResponseDTO res = PostDTO.FreePostResponseDTO.builder()
                .id(req.getId())
                .title(req.getTitle())
                .content(req.getContent())
                .createdDate(req.getCreatedDate())
                .modifiedDate(req.getModifiedDate())
                .authorId(req.getAuthor().getId())
                .nickname(req.getAuthor().getNickname())
                .authorProfileImageId(profileImageId)
                .commentList(comments)
                .imageId(images)
                .postVoterId(postVoterId)
                .postScraperId(postScraperId)
                .build();

        return res;
    }

    public PostDTO.CoursePostResponseDTO createCourseBoardResponse(Post req) {
        List<CommentDTO.ResponseDTO> comments = new ArrayList<>();
        Long profileImageId;
        if (req.getAuthor().getProfileImage() != null) {
            profileImageId = req.getAuthor().getProfileImage().getId();
        } else {
            profileImageId = -1L;
        }
        if (req.getCommentList() != null) {
            for (int i = 0; i < req.getCommentList().size(); i++) {
                Comment comment = req.getCommentList().get(i);
                List<ReCommentDTO.ResponseDTO> reCommentList = new ArrayList<>();
                Set<Long> commentVoterId = new HashSet<>();

                if (comment.getVoter() != null) {
                    Set<User> commentVoter = comment.getVoter();
                    for (User user : commentVoter) {
                        commentVoterId.add(user.getId());
                    }
                }
                if (comment.getReCommentList() != null) {
                    for (int j = 0; j < comment.getReCommentList().size(); j++) {
                        ReComment reComment = comment.getReCommentList().get(j);
                        Long reCommentProfileImageId;
                        if (reComment.getAuthor().getProfileImage() != null) {
                            reCommentProfileImageId = reComment.getAuthor().getProfileImage().getId();
                        } else {
                            reCommentProfileImageId = -1L;
                        }
                        Set<Long> reCommentVoterId = new HashSet<>();
                        if (reComment.getVoter() != null) {
                            Set<User> reCommentVoter = reComment.getVoter();
                            for (User user : reCommentVoter) {
                                reCommentVoterId.add(user.getId());
                            }
                        }
                        ReCommentDTO.ResponseDTO reCommentRes = ReCommentDTO.ResponseDTO.builder()
                                .id(reComment.getId())
                                .content(reComment.getContent())
                                .createdDate(reComment.getCreatedDate())
                                .modifiedDate(reComment.getModifiedDate())
                                .userId(reComment.getAuthor().getId())
                                .userNickname(reComment.getAuthor().getNickname())
                                .userProfileImageId(reCommentProfileImageId)
                                .reCommentVoterId(reCommentVoterId)
                                .build();
                        reCommentList.add(reCommentRes);
                    }
                }
                Long commentProfileImageId = -1L;
                Long commentAuthorId = -1L;
                String commentAuthorNickname = "***";
                if (comment.getAuthor() != null) {
                    commentAuthorId = comment.getAuthor().getId();
                    commentAuthorNickname = comment.getAuthor().getNickname();
                    if (comment.getAuthor().getProfileImage() != null) {
                        commentProfileImageId = comment.getAuthor().getProfileImage().getId();
                    }
                }
                CommentDTO.ResponseDTO commentRes = CommentDTO.ResponseDTO.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .createdDate(comment.getCreatedDate())
                        .modifiedDate(comment.getModifiedDate())
                        .userId(commentAuthorId)
                        .userNickname(commentAuthorNickname)
                        .userProfileImageId(commentProfileImageId)
                        .reCommentList(reCommentList)
                        .commentVoterId((commentVoterId))
                        .build();
                comments.add(commentRes);
            }
        }

        List<PostImageDTO.ResponseDTO> imageIdList = this.imageService.findAllByPostId(req.getId());
        List<Long> images = new ArrayList<>();
        for (PostImageDTO.ResponseDTO id : imageIdList) {
            images.add(id.getFileId());
        }

        Set<User> postVoter = req.getVoter();
        Set<Long> postVoterId = new HashSet<>();
        if (!postVoter.isEmpty()) {
            for (User user : postVoter) {
                postVoterId.add(user.getId());
            }
        }

        Set<User> postScraper = req.getScraper();
        Set<Long> postScraperId = new HashSet<>();
        if (!postScraper.isEmpty()) {
            for (User user : postScraper) {
                postScraperId.add(user.getId());
            }
        }

        List<CourseImageInfoDTO.responseDTO> imagesInfo = new ArrayList<>();
        for (int idx = 1; idx < images.size(); idx++) {
            CourseImageInfo imageInfo = this.courseImageInfoRepository.findByPostImageId(images.get(idx))
                    .orElseThrow(() -> new RuntimeException("이미지 정보가 존재하지 않습니다."));
            CourseImageInfoDTO.responseDTO responseDTO = CourseImageInfoDTO.responseDTO.builder()
                    .coordinate(imageInfo.getCoordinate())
                    .placeLink(imageInfo.getPlaceLink())
                    .placeName(imageInfo.getPlaceName()).build();
            imagesInfo.add(responseDTO);
        }
        PostDTO.CoursePostResponseDTO res = PostDTO.CoursePostResponseDTO.builder()
                .id(req.getId())
                .courseId(this.userCourseRepository.findByPostId(req.getId()).getId())
                .title(req.getTitle())
                .content(req.getContent())
                .createdDate(req.getCreatedDate())
                .modifiedDate(req.getModifiedDate())
                .authorId(req.getAuthor().getId())
                .nickname(req.getAuthor().getNickname())
                .authorProfileImageId(profileImageId)
                .commentList(comments)
                .imageId(images)
                .postVoterId(postVoterId)
                .postScraperId(postScraperId)
                .imageInfoList(imagesInfo).build();
        return res;
    }

    public Pageable sortBy(int page, String sortBy) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc(sortBy));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return pageable;
    }

    @Transactional
    public PostDTO.FreePostResponseDTO coursePost(PostDTO.CreateRequestDTO req, List<MultipartFile> files) throws Exception {
        Post newPost = Post.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .createdDate(LocalDateTime.now())
                .author(this.userRepository.findById(req.getUserId()).get())
                .build();
        List<PostImage> postImageList = imageHandler.parsePostImageInfo(files);

        if (!postImageList.isEmpty()) {
            for (PostImage postImage : postImageList) {
                newPost.addImage(postImageRepository.save(postImage));
            }
        }
        return createFreeBoardResponse(this.postRepository.save(newPost));
    }
}
