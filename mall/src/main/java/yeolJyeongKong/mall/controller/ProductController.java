package yeolJyeongKong.mall.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.service.ProductService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> productWithCategory(@PathVariable("categoryId") String categoryName,
                                                 @RequestParam String gender,
                                                 Pageable pageable) {
        Page<ProductPreviewDto> products = productService.productWithCategory(categoryName, gender, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
