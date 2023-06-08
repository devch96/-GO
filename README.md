# 자전GO

<img src="https://github.com/IB103/client/assets/92325898/39bde59c-2009-42e4-8b85-d9eea468ba82" width="70%"> 

1. 개요

    자전GO 모바일 어플리케이션의 메인 서버이다.
    통신 대상은 클라이언트이고, 모든 기능은 Rest API를 기반으로 한다.
    데이터 형식은 JSON, FormData를 사용한다.
    SpringSecurity를 통해 JSON Web Token(JWT)를 검사하고 인가절차를 밟는다.

2. 개발 환경

    - AWS Lightsail
    - Intellij IDEA
    - Spring Boot
    - Java 17
    - MySQL 8.0.32
    - Redis 7.0.10

    build.gradle
    
    ```java
    dependencies {
        implementation('org.springframework.boot:spring-boot-starter-web')
        testImplementation('org.projectlombok:lombok')
        compileOnly('org.projectlombok:lombok')
        annotationProcessor('org.projectlombok:lombok')
        testImplementation('org.springframework.boot:spring-boot-starter-test')
        implementation('org.springframework.boot:spring-boot-starter-data-jpa')
        testImplementation('org.junit.jupiter:junit-jupiter-api:5.9.2')
        testRuntimeOnly('org.junit.jupiter:junit-jupiter-engine:5.9.2')
        compileOnly('org.springframework.boot:spring-boot-devtools')
        implementation('org.springframework.boot:spring-boot-starter-security')
        testImplementation('org.springframework.security:spring-security-test')
        implementation('org.springframework.boot:spring-boot-starter-mail')
        implementation('org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.0')
        implementation('io.jsonwebtoken:jjwt-api:0.11.5')
        implementation('io.jsonwebtoken:jjwt-impl:0.11.5')
        implementation('io.jsonwebtoken:jjwt-jackson:0.11.5')
        implementation('commons-io:commons-io:2.6')
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("com.google.maps:google-maps-services:2.1.2")
        implementation(group: 'mysql', name: 'mysql-connector-java', version: '8.0.32')
        implementation('org.springframework.boot:spring-boot-starter-data-redis')
    }
    ```
    

