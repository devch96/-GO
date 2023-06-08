package com.hansung.capstone.community;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 한글 깨짐 처리
                .build();
    }

    @Test
    @DisplayName("Post create comment test - /api/community/comment/create")
    @Order(100)
    void createCommentTest() throws Exception{
        // POST 생성
        PostDTO.CreateRequestDTO req = PostDTO.CreateRequestDTO.builder()
                .title("test-title")
                .content("테스트 콘텐트 입니다").build();

        String cnt = objectMapper.writeValueAsString(req);
        mockMvc.perform(post("/api/community/post/create")
                        .content(cnt)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(100))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("test-title"))
                .andExpect(jsonPath("$.data.content").value("테스트 콘텐트 입니다"));

        CommentDTO.CreateRequestDTO comment = CommentDTO.CreateRequestDTO.builder()
                .postId(1L)
                .content("1번 댓글")
                .build();

        mockMvc.perform(post("/api/community/comment/create")
                .content(objectMapper.writeValueAsBytes(comment))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated());


    }

    @Test
    @Order(200)
    @DisplayName("Post modify comment test - /api/community/comment/modify")
    void modifyCommentTest() throws Exception {
        CommentDTO.ModifyRequestDTO req = CommentDTO.ModifyRequestDTO.builder()
                .commentId(1L)
                .userId(1L)
                .content("수정테스트").build();

        mockMvc.perform(put("/api/community/comment/modify")
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.commentList[0].content").value("수정테스트"));
    }

}
