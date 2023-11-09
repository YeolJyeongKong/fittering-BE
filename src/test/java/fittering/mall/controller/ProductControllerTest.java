package fittering.mall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fittering.mall.config.WithCustomMockUser;
import fittering.mall.config.jwt.JwtAuthenticationFilter;
import fittering.mall.controller.dto.request.RequestProductDetailDto;
import fittering.mall.controller.dto.response.ResponseOuterDto;
import fittering.mall.controller.dto.response.ResponseProductCategoryDto;
import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.domain.RestPage;
import fittering.mall.domain.entity.Mall;
import fittering.mall.domain.entity.Product;
import fittering.mall.service.*;
import fittering.mall.service.dto.ProductParamDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private MallService mallService;

    @MockBean
    private UserService userService;

    @MockBean
    private SizeService sizeService;

    @MockBean
    private RankService rankService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("아우터 상품을 등록한다.")
    @Test
    @WithCustomMockUser
    void createOuterProduct() throws Exception {
        //given
        RequestProductDetailDto request = RequestProductDetailDto.builder()
                .price(10000)
                .name("상품1")
                .gender("M")
                .type(0)
                .image("https://image.test.com")
                .origin("https://origin.test.com")
                .categoryName("outer")
                .subCategoryName("hoodedzip")
                .mallName("열졍콩몰")
                .outerSizes(List.of())
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/products")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("상품 등록 완료"));
    }

    @DisplayName("상의 상품을 등록한다.")
    @Test
    @WithCustomMockUser
    void createTopProduct() throws Exception {
        //given
        RequestProductDetailDto request = RequestProductDetailDto.builder()
                .price(10000)
                .name("상품1")
                .gender("M")
                .type(1)
                .image("https://image.test.com")
                .origin("https://origin.test.com")
                .categoryName("top")
                .subCategoryName("t-shirt")
                .mallName("열졍콩몰")
                .topSizes(List.of())
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/products")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("상품 등록 완료"));
    }

    @DisplayName("원피스 상품을 등록한다.")
    @Test
    @WithCustomMockUser
    void createDressProduct() throws Exception {
        //given
        RequestProductDetailDto request = RequestProductDetailDto.builder()
                .price(10000)
                .name("상품1")
                .gender("M")
                .type(2)
                .image("https://image.test.com")
                .origin("https://origin.test.com")
                .categoryName("dress")
                .subCategoryName("dress")
                .mallName("열졍콩몰")
                .dressSizes(List.of())
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/products")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("상품 등록 완료"));
    }

    @DisplayName("하의 상품을 등록한다.")
    @Test
    @WithCustomMockUser
    void createBottomProduct() throws Exception {
        //given
        RequestProductDetailDto request = RequestProductDetailDto.builder()
                .price(10000)
                .name("상품1")
                .gender("M")
                .type(3)
                .image("https://image.test.com")
                .origin("https://origin.test.com")
                .categoryName("bottom")
                .subCategoryName("pants")
                .mallName("열졍콩몰")
                .bottomSizes(List.of())
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/products")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("상품 등록 완료"));
    }

    @DisplayName("상품 등록 시 가격은 필수값이다.")
    @Test
    @WithCustomMockUser
    void createProductWithoutPrice() throws Exception {
        //given
        RequestProductDetailDto request = RequestProductDetailDto.builder()
                .name("상품1")
                .gender("M")
                .type(3)
                .image("https://image.test.com")
                .origin("https://origin.test.com")
                .categoryName("bottom")
                .subCategoryName("pants")
                .mallName("열졍콩몰")
                .dressSizes(List.of())
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/products")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("상품 가격은 필수입니다." + "\n"));
    }

    @DisplayName("상품 등록 시 상품 이름은 필수값이다.")
    @Test
    @WithCustomMockUser
    void createProductWithoutName() throws Exception {
        //given
        RequestProductDetailDto request = RequestProductDetailDto.builder()
                .price(10000)
                .gender("M")
                .type(3)
                .image("https://image.test.com")
                .origin("https://origin.test.com")
                .categoryName("bottom")
                .subCategoryName("pants")
                .mallName("열졍콩몰")
                .dressSizes(List.of())
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/products")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("상품 이름은 필수입니다." + "\n"));
    }

    @DisplayName("상품 등록 시 상품 이름은 50자 이하여야 한다.")
    @Test
    @WithCustomMockUser
    void createProductWithOverName() throws Exception {
        //given
        RequestProductDetailDto request = RequestProductDetailDto.builder()
                .name("012345678901234567890123456789012345678901234567890")
                .price(10000)
                .gender("M")
                .type(3)
                .image("https://image.test.com")
                .origin("https://origin.test.com")
                .categoryName("bottom")
                .subCategoryName("pants")
                .mallName("열졍콩몰")
                .dressSizes(List.of())
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/products")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("상품 이름은 50자 이하여야 합니다." + "\n"));
    }

    @DisplayName("상품 등록 시 성별은 필수값이다.")
    @Test
    @WithCustomMockUser
    void createProductWithoutGender() throws Exception {
        //given
        RequestProductDetailDto request = RequestProductDetailDto.builder()
                .name("상품1")
                .price(10000)
                .type(3)
                .image("https://image.test.com")
                .origin("https://origin.test.com")
                .categoryName("bottom")
                .subCategoryName("pants")
                .mallName("열졍콩몰")
                .dressSizes(List.of())
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/products")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("상품 성별 정보는 필수입니다." + "\n"));
    }

    @DisplayName("상품 등록 시 성별은 1자 이하여야 한다.")
    @Test
    @WithCustomMockUser
    void createProductWithOverGender() throws Exception {
        //given
        RequestProductDetailDto request = RequestProductDetailDto.builder()
                .name("상품1")
                .gender("MM")
                .price(10000)
                .type(3)
                .image("https://image.test.com")
                .origin("https://origin.test.com")
                .categoryName("bottom")
                .subCategoryName("pants")
                .mallName("열졍콩몰")
                .dressSizes(List.of())
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/products")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("상품 성별 정보는 1자 이하여야 합니다." + "\n"));
    }

    @DisplayName("상품 등록 시 타입은 필수값이다.")
    @Test
    @WithCustomMockUser
    void createProductWithoutType() throws Exception {
        //given
        RequestProductDetailDto request = RequestProductDetailDto.builder()
                .price(10000)
                .name("상품1")
                .gender("M")
                .image("https://image.test.com")
                .origin("https://origin.test.com")
                .categoryName("bottom")
                .subCategoryName("pants")
                .mallName("열졍콩몰")
                .dressSizes(List.of())
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/products")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("상품 종류는 필수입니다." + "\n"));
    }

    @DisplayName("상품 등록 시 이미지는 필수값이다.")
    @Test
    @WithCustomMockUser
    void createProductWithoutImage() throws Exception {
        //given
        RequestProductDetailDto request = RequestProductDetailDto.builder()
                .price(10000)
                .name("상품1")
                .gender("M")
                .type(3)
                .origin("https://origin.test.com")
                .categoryName("bottom")
                .subCategoryName("pants")
                .mallName("열졍콩몰")
                .dressSizes(List.of())
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/products")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("상품 이미지는 필수입니다." + "\n"));
    }

    @DisplayName("상품 등록 시 출처는 필수값이다.")
    @Test
    @WithCustomMockUser
    void createProductWithoutOrigin() throws Exception {
        //given
        RequestProductDetailDto request = RequestProductDetailDto.builder()
                .price(10000)
                .name("상품1")
                .gender("M")
                .type(3)
                .image("https://image.test.com")
                .categoryName("bottom")
                .subCategoryName("pants")
                .mallName("열졍콩몰")
                .dressSizes(List.of())
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/products")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("상품 출처는 필수입니다." + "\n"));
    }

    @DisplayName("상품 등록 시 카테고리는 필수값이다.")
    @Test
    @WithCustomMockUser
    void createProductWithoutCategory() throws Exception {
        //given
        RequestProductDetailDto request = RequestProductDetailDto.builder()
                .price(10000)
                .name("상품1")
                .gender("M")
                .type(3)
                .image("https://image.test.com")
                .origin("https://origin.test.com")
                .subCategoryName("pants")
                .mallName("열졍콩몰")
                .dressSizes(List.of())
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/products")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("상품 카테고리는 필수입니다." + "\n"));
    }

    @DisplayName("상품 등록 시 서브 카테고리는 필수값이다.")
    @Test
    @WithCustomMockUser
    void createProductWithoutSubCategory() throws Exception {
        //given
        RequestProductDetailDto request = RequestProductDetailDto.builder()
                .price(10000)
                .name("상품1")
                .gender("M")
                .type(3)
                .image("https://image.test.com")
                .origin("https://origin.test.com")
                .categoryName("bottom")
                .mallName("열졍콩몰")
                .dressSizes(List.of())
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/products")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("상품 서브 카테고리는 필수입니다." + "\n"));
    }

    @DisplayName("상품 등록 시 쇼핑몰 이름은 필수값이다.")
    @Test
    @WithCustomMockUser
    void createProductWithoutMallName() throws Exception {
        //given
        RequestProductDetailDto request = RequestProductDetailDto.builder()
                .price(10000)
                .name("상품1")
                .gender("M")
                .type(3)
                .image("https://image.test.com")
                .origin("https://origin.test.com")
                .categoryName("bottom")
                .subCategoryName("pants")
                .dressSizes(List.of())
                .build();

        //when //then
        mockMvc.perform(
                        post("/api/v1/auth/products")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("상품 쇼핑몰 이름은 필수입니다." + "\n"));
    }

    @DisplayName("카테고리별로 상품을 조회한다.")
    @Test
    @WithCustomMockUser
    void getProductWithCategory() throws Exception {
        //given
        Long categoryId = 1L;
        String gender = "M";
        Long filterId = 1L;
        ProductParamDto request = ProductParamDto.builder()
                .categoryId(categoryId)
                .gender(gender)
                .filterId(filterId)
                .build();
        ResponseProductPreviewDto product = ResponseProductPreviewDto.builder()
                .productId(1L)
                .build();
        Page<ResponseProductPreviewDto> response = new RestPage<>(List.of(product), 0, 20, 20);

        when(productService.productWithCategory(request, PageRequest.of(0, 20)))
                .thenReturn((RestPage<ResponseProductPreviewDto>) response);

        //when //then
        mockMvc.perform(
                        get("/api/v1/categories/" + categoryId + "/" + gender + "/" + filterId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("카테고리별 상품 조회 시 입력된 성별은 1자 이하여야 한다.")
    @Test
    @WithCustomMockUser
    void getProductWithCategoryWithOverGender() throws Exception {
        //given
        Long categoryId = 1L;
        String gender = "MM";
        Long filterId = 1L;
        ProductParamDto request = ProductParamDto.builder()
                .categoryId(categoryId)
                .gender(gender)
                .filterId(filterId)
                .build();
        ResponseProductPreviewDto product = ResponseProductPreviewDto.builder()
                .productId(1L)
                .build();
        Page<ResponseProductPreviewDto> response = new RestPage<>(List.of(product), 0, 20, 20);

        when(productService.productWithCategory(request, PageRequest.of(0, 20)))
                .thenReturn((RestPage<ResponseProductPreviewDto>) response);

        //when //then
        mockMvc.perform(
                        get("/api/v1/categories/" + categoryId + "/" + gender + "/" + filterId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("성별은 1자 이하여야 합니다."));
    }

    @DisplayName("서브 카테고리별로 상품을 조회한다.")
    @Test
    @WithCustomMockUser
    void getProductWithSubCategory() throws Exception {
        //given
        Long subCategoryId = 1L;
        String gender = "M";
        Long filterId = 1L;
        ProductParamDto request = ProductParamDto.builder()
                .categoryId(subCategoryId)
                .gender(gender)
                .filterId(filterId)
                .build();
        ResponseProductPreviewDto product = ResponseProductPreviewDto.builder()
                .productId(1L)
                .build();
        Page<ResponseProductPreviewDto> response = new RestPage<>(List.of(product), 0, 20, 20);

        when(productService.productWithSubCategory(request, PageRequest.of(0, 20)))
                .thenReturn((RestPage<ResponseProductPreviewDto>) response);

        //when //then
        mockMvc.perform(
                        get("/api/v1/categories/sub/" + subCategoryId + "/" + gender + "/" + filterId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("서브 카테고리별 상품 조회 시 입력된 성별은 1자 이하여야 한다.")
    @Test
    @WithCustomMockUser
    void getProductWithSubCategoryWithOverGender() throws Exception {
        //given
        Long subCategoryId = 1L;
        String gender = "MM";
        Long filterId = 1L;
        ProductParamDto request = ProductParamDto.builder()
                .categoryId(subCategoryId)
                .gender(gender)
                .filterId(filterId)
                .build();
        ResponseProductPreviewDto product = ResponseProductPreviewDto.builder()
                .productId(1L)
                .build();
        Page<ResponseProductPreviewDto> response = new RestPage<>(List.of(product), 0, 20, 20);

        when(productService.productWithSubCategory(request, PageRequest.of(0, 20)))
                .thenReturn((RestPage<ResponseProductPreviewDto>) response);

        //when //then
        mockMvc.perform(
                        get("/api/v1/categories/sub/" + subCategoryId + "/" + gender + "/" + filterId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("성별은 1자 이하여야 합니다."));
    }

    @DisplayName("카테고리별 상품 개수를 조회한다.")
    @Test
    @WithCustomMockUser
    void getCountOfCategory() throws Exception {
        //given
        ResponseProductCategoryDto countOfProducts = ResponseProductCategoryDto.builder()
                .category("bottom")
                .count(1L)
                .build();

        when(productService.multipleProductCountWithCategory()).thenReturn(List.of(countOfProducts));

        //when //then
        mockMvc.perform(
                        get("/api/v1/categories/count")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray());
    }

    @DisplayName("쇼핑몰에서 카테고리별 상품을 조회한다.")
    @Test
    @WithCustomMockUser
    void getProductWithCategoryOfMall() throws Exception {
        //given
        Long mallId = 1L;
        Long categoryId = 1L;
        String gender = "M";
        Long filterId = 1L;
        ProductParamDto productParamDto = ProductParamDto.builder()
                .mallId(mallId)
                .categoryId(categoryId)
                .gender(gender)
                .filterId(filterId)
                .build();

        ResponseProductPreviewDto product = ResponseProductPreviewDto.builder()
                .productId(1L)
                .build();
        Page<ResponseProductPreviewDto> response = new RestPage<>(List.of(product), 0, 20, 20);

        when(productService.productWithCategoryOfMall(productParamDto, PageRequest.of(0, 20)))
                .thenReturn((RestPage<ResponseProductPreviewDto>) response);

        //when //then
        mockMvc.perform(
                        get("/api/v1/malls/" + mallId + "/" + categoryId + "/" + gender + "/" + filterId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("쇼핑몰에서 서브 카테고리별 상품을 조회한다.")
    @Test
    @WithCustomMockUser
    void getProductWithSubCategoryOfMall() throws Exception {
        //given
        Long mallId = 1L;
        Long subCategoryId = 1L;
        String gender = "M";
        Long filterId = 1L;
        ProductParamDto productParamDto = ProductParamDto.builder()
                .mallId(mallId)
                .subCategoryId(subCategoryId)
                .gender(gender)
                .filterId(filterId)
                .build();

        ResponseProductPreviewDto product = ResponseProductPreviewDto.builder()
                .productId(1L)
                .build();
        Page<ResponseProductPreviewDto> response = new RestPage<>(List.of(product), 0, 20, 20);

        when(productService.productWithCategoryOfMall(productParamDto, PageRequest.of(0, 20)))
                .thenReturn((RestPage<ResponseProductPreviewDto>) response);

        //when //then
        mockMvc.perform(
                        get("/api/v1/malls/" + mallId + "/" + subCategoryId + "/" + gender + "/" + filterId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("쇼핑몰에서 카테고리별 상품 개수를 조회한다.")
    @Test
    @WithCustomMockUser
    void getCountOfCategoryOnMall() throws Exception {
        //given
        Long mallId = 1L;
        ResponseProductCategoryDto countOfProducts = ResponseProductCategoryDto.builder()
                .category("bottom")
                .count(1L)
                .build();

        when(productService.productCountWithCategoryOfMall(mallId)).thenReturn(List.of(countOfProducts));

        //when //then
        mockMvc.perform(
                        get("/api/v1/malls/" + mallId + "/categories/count")
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray());
    }

    @DisplayName("아우터 상품을 상세 조회한다.")
    @Test
    @WithCustomMockUser
    void getOuterProductDetail() throws Exception {
        //given
        Long userId = 1L;
        Long productId = 1L;
        Mall mall = Mall.builder()
                .id(1L)
                .name("쇼핑몰")
                .build();
        Product product = Product.builder()
                .id(1L)
                .price(10000)
                .name("상품")
                .gender("M")
                .type(0)
                .image("image")
                .origin("origin")
                .view(0)
                .timeView(0)
                .disabled(0)
                .mall(mall)
                .build();

        ResponseOuterDto response = ResponseOuterDto.builder()
                .productName("상품")
                .price(10000)
                .build();

        when(productService.findById(productId)).thenReturn(product);
        when(productService.outerProductDetail(userId, productId)).thenReturn(response);

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/products/" + productId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("상의 상품을 상세 조회한다.")
    @Test
    @WithCustomMockUser
    void getTopProductDetail() throws Exception {
        //given
        Long userId = 1L;
        Long productId = 1L;
        Mall mall = Mall.builder()
                .id(1L)
                .name("쇼핑몰")
                .build();
        Product product = Product.builder()
                .id(1L)
                .price(10000)
                .name("상품")
                .gender("M")
                .type(1)
                .image("image")
                .origin("origin")
                .view(0)
                .timeView(0)
                .disabled(0)
                .mall(mall)
                .build();

        ResponseOuterDto response = ResponseOuterDto.builder()
                .productName("상품")
                .price(10000)
                .build();

        when(productService.findById(productId)).thenReturn(product);
        when(productService.outerProductDetail(userId, productId)).thenReturn(response);

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/products/" + productId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("원피스 상품을 상세 조회한다.")
    @Test
    @WithCustomMockUser
    void getDressProductDetail() throws Exception {
        //given
        Long userId = 1L;
        Long productId = 1L;
        Mall mall = Mall.builder()
                .id(1L)
                .name("쇼핑몰")
                .build();
        Product product = Product.builder()
                .id(1L)
                .price(10000)
                .name("상품")
                .gender("M")
                .type(2)
                .image("image")
                .origin("origin")
                .view(0)
                .timeView(0)
                .disabled(0)
                .mall(mall)
                .build();

        ResponseOuterDto response = ResponseOuterDto.builder()
                .productName("상품")
                .price(10000)
                .build();

        when(productService.findById(productId)).thenReturn(product);
        when(productService.outerProductDetail(userId, productId)).thenReturn(response);

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/products/" + productId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("상의 상품을 상세 조회한다.")
    @Test
    @WithCustomMockUser
    void getBottomProductDetail() throws Exception {
        //given
        Long userId = 1L;
        Long productId = 1L;
        Mall mall = Mall.builder()
                .id(1L)
                .name("쇼핑몰")
                .build();
        Product product = Product.builder()
                .id(1L)
                .price(10000)
                .name("상품")
                .gender("M")
                .type(3)
                .image("image")
                .origin("origin")
                .view(0)
                .timeView(0)
                .disabled(0)
                .mall(mall)
                .build();

        ResponseOuterDto response = ResponseOuterDto.builder()
                .productName("상품")
                .price(10000)
                .build();

        when(productService.findById(productId)).thenReturn(product);
        when(productService.outerProductDetail(userId, productId)).thenReturn(response);

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/products/" + productId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("미분류 상품은 상세 조회할 수 없다.")
    @Test
    @WithCustomMockUser
    void getNonTypeProductDetail() throws Exception {
        //given
        Long userId = 1L;
        Long productId = 1L;
        Mall mall = Mall.builder()
                .id(1L)
                .name("쇼핑몰")
                .build();
        Product product = Product.builder()
                .id(1L)
                .price(10000)
                .name("상품")
                .gender("M")
                .type(4)
                .image("image")
                .origin("origin")
                .view(0)
                .timeView(0)
                .disabled(0)
                .mall(mall)
                .build();

        ResponseOuterDto response = ResponseOuterDto.builder()
                .productName("상품")
                .price(10000)
                .build();

        when(productService.findById(productId)).thenReturn(product);
        when(productService.outerProductDetail(userId, productId)).thenReturn(response);

        //when //then
        mockMvc.perform(
                        get("/api/v1/auth/products/" + productId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("정의된 타입 없음"));
    }

    @DisplayName("오늘의 랭킹순 상품을 조회한다.")
    @Test
    @WithCustomMockUser
    void getProductOfTimeRank() throws Exception {
        //given
        String gender = "M";

        ResponseProductPreviewDto product = ResponseProductPreviewDto.builder()
                .productId(1L)
                .productName("상품")
                .price(10000)
                .build();

        when(productService.productsOfTimeRank(gender)).thenReturn(List.of(product));

        //when //then
        mockMvc.perform(
                        get("/api/v1/products/rank/" + gender)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray());
    }

    @DisplayName("오늘의 랭킹순 상품을 조회 시 1자 이하의 성별 정보가 입력되어야 한다.")
    @Test
    @WithCustomMockUser
    void getProductOfTimeRankWithOverGender() throws Exception {
        //given
        String gender = "MM";

        ResponseProductPreviewDto product = ResponseProductPreviewDto.builder()
                .productId(1L)
                .productName("상품")
                .price(10000)
                .build();

        when(productService.productsOfTimeRank(gender)).thenReturn(List.of(product));

        //when //then
        mockMvc.perform(
                        get("/api/v1/products/rank/" + gender)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("성별은 1자 이하여야 합니다."));
    }
}