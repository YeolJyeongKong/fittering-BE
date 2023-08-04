package fittering.mall.controller;

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
import fittering.mall.domain.dto.*;
import fittering.mall.domain.entity.*;
import fittering.mall.service.*;

import java.util.ArrayList;
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
    public ResponseEntity<?> save(@RequestBody ProductDetailDto productDto) {
        Category category = categoryService.findByName(productDto.getCategoryName());
        SubCategory subCategory = categoryService.findByNameOfSubCategory(productDto.getSubCategoryName());
        Mall mall = mallService.findByName(productDto.getMallName());
        Product product = productService.save(Product.builder()
                                                .price(productDto.getPrice())
                                                .name(productDto.getName())
                                                .gender(productDto.getGender())
                                                .type(productDto.getType())
                                                .image(productDto.getImage())
                                                .view(0)
                                                .timeView(0)
                                                .category(category)
                                                .subCategory(subCategory)
                                                .mall(mall)
                                                .build());
        productService.saveDescriptionImages(productDto.getDescriptionImages(), product);
        List<Size> sizes = new ArrayList<>();

        if (productDto.getType().equals(OUTER)) {
            getSizesOfOuter(productDto, product, sizes);
            return new ResponseEntity<>("상품 등록 완료", HttpStatus.OK);
        }

        if (productDto.getType().equals(TOP)) {
            getSizesOfTop(productDto, product, sizes);
            return new ResponseEntity<>("상품 등록 완료", HttpStatus.OK);
        }

        if(productDto.getType().equals(DRESS)) {
            getSizesOfDress(productDto, product, sizes);
            return new ResponseEntity<>("상품 등록 완료", HttpStatus.OK);
        }

        getSizesOfBottom(productDto, product, sizes);
        return new ResponseEntity<>("상품 등록 완료", HttpStatus.OK);
    }

    @Operation(summary = "카테고리별 상품 조회 (대분류)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    @GetMapping("/category/{categoryId}/{gender}/{filterId}")
    public ResponseEntity<?> productWithCategory(@PathVariable("categoryId") Long categoryId,
                                                 @PathVariable("gender") String gender,
                                                 @PathVariable("filterId") Long filterId,
                                                 Pageable pageable) {
        Page<ProductPreviewDto> products = productService.productWithCategory(categoryId, gender, filterId, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "카테고리별 상품 조회 (소분류)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    @GetMapping("/category/sub/{subCategoryId}/{gender}/{filterId}")
    public ResponseEntity<?> productWithSubCategory(@PathVariable("subCategoryId") Long subCategoryId,
                                                    @PathVariable("gender") String gender,
                                                    @PathVariable("filterId") Long filterId,
                                                    Pageable pageable) {
        Page<ProductPreviewDto> products = productService.productWithSubCategory(subCategoryId, gender, filterId, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "카테고리별 상품 개수 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductCategoryDto.class))))
    @GetMapping("/category/count")
    public ResponseEntity<?> multipleProductCountWithCategory() {
        List<ProductCategoryDto> categoryWithProductCounts = productService.multipleProductCountWithCategory();
        return new ResponseEntity<>(categoryWithProductCounts, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 카테고리별 상품 조회 (대분류)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    @GetMapping("/malls/{mallId}/{categoryId}/{gender}/{filterId}")
    public ResponseEntity<?> productWithCategoryOfMall(@PathVariable("mallId") Long mallId,
                                                       @PathVariable("categoryId") Long categoryId,
                                                       @PathVariable("gender") String gender,
                                                       @PathVariable("filterId") Long filterId,
                                                       Pageable pageable) {
        Page<ProductPreviewDto> products = productService.productWithCategoryOfMall(
                mallId, categoryId, gender, filterId, pageable
        );
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 카테고리별 상품 조회 (소분류)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    @GetMapping("/malls/{mallId}/{subCategoryId}/{gender}/{filterId}")
    public ResponseEntity<?> productWithSubCategoryOfMall(@PathVariable("mallId") Long mallId,
                                                          @PathVariable("subCategoryId") Long subCategoryId,
                                                          @PathVariable("gender") String gender,
                                                          @PathVariable("filterId") Long filterId,
                                                          Pageable pageable) {
        Page<ProductPreviewDto> products = productService.productWithSubCategoryOfMall(
                mallId, subCategoryId, gender, filterId, pageable
        );
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 내 카테고리별 상품 개수 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductCategoryDto.class))))
    @GetMapping("/malls/{mallId}/category/count")
    public ResponseEntity<?> productCountWithCategoryOfMall(@PathVariable("mallId") Long mallId) {
        List<ProductCategoryDto> categoryWithProductCounts = productService.productCountWithCategoryOfMall(mallId);
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
            @Content(schema = @Schema(implementation = TopProductDto.class)),
            @Content(schema = @Schema(implementation = BottomProductDto.class))
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> productDetail(@PathVariable("productId") Long productId,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        productService.updateView(productId); //상품 조회수
        rankService.updateViewOnProduct(principalDetails.getUser().getId(), productId); //유저가 조회한 상품의 쇼핑몰 조회수
        userService.saveRecentProduct(principalDetails.getUser().getId(), productId); //최근 본 상품 업데이트
        Product product = productService.findById(productId);

        if(product.getType().equals(OUTER)) {
            OuterProductDto outerProduct = productService.outerProductDetail(productId);
            return new ResponseEntity<>(outerProduct, HttpStatus.OK);
        }

        if(product.getType().equals(TOP)) {
            TopProductDto topProduct = productService.topProductDetail(productId);
            return new ResponseEntity<>(topProduct, HttpStatus.OK);
        }

        if(product.getType().equals(DRESS)) {
            DressProductDto dressProduct = productService.dressProductDetail(productId);
            return new ResponseEntity<>(dressProduct, HttpStatus.OK);
        }

        if(product.getType().equals(BOTTOM)) {
            BottomProductDto bottomProduct = productService.bottomProductDetail(productId);
            return new ResponseEntity<>(bottomProduct, HttpStatus.OK);
        }

        return new ResponseEntity<>("정의된 타입 없음", HttpStatus.BAD_REQUEST);
    }

    private void getSizesOfOuter(ProductDetailDto productDto, Product product, List<Size> sizes) {
        productDto.getOuterSizes().forEach(outerDto -> {
            Size size = sizeService.saveOuter(outerDto, product);
            sizes.add(size);
        });
    }

    private void getSizesOfTop(ProductDetailDto productDto, Product product, List<Size> sizes) {
        productDto.getTopSizes().forEach(topDto -> {
            Size size = sizeService.saveTop(topDto, product);
            sizes.add(size);
        });
    }

    private void getSizesOfDress(ProductDetailDto productDto, Product product, List<Size> sizes) {
        productDto.getDressSizes().forEach(dressDto -> {
            Size size = sizeService.saveDress(dressDto, product);
            sizes.add(size);
        });
    }

    private void getSizesOfBottom(ProductDetailDto productDto, Product product, List<Size> sizes) {
        productDto.getBottomSizes().forEach(bottomDto -> {
            Size size = sizeService.saveBottom(bottomDto, product);
            sizes.add(size);
        });
    }
}
