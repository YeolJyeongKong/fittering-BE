package yeolJyeongKong.mall.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeolJyeongKong.mall.domain.dto.BottomProductDto;
import yeolJyeongKong.mall.domain.dto.ProductCategoryDto;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.dto.TopProductDto;
import yeolJyeongKong.mall.domain.entity.Product;
import yeolJyeongKong.mall.service.ProductService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> productWithCategory(@PathVariable("categoryId") Long categoryId,
                                                 @RequestParam String gender,
                                                 Pageable pageable) {
        Page<ProductPreviewDto> products = productService.productWithCategory(categoryId, gender, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

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

    @GetMapping("/category/count")
    public ResponseEntity<?> multipleProductCountWithCategory() {
        List<ProductCategoryDto> categoryWithProductCounts = productService.multipleProductCountWithCategory();
        return new ResponseEntity<>(categoryWithProductCounts, HttpStatus.OK);
    }

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
    @GetMapping("/products/{productId}")
    public ResponseEntity<?> productDetail(@PathVariable("productId") Long productId) {
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
