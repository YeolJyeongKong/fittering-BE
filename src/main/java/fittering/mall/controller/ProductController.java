package fittering.mall.controller;

import fittering.mall.domain.dto.controller.request.RequestProductDetailDto;
import fittering.mall.domain.dto.controller.response.*;
import fittering.mall.domain.dto.service.DressProductDto;
import fittering.mall.domain.dto.service.OuterProductDto;
import fittering.mall.domain.dto.service.TopProductDto;
import fittering.mall.domain.mapper.ProductMapper;
import fittering.mall.domain.mapper.SizeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import fittering.mall.config.auth.PrincipalDetails;
import fittering.mall.domain.entity.*;
import fittering.mall.service.*;

import java.util.List;

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
    @PostMapping("/product")
    public ResponseEntity<?> save(@RequestBody RequestProductDetailDto requestProductDto) {
        Category category = categoryService.findByName(requestProductDto.getCategoryName());
        SubCategory subCategory = categoryService.findByNameOfSubCategory(requestProductDto.getSubCategoryName());
        Mall mall = mallService.findByName(requestProductDto.getMallName());
        Product product = productService.save(ProductMapper.INSTANCE.toProduct(
                requestProductDto, 0, 0, category, subCategory, mall));
        productService.saveProductDescription(requestProductDto.getProductDescriptions(), product);

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
    @GetMapping("/category/{categoryId}/{gender}/{filterId}")
    public ResponseEntity<?> productWithCategory(@PathVariable("categoryId") Long categoryId,
                                                 @PathVariable("gender") String gender,
                                                 @PathVariable("filterId") Long filterId,
                                                 Pageable pageable) {
        Page<ResponseProductPreviewDto> products = productService.productWithCategory(categoryId, gender, filterId, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "카테고리별 상품 조회 (소분류)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/category/sub/{subCategoryId}/{gender}/{filterId}")
    public ResponseEntity<?> productWithSubCategory(@PathVariable("subCategoryId") Long subCategoryId,
                                                    @PathVariable("gender") String gender,
                                                    @PathVariable("filterId") Long filterId,
                                                    Pageable pageable) {
        Page<ResponseProductPreviewDto> products = productService.productWithSubCategory(subCategoryId, gender, filterId, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "카테고리별 상품 개수 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductCategoryDto.class))))
    @GetMapping("/category/count")
    public ResponseEntity<?> multipleProductCountWithCategory() {
        List<ResponseProductCategoryDto> categoryWithProductCounts = productService.multipleProductCountWithCategory();
        return new ResponseEntity<>(categoryWithProductCounts, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 카테고리별 상품 조회 (대분류)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/malls/{mallId}/{categoryId}/{gender}/{filterId}")
    public ResponseEntity<?> productWithCategoryOfMall(@PathVariable("mallId") Long mallId,
                                                       @PathVariable("categoryId") Long categoryId,
                                                       @PathVariable("gender") String gender,
                                                       @PathVariable("filterId") Long filterId,
                                                       Pageable pageable) {
        Page<ResponseProductPreviewDto> products = productService.productWithCategoryOfMall(
                mallId, categoryId, gender, filterId, pageable
        );
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 카테고리별 상품 조회 (소분류)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/malls/{mallId}/{subCategoryId}/{gender}/{filterId}")
    public ResponseEntity<?> productWithSubCategoryOfMall(@PathVariable("mallId") Long mallId,
                                                          @PathVariable("subCategoryId") Long subCategoryId,
                                                          @PathVariable("gender") String gender,
                                                          @PathVariable("filterId") Long filterId,
                                                          Pageable pageable) {
        Page<ResponseProductPreviewDto> products = productService.productWithSubCategoryOfMall(
                mallId, subCategoryId, gender, filterId, pageable
        );
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 내 카테고리별 상품 개수 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductCategoryDto.class))))
    @GetMapping("/malls/{mallId}/category/count")
    public ResponseEntity<?> productCountWithCategoryOfMall(@PathVariable("mallId") Long mallId) {
        List<ResponseProductCategoryDto> categoryWithProductCounts = productService.productCountWithCategoryOfMall(mallId);
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
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> productDetail(@PathVariable("productId") Long productId,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Product product = productService.findById(productId);
        productService.updateView(productId);
        rankService.updateViewOnMall(principalDetails.getUser().getId(), product.getMall().getId());
        userService.saveRecentProduct(principalDetails.getUser().getId(), productId);

        if(product.getType().equals(OUTER)) {
            ResponseOuterDto outerProduct = productService.outerProductDetail(productId);
            return new ResponseEntity<>(outerProduct, HttpStatus.OK);
        }

        if(product.getType().equals(TOP)) {
            ResponseTopDto topProduct = productService.topProductDetail(productId);
            return new ResponseEntity<>(topProduct, HttpStatus.OK);
        }

        if(product.getType().equals(DRESS)) {
            ResponseDressDto dressProduct = productService.dressProductDetail(productId);
            return new ResponseEntity<>(dressProduct, HttpStatus.OK);
        }

        if(product.getType().equals(BOTTOM)) {
            ResponseBottomDto bottomProduct = productService.bottomProductDetail(productId);
            return new ResponseEntity<>(bottomProduct, HttpStatus.OK);
        }

        return new ResponseEntity<>("정의된 타입 없음", HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "실시간 랭킹순 상품 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/products/rank/{gender}")
    public ResponseEntity<?> productOfTimeRank(@PathVariable("gender") String gender) {
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
