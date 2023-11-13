package fittering.mall.controller;

import fittering.mall.config.ControllerTestSupport;
import fittering.mall.config.WithCustomMockUser;
import fittering.mall.controller.dto.request.RequestMeasurementDto;
import fittering.mall.controller.dto.request.RequestSmartMeasurementDto;
import fittering.mall.controller.dto.request.RequestUserCheckDto;
import fittering.mall.controller.dto.request.RequestUserDto;
import fittering.mall.controller.dto.response.*;
import fittering.mall.domain.collection.Products;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest extends ControllerTestSupport {

    @Mock
    private HashOperations hashOperations;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("마이페이지를 조회한다.")
    @Test
    @WithCustomMockUser
    void getMyPage() throws Exception {
        //given
        ResponseUserDto response = ResponseUserDto.builder()
                .email("test@email.com")
                .username("test")
                .build();

        when(userService.info(1L)).thenReturn(response);

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/users/mypage")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("마이페이지를 수정한다.")
    @Test
    @WithCustomMockUser
    void updateMyPage() throws Exception {
        //given
        RequestUserDto request = RequestUserDto.builder()
                .email("test@email.com")
                .username("test")
                .gender("M")
                .year(1999)
                .month(1)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/mypage")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("마이페이지 수정 시 이메일은 필수값이다.")
    @Test
    @WithCustomMockUser
    void updateMyPageWithoutEmail() throws Exception {
        //given
        RequestUserDto request = RequestUserDto.builder()
                .username("test")
                .gender("M")
                .year(1999)
                .month(1)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/mypage")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이메일을 입력해주세요." + "\n"));
    }

    @DisplayName("올바르지 않은 이메일 입력 시 마이페이지 수정에 실패한다.")
    @Test
    @WithCustomMockUser
    void updateMyPageWithInvalidEmail() throws Exception {
        //given
        RequestUserDto request = RequestUserDto.builder()
                .email("test")
                .username("test")
                .gender("M")
                .year(1999)
                .month(1)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/mypage")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이메일 형식에 맞지 않습니다." + "\n"));
    }

    @DisplayName("마이페이지 수정 시 닉네임은 필수값이다.")
    @Test
    @WithCustomMockUser
    void updateMyPageWithoutUsername() throws Exception {
        //given
        RequestUserDto request = RequestUserDto.builder()
                .email("test@email.com")
                .gender("M")
                .year(1999)
                .month(1)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/mypage")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("닉네임을 입력해주세요." + "\n"));
    }

    @DisplayName("닉네임 20자 초과 시 마이페이지 수정에 실패한다.")
    @Test
    @WithCustomMockUser
    void updateMyPageWithOverUsername() throws Exception {
        //given
        RequestUserDto request = RequestUserDto.builder()
                .email("test@email.com")
                .username("123456789012345678901")
                .gender("M")
                .year(1999)
                .month(1)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/mypage")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("닉네임은 20자 이내로 입력해주세요." + "\n"));
    }

    @DisplayName("마이페이지 수정 시 성별은 필수값이다.")
    @Test
    @WithCustomMockUser
    void updateMyPageWithoutGender() throws Exception {
        //given
        RequestUserDto request = RequestUserDto.builder()
                .email("test@email.com")
                .username("test")
                .year(1999)
                .month(1)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/mypage")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("성별을 입력해주세요." + "\n"));
    }

    @DisplayName("성별 1자 초과 시 마이페이지 수정에 실패한다.")
    @Test
    @WithCustomMockUser
    void updateMyPageWithOverGender() throws Exception {
        //given
        RequestUserDto request = RequestUserDto.builder()
                .email("test@email.com")
                .username("test")
                .gender("MM")
                .year(1999)
                .month(1)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/mypage")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("성별은 1자 이내로 입력해주세요." + "\n"));
    }

    @DisplayName("마이페이지 수정 시 태어난 년도는 필수값이다.")
    @Test
    @WithCustomMockUser
    void updateMyPageWithoutYear() throws Exception {
        //given
        RequestUserDto request = RequestUserDto.builder()
                .email("test@email.com")
                .username("test")
                .gender("M")
                .month(1)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/mypage")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("태어난 년도를 입력해주세요." + "\n"));
    }

    @DisplayName("마이페이지 수정 시 태어난 월은 필수값이다.")
    @Test
    @WithCustomMockUser
    void updateMyPageWithoutMonth() throws Exception {
        //given
        RequestUserDto request = RequestUserDto.builder()
                .email("test@email.com")
                .username("test")
                .gender("M")
                .year(1990)
                .day(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/mypage")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("태어난 월을 입력해주세요." + "\n"));
    }

    @DisplayName("마이페이지 수정 시 태어난 일자는 필수값이다.")
    @Test
    @WithCustomMockUser
    void updateMyPageWithoutDay() throws Exception {
        //given
        RequestUserDto request = RequestUserDto.builder()
                .email("test@email.com")
                .username("test")
                .gender("M")
                .year(1990)
                .month(1)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/mypage")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("태어난 일자를 입력해주세요." + "\n"));
    }

    @DisplayName("비밀번호를 수정한다.")
    @Test
    @WithCustomMockUser
    void updatePassword() throws Exception {
        //given
        String password = "password";
        String newPassword = "newPassword";

        when(userService.passwordCheck(1L, password)).thenReturn(true);

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/password/check/" + password + "/" + newPassword)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("비밀번호 변경 성공"));
    }

    @DisplayName("기존 회원 정보와 일치하지 않아 비밀번호 수정에 실패한다.")
    @Test
    @WithCustomMockUser
    void updatePasswordWithInvalidInfo() throws Exception {
        //given
        String password = "password";
        String newPassword = "newPassword";

        when(userService.passwordCheck(1L, password)).thenReturn(false);

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/password/check/" + password + "/" + newPassword)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("현재 비밀번호 인증 실패"));
    }

    @DisplayName("체형 정보를 조회한다.")
    @Test
    @WithCustomMockUser
    void getMeasurement() throws Exception {
        //given
        ResponseMeasurementDto response = ResponseMeasurementDto.builder()
                .height(111.0)
                .weight(111.0)
                .build();

        when(userService.measurementInfo(1L)).thenReturn(response);

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/users/mysize")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("체형 정보를 수정한다.")
    @Test
    @WithCustomMockUser
    void updateMeasurement() throws Exception {
        //given
        RequestMeasurementDto request = RequestMeasurementDto.builder()
                .height(111.0)
                .weight(111.0)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/mysize")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("체형 정보 수정 시 키는 필수값이다.")
    @Test
    @WithCustomMockUser
    void updateMeasurementWithoutHeight() throws Exception {
        //given
        RequestMeasurementDto request = RequestMeasurementDto.builder()
                .weight(111.0)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/mysize")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("키를 입력해주세요." + "\n"));
    }

    @DisplayName("체형 정보 수정 시 몸무게는 필수값이다.")
    @Test
    @WithCustomMockUser
    void updateMeasurementWithoutWeight() throws Exception {
        //given
        RequestMeasurementDto request = RequestMeasurementDto.builder()
                .height(111.0)
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/mysize")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("몸무게를 입력해주세요." + "\n"));
    }

    @DisplayName("체형 실루엣 이미지를 제공한다.")
    @Test
    @WithCustomMockUser
    void getSilhouetteFromBody() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("bodyFile",
                "test.png",
                "image/png",
                "test".getBytes());
        String isFront = "isFront";
        ValueOperations<String, Object> valueOperationsMock = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);
        when(restTemplate.postForObject(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(ResponseSilhouetteApiDto.builder()
                .image_fname("test")
                .build());

        //when //then
        mockMvc.perform(
                        multipart("/api/v1/auth/users/mysize/silhouette")
                                .file(file).part(new MockPart("bodyFile", "bytes".getBytes(StandardCharsets.UTF_8)))
                                .param("type", isFront)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("유저의 즐겨찾기 상품을 조회한다.")
    @Test
    @WithCustomMockUser
    void getFavoriteProduct() throws Exception {
        //given
        when(favoriteService.userFavoriteProduct(1L, PageRequest.of(0, 20))).thenReturn(null);

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/users/favorite_goods")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("미리보기 버전에서 유저의 즐겨찾기 상품을 조회한다.")
    @Test
    @WithCustomMockUser
    void getFavoriteProductOnPreivew() throws Exception {
        //given
        when(favoriteService.userFavoriteProduct(1L, PageRequest.of(0, 20))).thenReturn(null);

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/users/favorite_goods/preview")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("유저의 즐겨찾기 상품을 등록한다.")
    @Test
    @WithCustomMockUser
    void saveFavoriteProduct() throws Exception {
        //given
        Long productId = 1L;

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/favorite_goods/" + productId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("유저 즐겨찾기 상품 등록 완료"));
    }

    @DisplayName("유저의 즐겨찾기 상품을 삭제한다.")
    @Test
    @WithCustomMockUser
    void deleteFavoriteProduct() throws Exception {
        //given
        Long productId = 1L;

        //when //then
        mockMvc.perform(
                        delete("/api/v1/auth/users/favorite_goods/" + productId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("유저 즐겨찾기 상품 삭제 완료"));
    }

    @DisplayName("미리보기 버전에서 유저의 최근 본 상품을 조회한다.")
    @Test
    @WithCustomMockUser
    void recentProductOnPreview() throws Exception {
        //given
        when(userService.recentProductPreview(1L)).thenReturn(List.of());

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/users/recent/preview")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @DisplayName("유저의 최근 본 상품을 조회한다.")
    @Test
    @WithCustomMockUser
    void recentProduct() throws Exception {
        //given
        when(userService.recentProduct(1L, PageRequest.of(0, 20))).thenReturn(null);

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/users/recent")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("체형 스마트 분석을 시도한다.")
    @Test
    @WithCustomMockUser
    void recommendMeasurement() throws Exception {
        //given
        RequestSmartMeasurementDto request = RequestSmartMeasurementDto.builder()
                .front("front")
                .side("side")
                .height(111.0)
                .weight(111.0)
                .sex("sex")
                .build();

        when(restTemplate.postForObject(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(null);

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/recommendation/measurement")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("체형 스마트 분석 시도 시 정면 사진은 필수값이다.")
    @Test
    @WithCustomMockUser
    void recommendMeasurementWithoutFront() throws Exception {
        //given
        RequestSmartMeasurementDto request = RequestSmartMeasurementDto.builder()
                .side("side")
                .height(111.0)
                .weight(111.0)
                .sex("sex")
                .build();

        when(restTemplate.postForObject(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(null);

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/recommendation/measurement")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("정면 사진은 필수입니다." + "\n"));
    }

    @DisplayName("체형 스마트 분석 시도 시 측면 사진은 필수값이다.")
    @Test
    @WithCustomMockUser
    void recommendMeasurementWithoutSide() throws Exception {
        //given
        RequestSmartMeasurementDto request = RequestSmartMeasurementDto.builder()
                .front("front")
                .height(111.0)
                .weight(111.0)
                .sex("sex")
                .build();

        when(restTemplate.postForObject(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(null);

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/recommendation/measurement")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("측면 사진은 필수입니다." + "\n"));
    }

    @DisplayName("체형 스마트 분석 시도 시 키 정보는 필수값이다.")
    @Test
    @WithCustomMockUser
    void recommendMeasurementWithoutHeight() throws Exception {
        //given
        RequestSmartMeasurementDto request = RequestSmartMeasurementDto.builder()
                .front("front")
                .side("side")
                .weight(111.0)
                .sex("sex")
                .build();

        when(restTemplate.postForObject(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(null);

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/recommendation/measurement")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("키 정보는 필수입니다." + "\n"));
    }

    @DisplayName("체형 스마트 분석 시도 시 정면 사진은 필수값이다.")
    @Test
    @WithCustomMockUser
    void recommendMeasurementWithoutWeight() throws Exception {
        //given
        RequestSmartMeasurementDto request = RequestSmartMeasurementDto.builder()
                .front("front")
                .side("side")
                .height(111.0)
                .sex("sex")
                .build();

        when(restTemplate.postForObject(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(null);

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/recommendation/measurement")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("몸무게 정보는 필수입니다." + "\n"));
    }

    @DisplayName("체형 스마트 분석 시도 시 성별 정보는 필수값이다.")
    @Test
    @WithCustomMockUser
    void recommendMeasurementWithoutSex() throws Exception {
        //given
        RequestSmartMeasurementDto request = RequestSmartMeasurementDto.builder()
                .front("front")
                .side("side")
                .height(111.0)
                .weight(111.0)
                .build();

        when(restTemplate.postForObject(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(null);

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/users/recommendation/measurement")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("성별 정보는 필수입니다." + "\n"));
    }

    @DisplayName("미리보기 버전에서 추천 상품을 조회한다.")
    @Test
    @WithCustomMockUser
    void recommendProductOnPreview() throws Exception {
        //given
        when(productService.productWithRecentRecommendation(1L)).thenReturn(Products.builder()
                .products(List.of())
                .build());
        when(restTemplate.postForObject(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(ResponseRecommendProductDto.builder()
                        .product_ids(List.of())
                        .build());
        when(productService.saveRecentRecommendation(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(null);
        when(productService.recommendProduct(List.of(), true)).thenReturn(List.of());

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/users/recommendation/preview")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("추천 상품을 조회한다.")
    @Test
    @WithCustomMockUser
    void recommendProduct() throws Exception {
        //given
        when(productService.productWithRecentRecommendation(1L)).thenReturn(Products.builder()
                .products(List.of())
                .build());
        when(restTemplate.postForObject(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(ResponseRecommendProductDto.builder()
                        .product_ids(List.of())
                        .build());
        when(productService.saveRecentRecommendation(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(null);
        when(productService.recommendProduct(List.of(), false)).thenReturn(List.of());

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/users/recommendation")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("미리보기 버전에서 비슷한 체형 고객 pick 상품을 조회한다.")
    @Test
    @WithCustomMockUser
    void recommendPickedProductOnPreview() throws Exception {
        //given
        when(productService.productWithUserRecommendation(1L)).thenReturn(Products.builder()
                .products(List.of())
                .build());
        when(restTemplate.postForObject(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(ResponseRecommendProductOnUserDto.builder()
                        .product_ids(List.of())
                        .build());
        when(productService.saveRecentRecommendation(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(null);
        when(productService.recommendProduct(List.of(), true)).thenReturn(List.of());

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/users/recommendation/pick/preview")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("비슷한 체형 고객 pick 상품을 조회한다.")
    @Test
    @WithCustomMockUser
    void recommendPickedProduct() throws Exception {
        //given
        when(productService.productWithUserRecommendation(1L)).thenReturn(Products.builder()
                .products(List.of())
                .build());
        when(restTemplate.postForObject(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(ResponseRecommendProductOnUserDto.builder()
                        .product_ids(List.of())
                        .build());
        when(productService.saveRecentRecommendation(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(null);
        when(productService.recommendProduct(List.of(), true)).thenReturn(List.of());

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/users/recommendation/pick")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("회원 탈퇴를 시도한다.")
    @Test
    @WithCustomMockUser
    void deleteUser() throws Exception {
        //given
        RequestUserCheckDto request = RequestUserCheckDto.builder()
                .email("test@email.com")
                .password("password")
                .build();

        when(userService.isValidEmailAndPassword("test@email.com", "password")).thenReturn(true);
        when(userService.isSameUser(1L, "test@email.com")).thenReturn(true);

        //when //then
        mockMvc.perform(
                        delete("/api/v1/auth/users")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("회원 탈퇴 완료"));
    }

    @DisplayName("회원 탈퇴 시 이메일은 필수값이다.")
    @Test
    @WithCustomMockUser
    void deleteUserWithoutEmail() throws Exception {
        //given
        RequestUserCheckDto request = RequestUserCheckDto.builder()
                .password("password")
                .build();

        when(userService.isValidEmailAndPassword("test@email.com", "password")).thenReturn(true);
        when(userService.isSameUser(1L, "test@email.com")).thenReturn(true);

        //when //then
        mockMvc.perform(
                        delete("/api/v1/auth/users")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이메일은 필수입니다." + "\n"));
    }

    @DisplayName("회원 탈퇴 시 비밀번호는 필수값이다.")
    @Test
    @WithCustomMockUser
    void deleteUserWithoutPassword() throws Exception {
        //given
        RequestUserCheckDto request = RequestUserCheckDto.builder()
                .email("test@email.com")
                .build();

        when(userService.isValidEmailAndPassword("test@email.com", "password")).thenReturn(true);
        when(userService.isSameUser(1L, "test@email.com")).thenReturn(true);

        //when //then
        mockMvc.perform(
                        delete("/api/v1/auth/users")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("비밀번호는 필수입니다." + "\n"));
    }

    @DisplayName("유저가 즐겨찾기 쇼핑몰 등록한다.")
    @Test
    @WithCustomMockUser
    void setFavoriteMall() throws Exception {
        //given
        Long mallId = 1L;

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/favorites/" + mallId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("즐겨찾기 등록 완료"));
    }

    @DisplayName("유저가 즐겨찾기 쇼핑몰 등록한다.")
    @Test
    @WithCustomMockUser
    void deleteFavoriteMall() throws Exception {
        //given
        Long mallId = 1L;

        //when //then
        mockMvc.perform(
                        delete("/api/v1/auth/favorites/" + mallId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("즐겨찾기 삭제 완료"));
    }
}