2. 기능

    - Rest API 한눈에 보기

        <img src="https://github.com/IB103/server/assets/75194525/be808317-ec28-451f-b2c3-2bdb31c35f36" width="1000"> 

        
        <img src="https://github.com/IB103/server/assets/75194525/3deeb161-c80f-49b1-9912-3e9168c497ae" width="1000"> 
        
    
    - Respone관련 기능

        서버에서 나가는 모든 Response 데이터는 서버에서 코드를 만들고 특정 코드에 대해 오류값을 전달한다.

        ResponseService.java

        ```java
        public CommonResponse getSuccessCommonResponse(){
            CommonResponse commonResponse = new CommonResponse();
            setSuccessResponse(commonResponse);
            return commonResponse;
        }
        public CommonResponse getFailureCommonResponse(){
            CommonResponse commonResponse = new CommonResponse();
            setFailResponse(commonResponse);
            return commonResponse;
        }
        public<T> SingleResponse<T> getSuccessSingleResponse(T data){
            SingleResponse singleResponse = new SingleResponse();
            singleResponse.data = data;
            setSuccessResponse(singleResponse);
            return singleResponse;
        }
        public<T> SingleResponse<T> getFailureSingleResponse(T data){
            SingleResponse singleResponse = new SingleResponse();
            singleResponse.data = data;
            setFailResponse(singleResponse);
            return singleResponse;
        }
        public<T> ListResponse<T> getListResponse(List<T> data){
            ListResponse listResponse = new ListResponse();
            listResponse.data = data;
            setSuccessResponse(listResponse);
            return listResponse;
        }
        public<T> PageResponse<T> getPageResponse(int totalPage, List<T> data){
            PageResponse pageResponse = new PageResponse();
            pageResponse.totalPage = totalPage;
            pageResponse.data = data;
            setSuccessResponse(pageResponse);
            return pageResponse;
        }
        void setSuccessResponse(CommonResponse res){
            res.code = 100;
            res.success = true;
            res.message = "SUCCESS";
        }
        void setFailResponse(CommonResponse res){
            res.code = 900;
            res.success = false;
            res.message = "FAILURE";
        }
        ```
        
    
    - 유저 관련 기능
        1. 로그인

            이메일, 비밀번호를 받아서 DB와 체크한다.
            
            AuthService.java
            
            ```java
            @Transactional
            public UserDTO.SignInResponseDTO login(UserDTO.SignInRequestDTO req) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword());                
                Authentication authentication = authenticationManagerBuilder.getObject()
                        .authenticate(authenticationToken);
            }
            ```

            로그인에 성공하면 AccessToken 과 Refresh Token을 전달한다.
                
            JwtTokenProvider.java

            ```java
            @Transactional
            public TokenInfo createToken(String email, String authorities){
                Long now = System.currentTimeMillis();

                String accessToken = Jwts.builder()
                        .setHeaderParam("typ", "JWT")
                        .setHeaderParam("alg", "HS512")
                        .setExpiration(new Date(now + accessTokenValidityInMilliseconds))
                        .setSubject("access-token")
                        .claim(url, true)
                        .claim(EMAIL_KEY, email)
                        .claim(AUTHORITIES_KEY, authorities)
                        .signWith(signingKey, SignatureAlgorithm.HS512)
                        .compact();

                String refreshToken = Jwts.builder()
                        .setHeaderParam("typ", "JWT")
                        .setHeaderParam("alg", "HS512")
                        .setExpiration(new Date(now + refreshTokenValidityInMilliseconds))
                        .setSubject("refresh-token")
                        .signWith(signingKey, SignatureAlgorithm.HS512)
                        .compact();

                return new TokenInfo(accessToken, refreshToken);
            }
            ```

            Refresh Token은 Access Token 재발급에 사용되므로 RedisDB에 따로 저장하여 관리한다.
            
            AuthService.java
        
            ```java
            @Transactional
            public void saveRefreshToken(String email, String refreshToken) {
                redisService.setValuesWithTimeout("RT:" + email, // key
                        refreshToken, // value
                        jwtTokenProvider.getTokenExpirationTime(refreshToken)); // timeout(milliseconds)
            }
            ```
            
        2. 회원가입
        
            이메일, 비밀번호, 닉네임, 유저이름, 생일을 받아서 저장한다.
            비밀번호는 encode해서 db에 저장한다.
            이메일과 닉네임은 중복되선 안되기에 사전에 중복확인을 한 후에 회원가입을 진행한다.
            
            UserServiceImpl.java
        
            ```java
            if (!(this.userRepository.findByEmail(req.getEmail()).isPresent() || this.userRepository.findByNickname(req.getNickname()).isPresent())) {
                        User newuser = User.builder()
                                .email(req.getEmail())
                                .password(passwordEncoder.encode(req.getPassword()))
                                .nickname(req.getNickname())
                                .username(req.getUsername())
                                .birthday(req.getBirthday())
                                .build();
                        this.userRepository.save(newuser);
            }
            ```
            
        3. 이메일 & 닉네임 중복확인
        
            클라이언트가 회원가입, 닉네임 변경에서 사용하는 기능
            
            UserServiceImpl.java
    
            ```java
            @Override
            public Boolean EmailDupCheck(String email) {
                Optional<User> user = this.userRepository.findByEmail(email);
                if (user.isPresent()) {
                    return false;
                } else {
                    return true;
                }
            }
        
            @Override
            public Boolean NicknameDupCheck(String nickname) {
                Optional<User> user = this.userRepository.findByNickname(nickname);
                if (user.isPresent()) {
                    return false;
                } else {
                    return true;
                }
            }
            ```
            
        4. 닉네임 변경
            
            userId 와 변경할 닉네임을 받아서 진행. 중복확인을 거쳐서 실행됨.

            UserServiceImpl.java

            ```java
            @Transactional
            @Override
            public Optional<User> modifyNickname(UserDTO.ModifyNickRequestDTO req) {
                Optional<User> user = this.userRepository.findByEmail(req.getEmail());
                user.ifPresent(s -> {
                    user.get().modifyNick(req.getNickname());
                });
                Optional<User> modifiedUser = this.userRepository.findByEmail(req.getEmail());
                return modifiedUser;
            }
            ```
            
        5. 아이디 찾기
        
            생년월일과 이름을 받아서 아이디 리스트를 전달한다.
            
            UserServiceImpl.java

            ```java
            @Override
            public List<String> findEmail(String username, String birthday) {
                List<UserEmailInterface> appuser = this.userRepository.findByUsernameAndBirthday(username, birthday);
                List<String> res = new ArrayList<>();
                if (appuser.isEmpty()) {
                    throw new DataNotFoundException("AppUser Not Found");
                } else {
                    for (UserEmailInterface s : appuser) {
                        String email = s.getEmail();
                        int atIndex = email.indexOf("@");
                        res.add(email.substring(0, 2) + "*".repeat(atIndex - 2) + email.substring(atIndex));
                    }
                    return res;
                }
            }
            ```
            
        6. 비밀번호 찾기(변경)
        
            BcryptPasswordEncoder 를 통해 DB에 비밀번호 평문이 아닌 암호문을 저장하기 때문에 비밀번호 찾기는 사실상 불가능하기에 비밀번호를 찾으려면 이메일 인증 후 변경하는 방식으로 구현했다.
            
            UserServiceImpl.java

            ```java
            @Transactional
            @Override
            public Optional<User> modifyPassword(UserDTO.ModifyPWRequestDTO req) {
                Optional<User> user = this.userRepository.findByEmail(req.getEmail());
                user.ifPresent(s -> {
                    user.get().modifyPW(passwordEncoder.encode(req.getPassword()));
                });
                Optional<User> modifiedUser = this.userRepository.findByEmail(req.getEmail());
                return modifiedUser;
            }
            ```
            
        7. 이메일 인증(전송)
        
            이메일을 전달받고 JavaMailSender를 통해 인증코드를 이메일로 전송
            인증코드를 RedisDB에 5분의 시간제한을 두고 저장.
            5분 후에는 인증이 불가.
            
            EmailServiceImpl.java

            ```java
            @Override
            public String sendSimpleMessage(String to, String code) throws Exception {
                MimeMessage message = createMessage(to,code);
                this.redisService.setValuesWithTimeout("Email-Confirm:" + to, code, 300000); // 5분
                try{
                    javaMailSender.send(message);
                }catch (MailException e){                        e.printStackTrace();
                        throw new IllegalArgumentException();
                }
                return code;
            }
            ```
            
        8. 이메일 인증(확인)
        
            클라이언트에서 전송받은 코드와 DB에 저장되어있는 코드 확인 후 일치하면 임시토큰을 발급하여 비밀번호 변경이 가능하도록 함.
            
            EmailServiceImpl.java

            ```java
            @Override
            public Boolean checkCode(String email, String code) throws Exception {
                if (this.redisService.getValues("Email-Confirm:" + email).equals(code)){
                        this.redisService.deleteValues("Email-Confirm:" + email);
                        return true;
                } else{
                        return false;
                }
            }
            ```
            
        9.  프로필 이미지 설정
        
            @RequestPart를 통해 FormData로 데이터를 받아서 이미지 설정
            
            UserController.java

            ```java
            @RequestPart(value = "requestDTO") UserDTO.ProfileImageRequestDTO req,
            @RequestPart(value = "imageList", required = false) MultipartFile imageList)
            ```
            
        10. 로그아웃
        
            Redis에 저장되어있는 RefreshToken 삭제
            
            AuthService.java

            ```java
            String refreshTokenInRedis = redisService.getValues("RT:" + email);
                    if (refreshTokenInRedis != null) {
                        redisService.deleteValues("RT:" + email);
                    }
            ```
            
        
    - 커뮤니티 관련 기능
        1. 게시글 작성
        
            @RequestPart 로 FormData를 전달받아 작성
        
        2. 게시글 수정
        
            새로운 title, content 그리고 imageId를 통해 기존에 있던 이미지 수정과 새로운 이미지 등록이 가능함.
        
            PostServiceImpl.java

            ```java
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
            List<PostImage> postImageList = imageHandler.parsePostImageInfo(files);
            if (!postImageList.isEmpty()) {
                for (PostImage postImage : postImageList) {
                    modifyPost.addImage(postImageRepository.save(postImage));
                }
            }
            modifyPost.modify(req.getTitle(), req.getContent(), LocalDateTime.now());
            return createFreeBoardResponse(this.postRepository.findById(req.getPostId()).get());
            ```
        
        3. 게시판 조회
        
            자유게시판과 코스게시판, 전체글, 유저가 스크랩한 글을 조회할 수 있음.
            Post Entity에 유저가 들어가있어 그냥 전달할 경우 유저의 민감한 정보가 전달 될 수 있기 때문에 전처리를 하고 클라이언트에 전달함.
            페이징 처리를 통해 등록날짜순 정렬함.
            
            페이징
        
            PostServiceImpl.java

            ```java
            public Pageable sortBy(int page, String sortBy) {
                    List<Sort.Order> sorts = new ArrayList<>();
                    sorts.add(Sort.Order.desc(sortBy));
                    Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
                    return pageable;
                }
            ```
            
            글 조회
            
            PostRepository.java

            ```java
            Page<Post> findAll(Pageable pageable);
            
            Page<Post> findAllByPostCategory(Pageable pageable, PostCategory postCategory);
            
            @Query(
                        value = "SELECT * FROM post WHERE post_id IN (SELECT post_post_id FROM post_scraper WHERE scraper_user_id = :userId)",
                        nativeQuery = true
                )
            Page<Post> findAllScrap(@Param("userId")Long userId, Pageable pageable);
            ```
            
        4. 게시판 검색
        
            제목or내용 검색, 작성자 검색으로 구현
            
            PostRepository.java

            ```java
            Page<Post> findAllByAuthor(User user, Pageable pageable);
        
            @Query(
                    value = "SELECT p FROM Post p Where p.title LIKE %:titleOrContent% OR p.content LIKE %:titleOrContent%"
            )
            Page<Post> findAllSearch(@Param("titleOrContent")String titleOrContent, Pageable pageable);
            ```
                
        5. 스크랩과 좋아요 기능
        
            양방향 연결이 아닌 단방향 연결로 구현
            
            Post.java

            ```java
            @ManyToMany
            @OnDelete(action = OnDeleteAction.CASCADE)
            private Set<User> voter = new HashSet<>();
            
            @ManyToMany
            @OnDelete(action = OnDeleteAction.CASCADE)
            private Set<User> scraper = new HashSet<>();
            ```
            
            PostServiceImpl.java

            ```java
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
            ```
            
        6. 삭제 기능
            
            userId와 AccessToken을 받아 처리.
            
            AuthService.java

            ```java
            public boolean checkIdAndToken(Long userId){
                    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    if(userId.equals(userDetails.getUserId())){
                        return true;
                    } else {
                        return false;
                    }
                }
            ```
            
        7. 댓글과 대댓글
            
            게시글과 같은 방식으로 구현.
            다른 점은 댓글에 대댓글이 존재할때 삭제요청이 오면 삭제를 하는 것이 아닌 “삭제된 댓글입니다” 처리를 구현함. 삭제된 댓글에 달려있는 대댓글이 없을경우 DB에서 완전 삭제
            
            CommentServiceImpl.java

            ```java
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
            ```
            
    
    - 코스 관련 기능
        1. 일정 좋아요 갯수 이상으로 지역별 검색

            페이징처리를 위해 countQuery 사용
            
            UserCourseRepository.java

            ```java
            @Query(
                    value = "SELECT uc.*\n" +
                            "FROM user_course uc\n" +
                            "JOIN (\n" +
                            "    SELECT pv.post_post_id, COUNT(*) AS vote_count\n" +
                            "    FROM post_voter pv\n" +
                            "    GROUP BY pv.post_post_id\n" +
                            "    HAVING COUNT(*) >= 1\n" +
                            ") AS subquery\n" +
                            "ON uc.post_id = subquery.post_post_id\n" +
                            "WHERE uc.region = :region\n" +
                            "ORDER BY subquery.vote_count DESC",
                    countQuery = "SELECT COUNT(*)\n" +
                            "FROM user_course uc\n" +
                            "JOIN (\n" +
                            "    SELECT pv.post_post_id, COUNT(*) AS vote_count\n" +
                            "    FROM post_voter pv\n" +
                            "    GROUP BY pv.post_post_id\n" +
                            "    HAVING COUNT(*) >= 1\n" +
                            ") AS subquery\n" +
                            "ON uc.post_id = subquery.post_post_id\n" +
                            "WHERE uc.region = :region",
                    nativeQuery = true
            )
            Page<UserCourse> findAllByRegion(Pageable pageable, @Param("region") String region);
            ```
            
        
    - 유저 라이딩 기록 관련 기능
        1. 특정 기간동안의 기록 전달
        
            같은 날에 기록이 저장될경우 합쳐서 전달
            
            UserRidingRepository.java
        
            ```java
            @Query(
                    value = "SELECT DATE(created_date) AS date, SUM(calorie) AS total_calorie, SUM(riding_distance) AS total_distance, SUM(riding_time) AS total_time\n" +
                            "FROM user_riding\n" +
                            "WHERE user_id = :userId and created_date <= :now and created_date >= :period\n"+
                            "GROUP BY user_id, DATE(created_date)",
                    nativeQuery = true
            )
            List<Object[]> findAllByPeriod(@Param("userId") Long userId, @Param("now") LocalDateTime now, @Param("period") LocalDateTime period);
            ```
            
        2. 유저 랭킹
        
            @Scheduled를 사용하여 특정 시간대에 자동으로 랭킹 갱신하고 Redis에 저장
            
            UserServiceImpl.java

            ```java
            @Scheduled(cron = "0 10 08 * * *")
            private void rank(){
                List<Object[]> ranker = this.userRidingRepository.getRank();
                for(Object[] row : ranker){
                    String key = "Rank_No"+String.valueOf(row[2]);
                    String value = row[0].toString() + ":" + row[1].toString();
                    this.redisService.setValues(key, value);
                }
            }
            ```
            
            UserRidingRepository.java

            ```java
            @Query(
                    value = "SELECT user_id, ROUND(SUM(riding_distance), 2) AS total_distance, RANK() OVER (ORDER BY SUM(riding_distance) DESC) AS distance_rank\n" +
                            "FROM user_riding\n" +
                            "GROUP BY user_id\n" +
                            "LIMIT 3",
                    nativeQuery = true
            )
            List<Object[]> getRank();
            ```
            
