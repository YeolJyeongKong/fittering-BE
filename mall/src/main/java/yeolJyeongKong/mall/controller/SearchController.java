package yeolJyeongKong.mall.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.dto.SearchDto;
import yeolJyeongKong.mall.service.SearchService;

@Tag(name = "검색", description = "검색 서비스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "검색 메소드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ProductPreviewDto.class))),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ProductPreviewDto.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<?> search(SearchDto searchDto, Pageable pageable) {
        Page<ProductPreviewDto> result = searchService.products(
                                                searchDto.getKeyword(),
                                                searchDto.getGender(),
                                                pageable
                                         );
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
