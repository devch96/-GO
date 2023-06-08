package com.hansung.capstone.community;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansung.capstone.user.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@Slf4j
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PostService postService;



    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 한글 깨짐 처리
                .build();
    }

    @Test
    @Order(100)
    @DisplayName("Post create post test - /api/community/post/create")
    void createPostTest() throws Exception {

        UserDTO.SignUpRequestDTO req = UserDTO.SignUpRequestDTO.builder()
                .email("hoon@test.com")
                .password("1234")
                .nickname("훈")
                .username("훈")
                .birthday("12345678").build();

        // 회원가입
        mockMvc.perform(post("/api/users/signup")
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(100))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nickname").value("훈"));

        PostDTO.CreateRequestDTO req1 = PostDTO.CreateRequestDTO.builder()
                .userId(1L)
                .title("test-title")
                .content("테스트 콘텐트 입니다").build();

        String cnt = objectMapper.writeValueAsString(req1);
        MockMultipartFile json = new MockMultipartFile("requestDTO", "", "application/json", cnt.getBytes());

        mockMvc.perform(multipart("/api/community/post/create")
                        .file(json)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(100))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("test-title"))
                .andExpect(jsonPath("$.data.content").value("테스트 콘텐트 입니다"));

    }

    @Test
    @Order(200)
    @DisplayName("Put modify post test - /api/community/post/modify")
    void modifyPostTest() throws Exception {
        PostDTO.ModifyRequestDTO req = PostDTO.ModifyRequestDTO.builder()
                .postId(1L)
                .title("제목 변경 테스트")
                .content("내용 변경 테스트").build();

        String cnt = objectMapper.writeValueAsString(req);

        mockMvc.perform(put("/api/community/post/modify")
                .content(cnt)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(100))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("제목 변경 테스트"))
                .andExpect(jsonPath("$.data.content").value("내용 변경 테스트"));

    }

    @Test
    @Order(300)
    @DisplayName("Get all post test - /api/community/post/list")
    void getAllPostTest() throws Exception {
        for (int i = 1; i <= 300; i++ ) {
            PostDTO.CreateRequestDTO req = PostDTO.CreateRequestDTO.builder()
                    .userId(1L)
                    .title("test-title - [%03d]".formatted(i))
                    .content("테스트 콘텐트 입니다").build();
            this.postService.createFreeBoardPost(req, null);
        }
        mockMvc.perform(get("/api/community/post/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].id").value(301));

    }

    @Test
    @Order(400)
    @DisplayName("Get detail post test - /api/community/post/detail")
    void getDetailPostTest() throws Exception {
        mockMvc.perform(get("/api/community/post/detail")
                        .param("id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(100))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("제목 변경 테스트"))
                .andExpect(jsonPath("$.data.content").value("내용 변경 테스트"));


    }
}