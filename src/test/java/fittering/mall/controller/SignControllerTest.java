package fittering.mall.controller;

import fittering.mall.config.ControllerTestSupport;
import fittering.mall.config.WithCustomMockUser;
import fittering.mall.controller.dto.request.RequestLoginDto;
import fittering.mall.controller.dto.request.RequestSignUpDto;
import fittering.mall.domain.entity.User;
import fittering.mall.service.dto.LoginDto;
import fittering.mall.service.dto.SignUpDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SignControllerTest extends ControllerTestSupport {

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("올바른 정보 입력 시 로그인에 성공한다.")
    @Test
    @WithCustomMockUser
    void loginWithValidEmailAndPassword() throws Exception {
        //given
        RequestLoginDto controllerRequest = RequestLoginDto.builder()
                .email("test@email.com")
                .password("password")
                .build();
        User user = User.builder()
                .username("test")
                .password("password")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .ageRange(1)
                .build();

        when(userService.login(any(LoginDto.class))).thenReturn(user);
        when(jwtTokenProvider.createToken(any(String.class), any(List.class))).thenReturn("token");

        //when //then
        mockMvc.perform(
                        post("/api/v1/login")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(controllerRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("token"));
    }

    @DisplayName("올바르지 않은 이메일 입력 시 로그인에 실패한다.")
    @Test
    @WithCustomMockUser
    void loginWithInvalidEmail() throws Exception {
        //given
        RequestLoginDto controllerRequest = RequestLoginDto.builder()
                .email("test")
                .password("password")
                .build();
        User user = User.builder()
                .username("test")
                .password("password")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .ageRange(1)
                .build();

        when(userService.login(any(LoginDto.class))).thenReturn(user);
        when(jwtTokenProvider.createToken(any(String.class), any(List.class))).thenReturn("token");

        //when //then
        mockMvc.perform(
                        post("/api/v1/login")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(controllerRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이메일 형식이 아닙니다." + "\n"));
    }

    @DisplayName("올바르지 않은 비밀번호 입력 시 로그인에 실패한다.")
    @Test
    @WithCustomMockUser
    void loginWithInvalidPassword() throws Exception {
        //given
        RequestLoginDto controllerRequest = RequestLoginDto.builder()
                .email("test@email.com")
                .build();
        User user = User.builder()
                .username("test")
                .password("password")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .ageRange(1)
                .build();

        when(userService.login(any(LoginDto.class))).thenReturn(user);
        when(jwtTokenProvider.createToken(any(String.class), any(List.class))).thenReturn("token");

        //when //then
        mockMvc.perform(
                        post("/api/v1/login")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(controllerRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("비밀번호는 필수입니다." + "\n"));
    }

    @DisplayName("일치하는 회원 정보가 없으면 로그인에 실패한다.")
    @Test
    @WithCustomMockUser
    void loginWithInvalidUserInfo() throws Exception {
        //given
        RequestLoginDto controllerRequest = RequestLoginDto.builder()
                .email("test@email.com")
                .password("password")
                .build();

        when(userService.login(any(LoginDto.class))).thenReturn(null);

        //when //then
        mockMvc.perform(
                        post("/api/v1/login")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(controllerRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("일치하는 유저 정보가 없습니다."));
    }

    @DisplayName("같은 이름과 이메일이 존재하지 않아 회원가입에 성공한다.")
    @Test
    @WithCustomMockUser
    void signUpSuccess() throws Exception {
        //given
        RequestSignUpDto request = RequestSignUpDto.builder()
                .username("test")
                .password("password")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .build();

        when(userService.save(any(SignUpDto.class))).thenReturn(null);

        //when //then
        mockMvc.perform(
                        post("/api/v1/signup")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.*").exists());
    }

    @DisplayName("같은 이름이 존재해 회원가입에 실패한다.")
    @Test
    @WithCustomMockUser
    void signUpWithDuplicatedName() throws Exception {
        //given
        RequestSignUpDto request = RequestSignUpDto.builder()
                .username("test")
                .password("password")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .build();

        when(userService.usernameExist(any(String.class))).thenReturn(true);

        //when //then
        mockMvc.perform(
                        post("/api/v1/signup")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("같은 이름이 존재합니다."));
    }

    @DisplayName("같은 이메일이 존재해 회원가입에 실패한다.")
    @Test
    @WithCustomMockUser
    void signUpWithDuplicatedEmail() throws Exception {
        //given
        RequestSignUpDto request = RequestSignUpDto.builder()
                .username("test")
                .password("password")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .build();

        when(userService.emailExist(any(String.class))).thenReturn(true);

        //when //then
        mockMvc.perform(
                        post("/api/v1/signup")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("같은 이메일이 존재합니다."));
    }

    @DisplayName("회원가입 시 닉네임은 필수값이다.")
    @Test
    @WithCustomMockUser
    void signUpWithoutUsername() throws Exception {
        //given
        RequestSignUpDto request = RequestSignUpDto.builder()
                .password("password")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/signup")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("닉네임을 입력해주세요." + "\n"));
    }

    @DisplayName("회원가입 시 닉네임은 필수값이다.")
    @Test
    @WithCustomMockUser
    void signUpWithOverUsername() throws Exception {
        //given
        RequestSignUpDto request = RequestSignUpDto.builder()
                .username("123456789012345678901")
                .password("password")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/signup")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("닉네임은 20자 이내로 입력해주세요." + "\n"));
    }

    @DisplayName("회원가입 시 비밀번호는 필수값이다.")
    @Test
    @WithCustomMockUser
    void signUpWithoutPassword() throws Exception {
        //given
        RequestSignUpDto request = RequestSignUpDto.builder()
                .username("test")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/signup")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("비밀번호를 입력해주세요." + "\n"));
    }

    @DisplayName("올바르지 않은 이메일 입력 시 회원가입에 실패한다.")
    @Test
    @WithCustomMockUser
    void signUpWithInvalidEmail() throws Exception {
        //given
        RequestSignUpDto request = RequestSignUpDto.builder()
                .username("test")
                .password("password")
                .email("test")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/signup")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이메일 형식에 맞지 않습니다." + "\n"));
    }

    @DisplayName("회원가입 시 이메일은 필수값이다.")
    @Test
    @WithCustomMockUser
    void signUpWithoutEmail() throws Exception {
        //given
        RequestSignUpDto request = RequestSignUpDto.builder()
                .username("test")
                .password("password")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/signup")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이메일을 입력해주세요." + "\n"));
    }

    @DisplayName("회원가입 시 성별은 필수값이다.")
    @Test
    @WithCustomMockUser
    void signUpWithoutGender() throws Exception {
        //given
        RequestSignUpDto request = RequestSignUpDto.builder()
                .username("test")
                .password("password")
                .email("test@email.com")
                .year(1990)
                .month(1)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/signup")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("성별을 입력해주세요." + "\n"));
    }

    @DisplayName("입력한 성별값이 1자를 초과하면 회원가입에 실패한다.")
    @Test
    @WithCustomMockUser
    void signUpWithOverGender() throws Exception {
        //given
        RequestSignUpDto request = RequestSignUpDto.builder()
                .username("test")
                .password("password")
                .email("test@email.com")
                .gender("MM")
                .year(1990)
                .month(1)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/signup")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("성별은 1자 이내로 입력해주세요." + "\n"));
    }

    @DisplayName("회원가입 시 태어난 년도는 필수값이다.")
    @Test
    @WithCustomMockUser
    void signUpWithoutYear() throws Exception {
        //given
        RequestSignUpDto request = RequestSignUpDto.builder()
                .username("test")
                .password("password")
                .email("test@email.com")
                .gender("M")
                .month(1)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/signup")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("태어난 년도를 입력해주세요." + "\n"));
    }

    @DisplayName("회원가입 시 태어난 월은 필수값이다.")
    @Test
    @WithCustomMockUser
    void signUpWithoutMonth() throws Exception {
        //given
        RequestSignUpDto request = RequestSignUpDto.builder()
                .username("test")
                .password("password")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/signup")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("태어난 월을 입력해주세요." + "\n"));
    }

    @DisplayName("회원가입 시 태어난 일자는 필수값이다.")
    @Test
    @WithCustomMockUser
    void signUpWithoutDay() throws Exception {
        //given
        RequestSignUpDto request = RequestSignUpDto.builder()
                .username("test")
                .password("password")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .month(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/signup")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("태어난 일자를 입력해주세요." + "\n"));
    }
}