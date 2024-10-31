package nextstep.app;

import nextstep.app.domain.Member;
import nextstep.app.infrastructure.InmemoryMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginTest {
    private static final Member TEST_MEMBER = InmemoryMemberRepository.TEST_MEMBER_1;

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("로그인 성공")
    @Test
    void login_success() throws Exception {
        ResultActions loginResponse = mockMvc.perform(post("/login")
                .param("username", TEST_MEMBER.getEmail())
                .param("password", TEST_MEMBER.getPassword())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        );

        loginResponse.andExpect(status().isOk());

        HttpSession session = loginResponse.andReturn().getRequest().getSession();
        assertThat(session).isNotNull();
        assertThat(session.getAttribute("SPRING_SECURITY_CONTEXT")).isNotNull();
    }

    @DisplayName("로그인 실패 - 사용자 없음")
    @Test
    void login_fail_with_no_user() throws Exception {
        ResultActions response = mockMvc.perform(post("/login")
                .param("username", "none")
                .param("password", "none")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        );

        response.andExpect(status().isUnauthorized());
    }

    @DisplayName("로그인 실패 - 비밀번호 불일치")
    @Test
    void login_fail_with_invalid_password() throws Exception {
        ResultActions response = mockMvc.perform(post("/login")
                .param("username", TEST_MEMBER.getEmail())
                .param("password", "invalid")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        );

        response.andExpect(status().isUnauthorized());
    }

    @DisplayName("로그인 후 세션을 통해 회원 목록 조회")
    @Test
    void login_after_members() throws Exception {
        ResultActions loginResponse = mockMvc.perform(post("/login")
                .param("username", TEST_MEMBER.getEmail())
                .param("password", TEST_MEMBER.getPassword())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        ).andDo(print());

        loginResponse.andExpect(status().isOk());

        MvcResult loginResult = loginResponse.andReturn();
        HttpSession session = loginResult.getRequest().getSession();
        String sessionId = session.getId();

        ResultActions membersResponse = mockMvc.perform(get("/members")
                .cookie(new Cookie("JSESSIONID", sessionId))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        );

        membersResponse.andExpect(status().isOk());
    }
}
