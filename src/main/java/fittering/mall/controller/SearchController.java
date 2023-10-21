package fittering.mall.controller;

import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.controller.dto.response.ResponseRelatedSearchDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import fittering.mall.service.SearchService;


@Tag(name = "검색", description = "검색 서비스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "검색")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ResponseProductPreviewDto.class)))
    @GetMapping("/auth/search/{keyword}/{gender}/{filterId}")
    public ResponseEntity<?> search(@PathVariable("keyword") @NotEmpty String keyword,
                                    @PathVariable("gender") @NotEmpty String gender,
                                    @PathVariable("filterId") @NotEmpty Long filterId,
                                    Pageable pageable) {
        Page<ResponseProductPreviewDto> result = searchService.products(keyword, gender, filterId, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "연관검색어")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ResponseRelatedSearchDto.class)))
    @GetMapping("/search/{keyword}")
    public ResponseEntity<?> relatedSearch(@PathVariable("keyword") @NotEmpty String keyword) {
        ResponseRelatedSearchDto result = ResponseRelatedSearchDto.builder()
                .products(searchService.relatedSearchProducts(keyword))
                .malls(searchService.relatedSearchMalls(keyword))
                .build();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
