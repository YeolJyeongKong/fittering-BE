package fittering.mall.controller;

import fittering.mall.config.ControllerTestSupport;
import fittering.mall.config.WithCustomMockUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryControllerTest extends ControllerTestSupport {

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("카테고리를 등록한다.")
    @Test
    @WithCustomMockUser
    void createCategory() throws Exception {
        //given
        String categoryName = "상의";

        //when //then
        mockMvc.perform(
                    post("/api/v1/auth/categories/" + categoryName)
                            .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("카테고리 등록 완료"));
    }

    @DisplayName("10 이상 길이 이상 카테고리 이름은 등록할 수 없다.")
    @Test
    @WithCustomMockUser
    void createCategoryWithOverCategoryName() throws Exception {
        //given
        String categoryName = "1234567890a";

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/categories/" + categoryName)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("카테고리 이름은 1 이상 10 이하여야 합니다."));
    }

    @DisplayName("빈 카테고리 이름은 등록할 수 없다.")
    @Test
    @WithCustomMockUser
    void createCategoryWithEmptyCategoryName() throws Exception {
        //given
        String categoryName = "";

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/categories/" + categoryName)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("서브 카테고리를 등록한다.")
    @Test
    @WithCustomMockUser
    void createSubCategory() throws Exception {
        //given
        String mainCategoryName = "상의";
        String subCategoryName = "티셔츠";

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/categories/sub/" + mainCategoryName + "/" + subCategoryName)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("서브 카테고리 등록 완료"));
    }

    @DisplayName("서브 카테고리 등록 시 10 이상 길이 이상 서브 카테고리 이름은 등록할 수 없다.")
    @Test
    @WithCustomMockUser
    void createSubCategoryWithOverSubCategoryName() throws Exception {
        //given
        String mainCategoryName = "상의";
        String subCategoryName = "1234567890a";

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/categories/sub/" + mainCategoryName + "/" + subCategoryName)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("서브 카테고리 이름은 1 이상 10 이하여야 합니다."));
    }

    @DisplayName("서브 카테고리 등록 시 10 이상 길이 이상 메인 카테고리 이름은 등록할 수 없다.")
    @Test
    @WithCustomMockUser
    void createSubCategoryWithOverCategoryName() throws Exception {
        //given
        String mainCategoryName = "1234567890a";
        String subCategoryName = "티셔츠";

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/categories/sub/" + mainCategoryName + "/" + subCategoryName)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("카테고리 이름은 1 이상 10 이하여야 합니다."));
    }

    @DisplayName("서브 카테고리 등록 시 빈 서브 카테고리 이름은 등록할 수 없다.")
    @Test
    @WithCustomMockUser
    void createSubCategoryWithEmptySubCategoryName() throws Exception {
        //given
        String mainCategoryName = "상의";
        String subCategoryName = "";

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/categories/sub/" + mainCategoryName + "/" + subCategoryName)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("서브 카테고리 등록 시 빈 메인 카테고리 이름은 등록할 수 없다.")
    @Test
    @WithCustomMockUser
    void createSubCategoryWithEmptyCategoryName() throws Exception {
        //given
        String mainCategoryName = "";
        String subCategoryName = "티셔츠";

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/categories/sub/" + mainCategoryName + "/" + subCategoryName)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}