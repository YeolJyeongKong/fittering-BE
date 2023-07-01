package yeolJyeongKong.mall.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yeolJyeongKong.mall.domain.dto.MeasurementDto;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.dto.RecommendProductsDto;
import yeolJyeongKong.mall.domain.dto.UserDto;
import yeolJyeongKong.mall.service.ProductService;
import yeolJyeongKong.mall.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/user/edit")
    public ResponseEntity<?> edit(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        UserDto userDto = userService.info(principalDetails.getUser().getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/user/edit")
    public ResponseEntity<?> edit(UserDto userDto,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        userService.infoUpdate(userDto, principalDetails.getUser().getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("/user/mysize/edit")
    public ResponseEntity<?> measurementEdit(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        MeasurementDto measurementDto = userService.measurementInfo(principalDetails.getUser().getId());
        return new ResponseEntity<>(measurementDto, HttpStatus.OK);
    }

    @PostMapping("/user/mysize/edit")
    public ResponseEntity<?> measurementEdit(MeasurementDto measurementDto,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        userService.measurementUpdate(measurementDto, principalDetails.getUser().getId());
        return new ResponseEntity<>(measurementDto, HttpStatus.OK);
    }

    @GetMapping("/user/favorite_goods")
    public ResponseEntity<?> favoriteProduct(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 Pageable pageable) {
        Page<ProductPreviewDto> products = productService.productWithUserFavorite(
                                                principalDetails.getUser().getId(), pageable
                                           );
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/user/recent")
    public ResponseEntity<?> recentProduct(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<ProductPreviewDto> products = userService.recentProduct(principalDetails.getUser().getId());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * ML server에서 상품 인덱스 받아오는 방식에 따라 수정 필요 (미완성)
     * - Restful API : productId List를 @RequestParam이 아닌 다른 API URL로부터 받아오는 로직 추가 필요
     * - kafka : 전달 방식 이해 및 로직 수정 필요
     *
     * type에 따라 전달되는 내용이 다르도록 만들어야 함
     * 처리하는 로직이 같아서 아직 구분해서 구현하지 않았음 (추후 수정)
     * - 1 : 추천 상품
     * - 2 : 비슷한 체형 고객 pick
     */
    @GetMapping("/user/recommendation/preview")
    public ResponseEntity<?> recommendProductPreview(@RequestParam RecommendProductsDto recommendProductsDto) {
        List<Long> productIds = recommendProductsDto.getProductIds();
        List<ProductPreviewDto> products = productService.recommendProduct(productIds, true);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * ML server에서 상품 인덱스 받아오는 방식에 따라 수정 필요 (미완성)
     * - Restful API : productId List를 @RequestParam이 아닌 다른 API URL로부터 받아오는 로직 추가 필요
     * - kafka : 전달 방식 이해 및 로직 수정 필요
     *
     * type에 따라 전달되는 내용이 다르도록 만들어야 함
     * 처리하는 로직이 같아서 아직 구분해서 구현하지 않았음 (추후 수정)
     * - 1 : 추천 상품
     * - 2 : 비슷한 체형 고객 pick
     */
    @GetMapping("/user/recommendation")
    public ResponseEntity<?> recommendProduct(@RequestParam RecommendProductsDto recommendProductsDto) {
        List<Long> productIds = recommendProductsDto.getProductIds();
        List<ProductPreviewDto> products = productService.recommendProduct(productIds, false);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
