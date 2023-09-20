package fittering.mall.controller;

import fittering.mall.domain.dto.controller.request.*;
import fittering.mall.domain.dto.controller.response.*;
import fittering.mall.domain.dto.service.MeasurementDto;
import fittering.mall.domain.mapper.MeasurementMapper;
import fittering.mall.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import fittering.mall.config.auth.PrincipalDetails;
import fittering.mall.domain.entity.Product;
import fittering.mall.service.FavoriteService;
import fittering.mall.service.ProductService;
import fittering.mall.service.UserService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static fittering.mall.controller.ControllerUtils.getValidationErrorResponse;

@Tag(name = "유저", description = "유저 서비스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserController {

    private static final String FRONT_SILHOUETTE_KEY_SUFFIX = ":front";
    private static final String SIDE_SILHOUETTE_KEY_SUFFIX = ":side";

    @Value("${ML.API.MEASURE}")
    private String ML_MEASURE_API;
    @Value("${ML.API.RECOMMEND.PRODUCT}")
    private String ML_PRODUCT_RECOMMENDATION_API;
    @Value("${ML.API.RECOMMEND.USER}")
    private String ML_PRODUCT_RECOMMENDATION_WITH_MEASUREMENT_API;
    @Value("${ML.API.SILHOUETTE}")
    private String ML_SILHOUETTE_API;
    @Value("${cloud.aws.cloudfront.silhouette}")
    private String CLOUDFRONT_SILHOUETTE_URL;

    private final UserService userService;
    private final ProductService productService;
    private final FavoriteService favoriteService;
    private final S3Service s3Service;
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Operation(summary = "마이페이지 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ResponseUserDto.class)))
    @GetMapping("/users/mypage")
    public ResponseEntity<?> edit(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        ResponseUserDto userInfo = userService.info(principalDetails.getUser().getId());
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    @Operation(summary = "마이페이지 수정")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = RequestUserDto.class)))
    @PostMapping("/users/mypage")
    public ResponseEntity<?> edit(@RequestBody @Valid RequestUserDto requestUserDto,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getValidationErrorResponse(bindingResult);
        }
        userService.infoUpdate(requestUserDto, principalDetails.getUser().getId());
        return new ResponseEntity<>(requestUserDto, HttpStatus.OK);
    }

    @Operation(summary = "비밀번호 수정")
    @ApiResponses (value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"비밀번호 변경 성공\""))),
            @ApiResponse(responseCode = "401", description = "FAIL", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"현재 비밀번호 인증 실패\"")))
    })
    @PostMapping("/users/password/check/{password}/{newPassword}")
    public ResponseEntity<?> checkPassword(@PathVariable("password") @NotEmpty String password,
                                           @PathVariable("newPassword") @NotEmpty String newPassword,
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
    @GetMapping("/users/mysize")
    public ResponseEntity<?> measurementEdit(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        ResponseMeasurementDto measurement = userService.measurementInfo(principalDetails.getUser().getId());
        return new ResponseEntity<>(measurement, HttpStatus.OK);
    }

    @Operation(summary = "체형 정보 수정")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = MeasurementDto.class)))
    @PostMapping("/users/mysize")
    public ResponseEntity<?> measurementEdit(@RequestBody @Valid RequestMeasurementDto requestMeasurementDto,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getValidationErrorResponse(bindingResult);
        }
        MeasurementDto measurementDto = MeasurementMapper.INSTANCE.toMeasurementDto(requestMeasurementDto);
        userService.measurementUpdate(measurementDto, principalDetails.getUser().getId());
        return new ResponseEntity<>(measurementDto, HttpStatus.OK);
    }

    @Operation(summary = "체형 실루엣 이미지 제공")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ResponseSilhouetteUrlDto.class)))
    @GetMapping("/users/mysize/silhouette")
    public ResponseEntity<?> silhouetteFromBody(@RequestParam("bodyFile") MultipartFile bodyFile,
                                                @RequestParam("type") String type,
                                                @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {
        Long userId = principalDetails.getUser().getId();
        boolean isFront = type.equals("FRONT");
        String savedFileName = s3Service.saveObject(bodyFile, userId, "body");

        saveSilhouetteFileNameInCache(userId, isFront, savedFileName);

        URI uri = UriComponentsBuilder.fromUriString(ML_SILHOUETTE_API)
                .build()
                .toUri();
        RequestSilhouetteApiDto requestSilhouetteApiDto = RequestSilhouetteApiDto.builder()
                .image_fname(savedFileName)
                .build();
        ResponseSilhouetteApiDto responseSilhouetteApiDto = restTemplate.postForObject(uri, requestSilhouetteApiDto, ResponseSilhouetteApiDto.class);

        ResponseSilhouetteUrlDto responseSilhouetteUrlDto = ResponseSilhouetteUrlDto.builder()
                .url(CLOUDFRONT_SILHOUETTE_URL + responseSilhouetteApiDto.getImage_fname())
                .build();
        return new ResponseEntity<>(responseSilhouetteUrlDto, HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 상품 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/users/favorite_goods")
    public ResponseEntity<?> favoriteProduct(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                             Pageable pageable) {
        Page<ResponseProductPreviewDto> products = favoriteService.userFavoriteProduct(
                                                principalDetails.getUser().getId(), pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 상품 조회 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/users/favorite_goods/preview")
    public ResponseEntity<?> favoriteProductPreview(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<ResponseProductPreviewDto> products = favoriteService.userFavoriteProductPreview(principalDetails.getUser().getId());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 상품 등록")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"유저 즐겨찾기 상품 등록 완료\"")))
    @PostMapping("/users/favorite_goods/{productId}")
    public ResponseEntity<?> saveFavoriteProduct(@PathVariable("productId") @NotEmpty Long productId,
                                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
        favoriteService.saveFavoriteProduct(principalDetails.getUser().getId(), productId);
        return new ResponseEntity<>("유저 즐겨찾기 상품 등록 완료", HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 상품 삭제")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"유저 즐겨찾기 상품 삭제 완료\"")))
    @DeleteMapping("/users/favorite_goods/{productId}")
    public ResponseEntity<?> deleteFavoriteProduct(@PathVariable("productId") @NotEmpty Long productId,
                                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        favoriteService.deleteFavoriteProduct(principalDetails.getUser().getId(), productId);
        return new ResponseEntity<>("유저 즐겨찾기 상품 삭제 완료", HttpStatus.OK);
    }

    @Operation(summary = "유저 최근 본 상품 조회 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/users/recent/preview")
    public ResponseEntity<?> recentProductPreview(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<ResponseProductPreviewDto> products = userService.recentProductPreview(principalDetails.getUser().getId());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "유저 최근 본 상품 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/users/recent")
    public ResponseEntity<?> recentProduct(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                           Pageable pageable) {
        Page<ResponseProductPreviewDto> products = userService.recentProduct(principalDetails.getUser().getId(), pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "체형 스마트 분석")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseMeasurementDto.class))))
    @PostMapping("/users/recommendation/measurement")
    public ResponseEntity<?> recommendMeasurement(@RequestBody @Valid RequestSmartMeasurementDto requestSmartMeasurementDto,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getValidationErrorResponse(bindingResult);
        }

        URI uri = UriComponentsBuilder.fromUriString(ML_MEASURE_API)
                .build()
                .toUri();
        ResponseMeasurementDto responseMeasurementDto = restTemplate.postForObject(uri, requestSmartMeasurementDto, ResponseMeasurementDto.class);
        return new ResponseEntity<>(responseMeasurementDto, HttpStatus.OK);
    }

    @Operation(summary = "추천 상품 조회 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/users/recommendation/preview")
    public ResponseEntity<?> recommendProductPreview(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<Long> productIds = findRecommendedProductIds(principalDetails.getUser().getId());
        List<ResponseProductPreviewDto> products = productService.recommendProduct(productIds, true);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "추천 상품 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/users/recommendation")
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

        URI uri = UriComponentsBuilder.fromUriString(ML_PRODUCT_RECOMMENDATION_API)
                .build()
                .toUri();
        RequestRecommendProductDto requestRecommendProductDto = userService.recentProductIds(userId);
        ResponseRecommendProductDto responseMeasurementDto = restTemplate.postForObject(uri, requestRecommendProductDto, ResponseRecommendProductDto.class);
        productIds = responseMeasurementDto.getProduct_ids();

        productIds.forEach(productId -> {
            productService.saveRecentRecommendation(userId, productId);
        });

        return productIds;
    }

    @Operation(summary = "비슷한 체형 고객 pick 조회 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/users/recommendation/pick/preview")
    public ResponseEntity<?> recommendProductPreviewOnPick(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<Long> productIds = findRecommendedProductIdsOnPick(principalDetails.getUser().getId());
        List<ResponseProductPreviewDto> products = productService.recommendProduct(productIds, true);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "비슷한 체형 고객 pick 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/users/recommendation/pick")
    public ResponseEntity<?> recommendProductOnPick(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<Long> productIds = findRecommendedProductIdsOnPick(principalDetails.getUser().getId());
        List<ResponseProductPreviewDto> products = productService.recommendProduct(productIds, false);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Cacheable(value = "recommendationOnPick", key = "#userId")
    public List<Long> findRecommendedProductIdsOnPick(Long userId) {
        List<Long> productIds = new ArrayList<>();
        List<Product> recommendedProducts = productService.productWithUserRecommendation(userId);

        if (!recommendedProducts.isEmpty()) {
            getRecommendedProductIds(productIds, recommendedProducts);
            return productIds;
        }

        URI uri = UriComponentsBuilder.fromUriString(ML_PRODUCT_RECOMMENDATION_WITH_MEASUREMENT_API)
                .build()
                .toUri();
        RequestRecommendProductOnUserDto requestRecommendProductOnUserDto = RequestRecommendProductOnUserDto.builder()
                .user_id(userId)
                .build();
        ResponseRecommendProductOnUserDto responseRecommendProductOnUserDto = restTemplate.postForObject(uri, requestRecommendProductOnUserDto, ResponseRecommendProductOnUserDto.class);
        productIds = responseRecommendProductOnUserDto.getProduct_ids();

        productIds.forEach(productId -> {
            productService.saveUserRecommendation(userId, productId);
        });

        return productIds;
    }

    @Operation(summary = "회원 탈퇴")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"회원 탈퇴 완료\"")))
    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        userService.delete(principalDetails.getUser().getId());
        return new ResponseEntity<>("회원 탈퇴 완료", HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 쇼핑몰 등록")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"즐겨찾기 등록 완료\"")))
    @PostMapping("/favorites/{mallId}")
    public ResponseEntity<?> setFavoriteMall(@PathVariable("mallId") @NotEmpty Long mallId,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        favoriteService.saveFavoriteMall(principalDetails.getUser().getId(), mallId);
        return new ResponseEntity<>("즐겨찾기 등록 완료", HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 쇼핑몰 삭제")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"즐겨찾기 삭제 완료\"")))
    @DeleteMapping("/favorites/{mallId}")
    public ResponseEntity<?> deleteFavoriteMall(@PathVariable("mallId") @NotEmpty Long mallId,
                                                @AuthenticationPrincipal PrincipalDetails principalDetails) {
        favoriteService.deleteFavoriteMall(principalDetails.getUser().getId(), mallId);
        return new ResponseEntity<>("즐겨찾기 삭제 완료", HttpStatus.OK);
    }

    private static void getRecommendedProductIds(List<Long> productIds, List<Product> recommendedProducts) {
        for (Product recommendedProduct : recommendedProducts) {
            productIds.add(recommendedProduct.getId());
        }
    }

    private void saveSilhouetteFileNameInCache(Long userId, boolean isFront, String savedFileName) {
        if (isFront) {
            redisTemplate.opsForValue().set(userId + FRONT_SILHOUETTE_KEY_SUFFIX, savedFileName);
            return;
        }
        redisTemplate.opsForValue().set(userId + SIDE_SILHOUETTE_KEY_SUFFIX, savedFileName);
    }

    private void deleteSilhouetteFileNameInCache(Long userId, boolean isFront) {
        if (isFront) {
            redisTemplate.delete(userId + FRONT_SILHOUETTE_KEY_SUFFIX);
            return;
        }
        redisTemplate.delete(userId + SIDE_SILHOUETTE_KEY_SUFFIX);
    }
}
