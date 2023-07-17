package yeolJyeongKong.mall.controller;

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
import yeolJyeongKong.mall.config.PrincipalDetails;
import yeolJyeongKong.mall.domain.dto.*;
import yeolJyeongKong.mall.domain.entity.Category;
import yeolJyeongKong.mall.domain.entity.Mall;
import yeolJyeongKong.mall.domain.entity.Product;
import yeolJyeongKong.mall.domain.entity.Size;
import yeolJyeongKong.mall.service.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "상품", description = "상품 서비스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final MallService mallService;
    private final UserService userService;
    private final SizeService sizeService;
    private final RankService rankService;

    @Operation(summary = "상품 등록 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"상품 등록 완료\"")))
    @PostMapping("/product/add")
    public ResponseEntity<?> save(@ModelAttribute ProductDetailDto productDto) {
        Category category = categoryService.findByName(productDto.getCategoryName());
        Mall mall = mallService.findByName(productDto.getMallName());
        Product product = new Product(category, mall);
        List<Size> sizes = new ArrayList<>();

        if(productDto.getType() == 0) {
            for(TopDto topDto : productDto.getTopSizes()) {
                Size size = sizeService.saveTop(topDto);
                size.setProduct(product);
                sizes.add(size);
            }
        } else {
            for(BottomDto bottomDto : productDto.getBottomSizes()) {
                Size size = sizeService.saveBottom(bottomDto);
                size.setProduct(product);
                sizes.add(size);
            }
        }

        product.setSizes(sizes);
        productService.save(product);
        return new ResponseEntity<>("상품 등록 완료", HttpStatus.OK);
    }

    @Operation(summary = "카테고리별 상품 조회 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    @GetMapping("/category/{categoryId}/{gender}/{filterId}")
    public ResponseEntity<?> productWithCategory(@PathVariable("categoryId") Long categoryId,
                                                 @PathVariable("gender") String gender,
                                                 @PathVariable("filterId") Long filterId,
                                                 Pageable pageable) {
        Page<ProductPreviewDto> products = productService.productWithCategory(categoryId, gender, filterId, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "카테고리별 상품 개수 조회 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductCategoryDto.class))))
    @GetMapping("/category/count")
    public ResponseEntity<?> multipleProductCountWithCategory() {
        List<ProductCategoryDto> categoryWithProductCounts = productService.multipleProductCountWithCategory();
        return new ResponseEntity<>(categoryWithProductCounts, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 내 카테고리별 상품 조회 메소드")
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
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> productDetail(@PathVariable("productId") Long productId,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        productService.updateView(productId); //상품 조회수
        rankService.updateView(principalDetails.getUser().getId(), productId); //유저가 조회한 상품의 쇼핑몰 조회수
        userService.saveRecentProduct(principalDetails.getUser().getId(), productId); //최근 본 상품 업데이트
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
