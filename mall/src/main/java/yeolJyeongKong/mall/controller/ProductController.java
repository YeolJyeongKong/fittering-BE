package yeolJyeongKong.mall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeolJyeongKong.mall.domain.dto.*;
import yeolJyeongKong.mall.domain.entity.Product;
import yeolJyeongKong.mall.service.ProductService;

import java.util.List;

@Tag(name = "상품", description = "상품 서비스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "카테고리별 상품 조회 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> productWithCategory(@PathVariable("categoryId") Long categoryId,
                                                 @RequestParam String gender,
                                                 Pageable pageable) {
        Page<ProductPreviewDto> products = productService.productWithCategory(categoryId, gender, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 내 카테고리별 상품 조회 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    @GetMapping("/malls/{mallId}/{categoryId}")
    public ResponseEntity<?> productWithCategoryOfMall(@PathVariable("mallId") Long mallId,
                                                       @PathVariable("categoryId") Long categoryId,
                                                       @RequestParam String gender,
                                                       Pageable pageable) {
        Page<ProductPreviewDto> products = productService.productWithCategoryOfMall(
                                                   mallId, categoryId, gender, pageable
                                           );
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "카테고리별 상품 개수 조회 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductCategoryDto.class))))
    @GetMapping("/category/count")
    public ResponseEntity<?> multipleProductCountWithCategory() {
        List<ProductCategoryDto> categoryWithProductCounts = productService.multipleProductCountWithCategory();
        return new ResponseEntity<>(categoryWithProductCounts, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 내 카테고리별 상품 개수 조회 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductCategoryDto.class))))
    @GetMapping("/malls/{mallId}/category/count")
    public ResponseEntity<?> productCountWithCategoryOfMall(@PathVariable("mallId") Long mallId) {
        List<ProductCategoryDto> categoryWithProductCounts = productService.productCountWithCategoryOfMall(mallId);
        return new ResponseEntity<>(categoryWithProductCounts, HttpStatus.OK);
    }

    /**
     * product.type
     * 0 : 상의(Top)
     * 1 : 하의(Bottom)
     * ...
     */
    @Operation(summary = "상품 상세 조회 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = {
            @Content(schema = @Schema(implementation = TopProductDto.class)),
            @Content(schema = @Schema(implementation = BottomProductDto.class))
    })
    @GetMapping("/products/{productId}")
    public ResponseEntity<?> productDetail(@PathVariable("productId") Long productId) {
        productService.updateView(productId);
        Product product = productService.findById(productId);

        if(product.getType().equals(0)) {
            TopProductDto topProduct = productService.topProductDetail(productId);
            return new ResponseEntity<>(topProduct, HttpStatus.OK);
        }

        if(product.getType().equals(1)) {
            BottomProductDto bottomProduct = productService.bottomProductDetail(productId);
            return new ResponseEntity<>(bottomProduct, HttpStatus.OK);
        }

        return new ResponseEntity<>("정의된 타입 없음", HttpStatus.BAD_REQUEST);
    }
}
