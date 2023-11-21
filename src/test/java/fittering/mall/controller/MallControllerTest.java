package fittering.mall.controller;

import fittering.mall.config.ControllerTestSupport;
import fittering.mall.config.WithCustomMockUser;
import fittering.mall.controller.dto.request.RequestMallDto;
import fittering.mall.controller.dto.response.ResponseMallDto;
import fittering.mall.controller.dto.response.ResponseMallNameAndIdDto;
import fittering.mall.controller.dto.response.ResponseMallPreviewDto;
import fittering.mall.controller.dto.response.ResponseMallWithProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MallControllerTest extends ControllerTestSupport {

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("쇼핑몰을 등록한다.")
    @Test
    @WithCustomMockUser
    void createMall() throws Exception {
        //given
        RequestMallDto request = RequestMallDto.builder()
                .name("열졍콩몰")
                .url("https://www.test.com")
                .description("테스트 쇼핑몰")
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/malls")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("쇼핑몰 등록 완료"));
    }

    @DisplayName("쇼핑몰 등록 시 이름은 필수값이다.")
    @Test
    @WithCustomMockUser
    void createMallWithEmptyName() throws Exception {
        //given
        RequestMallDto request = RequestMallDto.builder()
                .url("https://www.test.com")
                .description("테스트 쇼핑몰")
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/malls")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("쇼핑몰 이름은 필수입니다." + "\n"));
    }

    @DisplayName("쇼핑몰 등록 시 링크는 필수값이다.")
    @Test
    @WithCustomMockUser
    void createMallWithEmptyUrl() throws Exception {
        //given
        RequestMallDto request = RequestMallDto.builder()
                .name("열졍콩몰")
                .description("테스트 쇼핑몰")
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/malls")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("쇼핑몰 링크는 필수입니다." + "\n"));
    }

    @DisplayName("쇼핑몰 등록 시 설명은 필수값이다.")
    @Test
    @WithCustomMockUser
    void createMallWithOverName() throws Exception {
        //given
        RequestMallDto request = RequestMallDto.builder()
                .name("열졍콩몰")
                .url("https://www.test.com")
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/malls")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("쇼핑몰 설명은 필수입니다." + "\n"));
    }

    @DisplayName("쇼핑몰을 조회한다.")
    @Test
    @WithCustomMockUser
    void getMall() throws Exception {
        //given
        Long mallId = 1L;
        ResponseMallDto response = ResponseMallDto.builder()
                .id(1L)
                .url("https://www.test.com")
                .description("테스트 쇼핑몰")
                .build();

        when(mallService.findById(1L, 1L)).thenReturn(response);

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/malls/" + mallId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("등록되지 않은 쇼핑몰 조회 시 빈 값을 반환한다.")
    @Test
    @WithCustomMockUser
    void getMallWhenNoData() throws Exception {
        //given
        Long mallId = 1L;

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/malls/" + mallId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").doesNotExist());
    }

    @DisplayName("쇼핑몰 이름 및 ID 리스트를 조회한다.")
    @Test
    @WithCustomMockUser
    void getMallNameAndIdList() throws Exception {
        //given
        ResponseMallNameAndIdDto mall1 = ResponseMallNameAndIdDto.builder()
                .id(1L)
                .name("열졍콩몰")
                .build();
        ResponseMallNameAndIdDto mall2 = ResponseMallNameAndIdDto.builder()
                .id(2L)
                .name("열졍콩몰2")
                .build();

        when(mallService.findMallNameAndIdList()).thenReturn(List.of(mall1, mall2));

        //when //then
        mockMvc.perform(
                        get("/api/v1/malls/list")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray());
    }

    @DisplayName("쇼핑몰 전체 리스트를 조회한다.")
    @Test
    @WithCustomMockUser
    void getMallList() throws Exception {
        //given
        ResponseMallWithProductDto mall1 = ResponseMallWithProductDto.builder()
                .id(1L)
                .name("열졍콩몰")
                .build();
        ResponseMallWithProductDto mall2 = ResponseMallWithProductDto.builder()
                .id(2L)
                .name("열졍콩몰2")
                .build();

        when(mallService.findAll(1L)).thenReturn(List.of(mall1, mall2));

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/malls/preview/list")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray());
    }

    @DisplayName("쇼핑몰 랭킹을 조회한다.")
    @Test
    @WithCustomMockUser
    void getMallRank() throws Exception {
        //given
        Long userId = 1L;

        ResponseMallWithProductDto mall1 = ResponseMallWithProductDto.builder()
                .id(1L)
                .name("열졍콩몰")
                .build();
        ResponseMallWithProductDto mall2 = ResponseMallWithProductDto.builder()
                .id(2L)
                .name("열졍콩몰2")
                .build();

        when(rankService.mallRank(userId)).thenReturn(List.of(mall1, mall2));

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/malls/rank")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray());
    }

    @DisplayName("쇼핑몰 랭킹 미리보기 버전을 조회한다.")
    @Test
    @WithCustomMockUser
    void getMallRankPreview() throws Exception {
        //given
        Long userId = 1L;
        int mallPreviewSize = 4;

        ResponseMallPreviewDto mall1 = ResponseMallPreviewDto.builder()
                .id(1L)
                .name("열졍콩몰")
                .build();
        ResponseMallPreviewDto mall2 = ResponseMallPreviewDto.builder()
                .id(2L)
                .name("열졍콩몰2")
                .build();

        when(rankService.mallRankPreview(userId, PageRequest.of(0, 20), mallPreviewSize))
                .thenReturn(List.of(mall1, mall2));

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/malls/rank/preview")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray());
    }

    @DisplayName("즐겨찾기 쇼핑몰 리스트를 조회한다.")
    @Test
    @WithCustomMockUser
    void getFavoriteMallList() throws Exception {
        //given
        Long userId = 1L;

        ResponseMallWithProductDto mall1 = ResponseMallWithProductDto.builder()
                .id(1L)
                .name("열졍콩몰")
                .build();
        ResponseMallWithProductDto mall2 = ResponseMallWithProductDto.builder()
                .id(2L)
                .name("열졍콩몰2")
                .build();

        when(favoriteService.userFavoriteMall(userId)).thenReturn(List.of(mall1, mall2));

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/malls/favorite_malls")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray());
    }

    @DisplayName("등록된 즐겨찾기 쇼핑몰이 없으면 조회 시 빈 리스트를 반환한다.")
    @Test
    @WithCustomMockUser
    void getFavoriteMallListWhenNoData() throws Exception {
        //given

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/malls/favorite_malls")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isEmpty());
    }
}