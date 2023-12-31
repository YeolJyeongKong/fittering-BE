package fittering.mall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fittering.mall.service.CategoryService;

@Slf4j
@Tag(name = "카테고리", description = "카테고리 서비스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 등록")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"카테고리 등록 완료\"")))
    @PostMapping("/categories/{categoryName}")
    public ResponseEntity<?> save(@PathVariable("categoryName") @NotEmpty String categoryName) {
        if (categoryName.length() >= 10) {
            return new ResponseEntity<>("카테고리 이름은 1 이상 10 이하여야 합니다.", HttpStatus.BAD_REQUEST);
        }
        categoryService.save(categoryName);
        return new ResponseEntity<>("카테고리 등록 완료", HttpStatus.OK);
    }

    @Operation(summary = "세부 카테고리 등록")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"서브 카테고리 등록 완료\"")))
    @PostMapping("/categories/sub/{categoryName}/{subCategoryName}")
    public ResponseEntity<?> saveSubCategory(@PathVariable("categoryName") @NotEmpty String categoryName,
                                             @PathVariable("subCategoryName") @NotEmpty String subCategoryName) {
        if (categoryName.length() >= 10) {
            return new ResponseEntity<>("카테고리 이름은 1 이상 10 이하여야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if (subCategoryName.length() >= 10) {
            return new ResponseEntity<>("서브 카테고리 이름은 1 이상 10 이하여야 합니다.", HttpStatus.BAD_REQUEST);
        }
        categoryService.saveSubCategory(categoryName, subCategoryName);
        return new ResponseEntity<>("서브 카테고리 등록 완료", HttpStatus.OK);
    }
}
