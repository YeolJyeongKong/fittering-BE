package yeolJyeongKong.mall.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeolJyeongKong.mall.domain.dto.ProductCategoryDto;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
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
}
