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
import yeolJyeongKong.mall.domain.entity.Product;
import yeolJyeongKong.mall.domain.entity.RecentRecommendation;
import yeolJyeongKong.mall.domain.entity.UserRecommendation;
import yeolJyeongKong.mall.service.FavoriteService;
import yeolJyeongKong.mall.service.ProductService;
import yeolJyeongKong.mall.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "유저", description = "유저 서비스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final ProductService productService;
    private final FavoriteService favoriteService;

    @Operation(summary = "마이페이지 조회 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = UserDto.class)))
    @GetMapping("/user/edit")
    public ResponseEntity<?> edit(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        UserDto userDto = userService.info(principalDetails.getUser().getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Operation(summary = "마이페이지 수정 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = UserDto.class)))
    @PostMapping("/user/edit")
    public ResponseEntity<?> edit(@ModelAttribute UserDto userDto,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        userService.infoUpdate(userDto, principalDetails.getUser().getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Operation(summary = "체형 정보 조회 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = MeasurementDto.class)))
    @GetMapping("/user/mysize/edit")
    public ResponseEntity<?> measurementEdit(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        MeasurementDto measurementDto = userService.measurementInfo(principalDetails.getUser().getId());
        return new ResponseEntity<>(measurementDto, HttpStatus.OK);
    }

    @Operation(summary = "체형 정보 수정 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = MeasurementDto.class)))
    @PostMapping("/user/mysize/edit")
    public ResponseEntity<?> measurementEdit(@ModelAttribute MeasurementDto measurementDto,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        userService.measurementUpdate(measurementDto, principalDetails.getUser().getId());
        return new ResponseEntity<>(measurementDto, HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 상품 조회 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    @GetMapping("/user/favorite_goods")
    public ResponseEntity<?> favoriteProduct(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                             Pageable pageable) {
        Page<ProductPreviewDto> products = productService.productWithUserFavorite(
                                                principalDetails.getUser().getId(), pageable
                                           );
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "유저 최근 본 상품 조회 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    @GetMapping("/user/recent")
    public ResponseEntity<?> recentProduct(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<ProductPreviewDto> products = userService.recentProduct(principalDetails.getUser().getId());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * <추천 상품 기능 설명>
     * Request  : 해당 유저의 모든 최근 본 상품 정보들
     * Response : List<productId>
     * API 주소 : /api/recommendation
     *
     * DB에 추천 상품 정보가 없을 때만 추천 상품 API를 통해 product IDs를 얻어옴
     * 이외에는 DB에 저장된 추천 상품 정보를 가져옴
     * > 일정 주기를 두고 "초기화" == ML 학습 주기를 기준
     *   으로 하기로 했는데 재학습을 하지 않는다는 말이 있어서 일단 보류
     */
    @Operation(summary = "추천 상품 조회 메소드 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    @GetMapping("/user/recommendation/preview")
    public ResponseEntity<?> recommendProductPreview(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<Long> productIds = findRecommendedProductIds(principalDetails.getUser().getId());
        List<ProductPreviewDto> products = productService.recommendProduct(productIds, true);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "추천 상품 조회 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    @GetMapping("/user/recommendation")
    public ResponseEntity<?> recommendProduct(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<Long> productIds = findRecommendedProductIds(principalDetails.getUser().getId());
        List<ProductPreviewDto> products = productService.recommendProduct(productIds, false);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    private List<Long> findRecommendedProductIds(Long userId) {
        List<Long> productIds = new ArrayList<>();
        List<Product> recommendedProducts = productService.productWithRecentRecommendation(userId);

        if(recommendedProducts.size() > 0) {
            for (Product recommendedProduct : recommendedProducts) {
                productIds.add(recommendedProduct.getId());
            }
            return productIds;
        }

        RecentRecommendation recentRecommendation = productService.saveRecentRecommendation(userId);

        List<ProductPreviewDto> recentProducts = userService.recentProduct(userId); //request
        //TODO: API Gateway에 등록된 추천 상품 API에서 데이터를 받아오는 로직 추가 필요
        productIds = new ArrayList<>(); //response

        productService.updateRecentRecommendation(recentRecommendation, productIds);

        return productIds;
    }

    /**
     * <비슷한 체형 고객 pick 기능 설명>
     * Request  : 해당 유저의 체형 정보(Measurement)
     * Response : List<productId>
     * API 주소 : /api/recommendation/pick
     *
     * DB에 추천 상품 정보가 없을 때만 추천 상품 API를 통해 product IDs를 얻어옴
     * 이외에는 DB에 저장된 추천 상품 정보를 가져옴
     * > 일정 주기를 두고 "초기화" == ML 학습 주기를 기준
     *   으로 하기로 했는데 재학습을 하지 않는다는 말이 있어서 일단 보류
     */
    @Operation(summary = "비슷한 체형 고객 pick 조회 메소드 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    @GetMapping("/user/recommendation/pick/preview")
    public ResponseEntity<?> recommendProductPreviewOnPick(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<Long> productIds = findRecommendedProductIdsOnPick(principalDetails.getUser().getId());
        List<ProductPreviewDto> products = productService.recommendProduct(productIds, true);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "비슷한 체형 고객 pick 조회 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    @GetMapping("/user/recommendation/pick")
    public ResponseEntity<?> recommendProductOnPick(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<Long> productIds = findRecommendedProductIdsOnPick(principalDetails.getUser().getId());
        List<ProductPreviewDto> products = productService.recommendProduct(productIds, false);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    private List<Long> findRecommendedProductIdsOnPick(Long userId) {
        List<Long> productIds = new ArrayList<>();
        List<Product> recommendedProducts = productService.productWithUserRecommendation(userId);

        if(recommendedProducts.size() > 0) {
            for (Product recommendedProduct : recommendedProducts) {
                productIds.add(recommendedProduct.getId());
            }
            return productIds;
        }

        UserRecommendation userRecommendation = productService.saveUserRecommendation(userId);

        MeasurementDto measurement = userService.measurementInfo(userId); //request
        //TODO: API Gateway에 등록된 비슷한 체형 고객 pick API에서 데이터를 받아오는 로직 추가 필요
        productIds = new ArrayList<>(); //response

        productService.updateUserRecommendation(userRecommendation, productIds);

        return productIds;
    }

    @Operation(summary = "유저 즐겨찾기 쇼핑몰 등록 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"즐겨찾기 등록 완료\"")))
    @PostMapping("/favorite/{userId}/{mallId}")
    public ResponseEntity<?> setFavoriteMall(@PathVariable("userId") Long userId,
                                             @PathVariable("mallId") Long mallId) {
        favoriteService.setFavoriteMall(userId, mallId);
        return new ResponseEntity<>("즐겨찾기 등록 완료", HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 쇼핑몰 삭제 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"즐겨찾기 삭제 완료\"")))
    @DeleteMapping("/favorite/{userId}/{mallId}")
    public ResponseEntity<?> deleteFavoriteMall(@PathVariable("userId") Long userId,
                                                @PathVariable("mallId") Long mallId) {
        favoriteService.deleteFavoriteMall(userId, mallId);
        return new ResponseEntity<>("즐겨찾기 삭제 완료", HttpStatus.OK);
    }

    @Operation(summary = "최근 본 상품 업데이트 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"최근 본 상품 업데이트 완료\"")))
    @PostMapping("/user/recent/{userId}/{productId}")
    public ResponseEntity<?> updateRecentProduct(@PathVariable("userId") Long userId,
                                                 @PathVariable("productId") Long productId) {
        userService.saveRecentProduct(userId, productId);
        return new ResponseEntity<>("최근 본 상품 업데이트 완료", HttpStatus.OK);
    }
}
