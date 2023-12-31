package fittering.mall.controller;

import fittering.mall.controller.dto.request.RequestProductDetailDto;
import fittering.mall.controller.dto.response.*;
import fittering.mall.domain.mapper.ProductMapper;
import fittering.mall.domain.mapper.SizeMapper;
import fittering.mall.service.dto.ProductParamDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import fittering.mall.domain.entity.*;
import fittering.mall.service.*;

import java.util.List;

import static fittering.mall.controller.ControllerUtils.getValidationErrorResponse;
import static fittering.mall.domain.entity.Product.*;

@Tag(name = "상품", description = "상품 서비스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final MallService mallService;
    private final UserService userService;
    private final SizeService sizeService;
    private final RankService rankService;

    @Operation(summary = "상품 등록")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"상품 등록 완료\"")))
    @PostMapping("/auth/products")
    public ResponseEntity<?> save(@RequestBody @Valid RequestProductDetailDto requestProductDto,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getValidationErrorResponse(bindingResult);
        }
        Category category = categoryService.findByName(requestProductDto.getCategoryName());
        SubCategory subCategory = categoryService.findByNameOfSubCategory(requestProductDto.getSubCategoryName());
        Mall mall = mallService.findByName(requestProductDto.getMallName());
        Product product = productService.save(ProductMapper.INSTANCE.toProduct(
                requestProductDto, 0, 0, category, subCategory, mall, 0));
        productService.saveProductDescriptions(requestProductDto.getProductDescriptions(), product);

        if (requestProductDto.getType().equals(OUTER)) {
            getSizesOfOuter(requestProductDto, product);
            return new ResponseEntity<>("상품 등록 완료", HttpStatus.OK);
        }

        if (requestProductDto.getType().equals(TOP)) {
            getSizesOfTop(requestProductDto, product);
            return new ResponseEntity<>("상품 등록 완료", HttpStatus.OK);
        }

        if (requestProductDto.getType().equals(DRESS)) {
            getSizesOfDress(requestProductDto, product);
            return new ResponseEntity<>("상품 등록 완료", HttpStatus.OK);
        }

        getSizesOfBottom(requestProductDto, product);
        return new ResponseEntity<>("상품 등록 완료", HttpStatus.OK);
    }

    @Operation(summary = "카테고리별 상품 조회 (대분류)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/categories/{categoryId}/{gender}/{filterId}")
    public ResponseEntity<?> productWithCategory(@PathVariable("categoryId") @NotEmpty Long categoryId,
                                                 @PathVariable("gender") @NotEmpty String gender,
                                                 @PathVariable("filterId") @NotEmpty Long filterId,
                                                 Pageable pageable) {
        if (gender.length() > 1) {
            return new ResponseEntity<>("성별은 1자 이하여야 합니다.", HttpStatus.BAD_REQUEST);
        }
        ProductParamDto productParamDto = ProductParamDto.builder()
                .categoryId(categoryId)
                .gender(gender)
                .filterId(filterId)
                .build();
        Page<ResponseProductPreviewDto> products = productService.productWithCategory(productParamDto, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "카테고리별 상품 조회 (소분류)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/categories/sub/{subCategoryId}/{gender}/{filterId}")
    public ResponseEntity<?> productWithSubCategory(@PathVariable("subCategoryId") @NotEmpty Long subCategoryId,
                                                    @PathVariable("gender") @NotEmpty String gender,
                                                    @PathVariable("filterId") @NotEmpty Long filterId,
                                                    Pageable pageable) {
        if (gender.length() > 1) {
            return new ResponseEntity<>("성별은 1자 이하여야 합니다.", HttpStatus.BAD_REQUEST);
        }
        ProductParamDto productParamDto = ProductParamDto.builder()
                .subCategoryId(subCategoryId)
                .gender(gender)
                .filterId(filterId)
                .build();
        Page<ResponseProductPreviewDto> products = productService.productWithSubCategory(productParamDto, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "카테고리별 상품 개수 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductAllCategoryDto.class))))
    @GetMapping("/categories/count")
    public ResponseEntity<?> multipleProductCountWithCategory() {
        ResponseProductAllCategoryDto categoryWithProductCounts = productService.multipleProductCountWithCategory();
        return new ResponseEntity<>(categoryWithProductCounts, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 카테고리별 상품 조회 (대분류)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/malls/{mallId}/{categoryId}/{gender}/{filterId}")
    public ResponseEntity<?> productWithCategoryOfMall(@PathVariable("mallId") @NotEmpty Long mallId,
                                                       @PathVariable("categoryId") @NotEmpty Long categoryId,
                                                       @PathVariable("gender") @NotEmpty String gender,
                                                       @PathVariable("filterId") @NotEmpty Long filterId,
                                                       Pageable pageable) {
        ProductParamDto productParamDto = ProductParamDto.builder()
                .mallId(mallId)
                .categoryId(categoryId)
                .gender(gender)
                .filterId(filterId)
                .build();
        Page<ResponseProductPreviewDto> products = productService.productWithCategoryOfMall(productParamDto, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 카테고리별 상품 조회 (소분류)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/malls/sub/{mallId}/{subCategoryId}/{gender}/{filterId}")
    public ResponseEntity<?> productWithSubCategoryOfMall(@PathVariable("mallId") @NotEmpty Long mallId,
                                                          @PathVariable("subCategoryId") @NotEmpty Long subCategoryId,
                                                          @PathVariable("gender") @NotEmpty String gender,
                                                          @PathVariable("filterId") @NotEmpty Long filterId,
                                                          Pageable pageable) {
        ProductParamDto productParamDto = ProductParamDto.builder()
                .mallId(mallId)
                .subCategoryId(subCategoryId)
                .gender(gender)
                .filterId(filterId)
                .build();
        Page<ResponseProductPreviewDto> products = productService.productWithSubCategoryOfMall(productParamDto, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 내 카테고리별 상품 개수 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductAllCategoryDto.class))))
    @GetMapping("/malls/{mallId}/categories/count")
    public ResponseEntity<?> productCountWithCategoryOfMall(@PathVariable("mallId") @NotEmpty Long mallId) {
        ResponseProductAllCategoryDto categoryWithProductCounts = productService.productCountWithCategoryOfMall(mallId);
        return new ResponseEntity<>(categoryWithProductCounts, HttpStatus.OK);
    }

    /**
     * product.type
     * 0 : 아우터(Outer)
     * 1 : 상의(Top)
     * 2 : 원피스(Dress)
     * 3 : 하의(Bottom)
     */
    @Operation(summary = "상품 상세 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = {
            @Content(schema = @Schema(implementation = ResponseOuterDto.class)),
            @Content(schema = @Schema(implementation = ResponseTopDto.class)),
            @Content(schema = @Schema(implementation = ResponseDressDto.class)),
            @Content(schema = @Schema(implementation = ResponseBottomDto.class))
    })
    @GetMapping("/auth/products/{productId}")
    public ResponseEntity<?> productDetail(@PathVariable("productId") @NotEmpty Long productId,
                                           @AuthenticationPrincipal User user) {
        Product product = productService.findById(productId);
        productService.updateView(productId);
        rankService.updateViewOnMall(user.getId(), product.getMall().getId());

        if (!userService.isRecentProduct(user.getId(), productId)) {
            userService.saveRecentProduct(user.getId(), productId);
        }

        if(product.getType().equals(OUTER)) {
            ResponseOuterDto outerProduct = productService.outerProductDetail(user.getId(), productId);
            return new ResponseEntity<>(outerProduct, HttpStatus.OK);
        }

        if(product.getType().equals(TOP)) {
            ResponseTopDto topProduct = productService.topProductDetail(user.getId(), productId);
            return new ResponseEntity<>(topProduct, HttpStatus.OK);
        }

        if(product.getType().equals(DRESS)) {
            ResponseDressDto dressProduct = productService.dressProductDetail(user.getId(), productId);
            return new ResponseEntity<>(dressProduct, HttpStatus.OK);
        }

        if(product.getType().equals(BOTTOM)) {
            ResponseBottomDto bottomProduct = productService.bottomProductDetail(user.getId(), productId);
            return new ResponseEntity<>(bottomProduct, HttpStatus.OK);
        }

        return new ResponseEntity<>("정의된 타입 없음", HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "오늘의 랭킹순 상품 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/products/rank/{gender}")
    public ResponseEntity<?> productOfTimeRank(@PathVariable("gender") @NotEmpty String gender) {
        if (gender.length() > 1) {
            return new ResponseEntity<>("성별은 1자 이하여야 합니다.", HttpStatus.BAD_REQUEST);
        }
        List<ResponseProductPreviewDto> productsOfTimeRank = productService.productsOfTimeRank(gender);
        return new ResponseEntity<>(productsOfTimeRank, HttpStatus.OK);
    }

    private void getSizesOfOuter(RequestProductDetailDto productDto, Product product) {
        productDto.getOuterSizes().forEach(requestOuterDto ->
                sizeService.saveOuter(SizeMapper.INSTANCE.toOuterDto(requestOuterDto), product));
    }

    private void getSizesOfTop(RequestProductDetailDto productDto, Product product) {
        productDto.getTopSizes().forEach(requestTopDto ->
                sizeService.saveTop(SizeMapper.INSTANCE.toTopDto(requestTopDto), product));
    }

    private void getSizesOfDress(RequestProductDetailDto productDto, Product product) {
        productDto.getDressSizes().forEach(requestDressDto ->
                sizeService.saveDress(SizeMapper.INSTANCE.toDressDto(requestDressDto), product));
    }

    private void getSizesOfBottom(RequestProductDetailDto productDto, Product product) {
        productDto.getBottomSizes().forEach(requestBottomDto ->
                sizeService.saveBottom(SizeMapper.INSTANCE.toBottomDto(requestBottomDto), product));
    }
}
