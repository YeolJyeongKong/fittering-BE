package yeolJyeongKong.mall.controller;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

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
