package fittering.mall.controller;

import fittering.mall.config.SecurityConfig;
import fittering.mall.config.jwt.JwtAuthenticationFilter;
import fittering.mall.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CategoryController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        SecurityConfig.class,
                        JwtAuthenticationFilter.class
                }
        )
)
@Slf4j
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @DisplayName("카테고리를 등록한다.")
    @Test
    @WithMockUser(username = "test", roles = "USER")
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
    @WithMockUser(username = "test", roles = "USER")
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
    @WithMockUser(username = "test", roles = "USER")
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
    @WithMockUser(username = "test", roles = "USER")
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
    @WithMockUser(username = "test", roles = "USER")
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
    @WithMockUser(username = "test", roles = "USER")
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
    @WithMockUser(username = "test", roles = "USER")
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
    @WithMockUser(username = "test", roles = "USER")
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