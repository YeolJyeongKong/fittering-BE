package fittering.mall.controller;

import fittering.mall.config.ControllerTestSupport;
import fittering.mall.config.WithCustomMockUser;
import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.domain.RestPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchControllerTest extends ControllerTestSupport {

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("상품 이름을 검색해서 조회한다.")
    @Test
    @WithCustomMockUser
    void getProductsBySearch() throws Exception {
        //given
        String keyword = "상품";
        String gender = "M";
        Long filterId = 1L;
        ResponseProductPreviewDto searchedProduct = ResponseProductPreviewDto.builder()
                .productId(1L)
                .productName("상품1")
                .build();
        Page<ResponseProductPreviewDto> response = new RestPage<>(List.of(searchedProduct), 0, 20, 20);

        when(searchService.products(keyword, gender, filterId, PageRequest.of(0, 20)))
                .thenReturn((RestPage<ResponseProductPreviewDto>) response);

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/search/" + keyword + "/" + gender + "/" + filterId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("검색 시 연관된 상품 또는 쇼핑몰을 조회한다.")
    @Test
    @WithCustomMockUser
    void getProductsAndMallsByRelatedSearch() throws Exception {
        //given
        String keyword = "상품";

        when(searchService.relatedSearchProducts(keyword)).thenReturn(List.of());
        when(searchService.relatedSearchMalls(keyword)).thenReturn(List.of());

        //when //then
        mockMvc.perform(
                        get("/api/v1/search/" + keyword)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}