4. SecurityConfig

    
    ```java
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
    .authorizeHttpRequests(
    authorize ->
            authorize
                    .requestMatchers("/api/users/signup").permitAll()
                    .requestMatchers("/api/users/signin").permitAll()
                    .requestMatchers("/api/users/nickname/duplicate-check").permitAll()
                    .requestMatchers("/api/users/email/duplicate-check").permitAll()
                    .requestMatchers("/api/users/findID").permitAll()
                    .requestMatchers("/api/community/post/list/**").permitAll()
                    .requestMatchers("/api/community/post/list/scrap").authenticated()
                    .requestMatchers("/api/community/post/detail").permitAll()
                    .requestMatchers("/api/users/riding/rank").permitAll()
                    .requestMatchers("/api/user-course/list").permitAll()
                    .requestMatchers("/api/user-course/detail").permitAll()
                    .requestMatchers("/api/email/send").permitAll()
                    .requestMatchers("/api/email/confirm").permitAll()
                    .requestMatchers("/profile-image/**").permitAll()
                    .requestMatchers("/image/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/v3/api-docs/**").permitAll()
                    .requestMatchers("/api/auth/reissue").permitAll()
                    .anyRequest().authenticated()
    )
    }
    ```


5. 테이블 구조

    
    <img src="https://github.com/IB103/server/assets/75194525/b2d0b596-c025-4088-898d-297703bfa5b8" width="1000"> 
