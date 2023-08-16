package fittering.mall.controller;

import fittering.mall.domain.dto.controller.request.RequestMeasurementDto;
import fittering.mall.domain.dto.controller.request.RequestSmartMeasurementDto;
import fittering.mall.domain.dto.controller.request.RequestUserDto;
import fittering.mall.domain.dto.controller.response.ResponseMeasurementDto;
import fittering.mall.domain.dto.controller.response.ResponseProductPreviewDto;
import fittering.mall.domain.dto.controller.response.ResponseUserDto;
import fittering.mall.domain.dto.service.MeasurementDto;
import fittering.mall.domain.mapper.MeasurementMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import fittering.mall.config.auth.PrincipalDetails;
import fittering.mall.domain.entity.Product;
import fittering.mall.service.FavoriteService;
import fittering.mall.service.ProductService;
import fittering.mall.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "유저", description = "유저 서비스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final ProductService productService;
    private final FavoriteService favoriteService;

    @Operation(summary = "마이페이지 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ResponseUserDto.class)))
    @GetMapping("/user/mypage")
    public ResponseEntity<?> edit(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        ResponseUserDto userInfo = userService.info(principalDetails.getUser().getId());
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    @Operation(summary = "마이페이지 수정")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = RequestUserDto.class)))
    @PostMapping("/user/mypage")
    public ResponseEntity<?> edit(@RequestBody RequestUserDto requestUserDto,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        userService.infoUpdate(requestUserDto, principalDetails.getUser().getId());
        return new ResponseEntity<>(requestUserDto, HttpStatus.OK);
    }

    @Operation(summary = "비밀번호 수정")
    @ApiResponses (value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"비밀번호 변경 성공\""))),
            @ApiResponse(responseCode = "401", description = "FAIL", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"현재 비밀번호 인증 실패\"")))
    })
    @PostMapping("/user/check/password/{password}/{newPassword}")
    public ResponseEntity<?> checkPassword(@PathVariable("password") String password,
                                           @PathVariable("newPassword") String newPassword,
                                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long userId = principalDetails.getUser().getId();
        if(userService.passwordCheck(userId, password)) {
            userService.setPassword(userId, newPassword);
            return new ResponseEntity<>("비밀번호 변경 성공", HttpStatus.OK);
        }
        return new ResponseEntity<>("현재 비밀번호 인증 실패", HttpStatus.UNAUTHORIZED);
    }

    @Operation(summary = "체형 정보 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ResponseMeasurementDto.class)))
    @GetMapping("/user/mysize")
    public ResponseEntity<?> measurementEdit(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        ResponseMeasurementDto measurement = userService.measurementInfo(principalDetails.getUser().getId());
        return new ResponseEntity<>(measurement, HttpStatus.OK);
    }

    @Operation(summary = "체형 정보 수정")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = MeasurementDto.class)))
    @PostMapping("/user/mysize")
    public ResponseEntity<?> measurementEdit(@RequestBody RequestMeasurementDto requestMeasurementDto,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MeasurementDto measurementDto = MeasurementMapper.INSTANCE.toMeasurementDto(requestMeasurementDto);
        userService.measurementUpdate(measurementDto, principalDetails.getUser().getId());
        return new ResponseEntity<>(measurementDto, HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 상품 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/user/favorite_goods")
    public ResponseEntity<?> favoriteProduct(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                             Pageable pageable) {
        Page<ResponseProductPreviewDto> products = favoriteService.userFavoriteProduct(
                                                principalDetails.getUser().getId(), pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 상품 조회 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/user/favorite_goods/preview")
    public ResponseEntity<?> favoriteProductPreview(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<ResponseProductPreviewDto> products = favoriteService.userFavoriteProductPreview(principalDetails.getUser().getId());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 상품 등록")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"유저 즐겨찾기 상품 등록 완료\"")))
    @PostMapping("/user/favorite_goods/{productId}")
    public ResponseEntity<?> saveFavoriteProduct(@PathVariable("productId") Long productId,
                                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
        favoriteService.saveFavoriteProduct(principalDetails.getUser().getId(), productId);
        return new ResponseEntity<>("유저 즐겨찾기 상품 등록 완료", HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 상품 삭제")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"유저 즐겨찾기 상품 삭제 완료\"")))
    @DeleteMapping("/user/favorite_goods/{productId}")
    public ResponseEntity<?> deleteFavoriteProduct(@PathVariable("productId") Long productId,
                                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        favoriteService.deleteFavoriteProduct(principalDetails.getUser().getId(), productId);
        return new ResponseEntity<>("유저 즐겨찾기 상품 삭제 완료", HttpStatus.OK);
    }

    @Operation(summary = "유저 최근 본 상품 조회 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/user/recent/preview")
    public ResponseEntity<?> recentProductPreview(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<ResponseProductPreviewDto> products = userService.recentProductPreview(principalDetails.getUser().getId());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "유저 최근 본 상품 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/user/recent")
    public ResponseEntity<?> recentProduct(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                           Pageable pageable) {
        Page<ResponseProductPreviewDto> products = userService.recentProduct(principalDetails.getUser().getId(), pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "체형 스마트 분석")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseMeasurementDto.class))))
    @PostMapping("/user/recommendation/measurement")
    public ResponseEntity<?> recommendMeasurement(@RequestBody RequestSmartMeasurementDto requestSmartMeasurementDto,
                                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        //request: smartMeasurementDto
        //실루엣 이미지(정면/측면), 키, 몸무게, 성별
        //TODO: 체형 측정 API에서 체형 측정 결과를 받아오는 로직 필요
        ResponseMeasurementDto responseMeasurementDto = null; //response
        return new ResponseEntity<>(responseMeasurementDto, HttpStatus.OK);
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
    @Operation(summary = "추천 상품 조회 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/user/recommendation/preview")
    public ResponseEntity<?> recommendProductPreview(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<Long> productIds = findRecommendedProductIds(principalDetails.getUser().getId());
        List<ResponseProductPreviewDto> products = productService.recommendProduct(productIds, true);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "추천 상품 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/user/recommendation")
    public ResponseEntity<?> recommendProduct(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<Long> productIds = findRecommendedProductIds(principalDetails.getUser().getId());
        List<ResponseProductPreviewDto> products = productService.recommendProduct(productIds, false);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Cacheable(value = "recommendation", key = "#userId")
    public List<Long> findRecommendedProductIds(Long userId) {
        List<Long> productIds = new ArrayList<>();
        List<Product> recommendedProducts = productService.productWithRecentRecommendation(userId);

        if(!recommendedProducts.isEmpty()) {
            getRecommendedProductIds(productIds, recommendedProducts);
            return productIds;
        }

        List<ResponseProductPreviewDto> recentProducts = userService.recentProductPreview(userId); //request
        /**
         * TODO: 추천 상품 API에서 데이터를 받아오는 로직 추가 필요
         * - 해당 API에서 가져오는 상품 개수가 10개라고 가정
         * - 각 recentRecommendation 객체는 userId 1개, productId 1개를 가지므로
         *   10개의 recentRecommendation 객체 생성
         */
        productIds = new ArrayList<>(); //response

        productIds.forEach(productId -> {
            productService.saveRecentRecommendation(userId, productId);
        });

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
    @Operation(summary = "비슷한 체형 고객 pick 조회 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/user/recommendation/pick/preview")
    public ResponseEntity<?> recommendProductPreviewOnPick(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<Long> productIds = findRecommendedProductIdsOnPick(principalDetails.getUser().getId());
        List<ResponseProductPreviewDto> products = productService.recommendProduct(productIds, true);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "비슷한 체형 고객 pick 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/user/recommendation/pick")
    public ResponseEntity<?> recommendProductOnPick(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<Long> productIds = findRecommendedProductIdsOnPick(principalDetails.getUser().getId());
        List<ResponseProductPreviewDto> products = productService.recommendProduct(productIds, false);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Cacheable(value = "recommendationOnPick", key = "#userId")
    public List<Long> findRecommendedProductIdsOnPick(Long userId) {
        List<Long> productIds = new ArrayList<>();
        List<Product> recommendedProducts = productService.productWithUserRecommendation(userId);

        if(!recommendedProducts.isEmpty()) {
            getRecommendedProductIds(productIds, recommendedProducts);
            return productIds;
        }

        ResponseMeasurementDto measurement = userService.measurementInfo(userId); //request
        /**
         * TODO: 비슷한 체형 고객 pick API에서 데이터를 받아오는 로직 추가 필요
         * - 해당 API에서 가져오는 상품 개수가 10개라고 가정
         * - 각 userRecommenation 객체는 userId 1개, productId 1개를 가지므로
         *   10개의 userRecommendation 객체 생성
         */
        productIds = new ArrayList<>(); //response

        productIds.forEach(productId -> {
            productService.saveUserRecommendation(userId, productId);
        });

        return productIds;
    }

    @Operation(summary = "유저 즐겨찾기 쇼핑몰 등록")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"즐겨찾기 등록 완료\"")))
    @PostMapping("/favorite/{mallId}")
    public ResponseEntity<?> setFavoriteMall(@PathVariable("mallId") Long mallId,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        favoriteService.saveFavoriteMall(principalDetails.getUser().getId(), mallId);
        return new ResponseEntity<>("즐겨찾기 등록 완료", HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 쇼핑몰 삭제")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"즐겨찾기 삭제 완료\"")))
    @DeleteMapping("/favorite/{mallId}")
    public ResponseEntity<?> deleteFavoriteMall(@PathVariable("mallId") Long mallId,
                                                @AuthenticationPrincipal PrincipalDetails principalDetails) {
        favoriteService.deleteFavoriteMall(principalDetails.getUser().getId(), mallId);
        return new ResponseEntity<>("즐겨찾기 삭제 완료", HttpStatus.OK);
    }

    private static void getRecommendedProductIds(List<Long> productIds, List<Product> recommendedProducts) {
        for (Product recommendedProduct : recommendedProducts) {
            productIds.add(recommendedProduct.getId());
        }
    }
}
