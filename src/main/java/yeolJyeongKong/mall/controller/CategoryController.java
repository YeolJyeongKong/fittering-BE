package yeolJyeongKong.mall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeolJyeongKong.mall.service.CategoryService;

@Tag(name = "카테고리", description = "카테고리 서비스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 등록 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"카테고리 등록 완료\"")))
    @PostMapping("/category/add/{categoryName}")
    public ResponseEntity<?> save(@PathVariable("categoryName") String categoryName) {
        categoryService.save(categoryName);
        return new ResponseEntity<>("카테고리 등록 완료", HttpStatus.OK);
    }
}
