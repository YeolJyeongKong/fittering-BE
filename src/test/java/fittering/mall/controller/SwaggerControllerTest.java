package fittering.mall.controller;

import fittering.mall.config.ControllerTestSupport;
import fittering.mall.config.WithCustomMockUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SwaggerControllerTest extends ControllerTestSupport {

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("Swagger 문서 조회 시 CSS 파일 경로를 얻는다.")
    @Test
    @WithCustomMockUser
    void getCss() throws Exception {
        //given

        //when //then
        mockMvc.perform(
                        get("/swagger-ui/swagger-ui.css")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}