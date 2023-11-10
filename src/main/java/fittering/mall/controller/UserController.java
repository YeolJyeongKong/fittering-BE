package fittering.mall.controller;

import fittering.mall.controller.dto.request.*;
import fittering.mall.controller.dto.response.*;
import fittering.mall.domain.collection.Products;
import fittering.mall.domain.entity.User;
import fittering.mall.service.dto.MeasurementDto;
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
    public ResponseEntity<?> edit(@AuthenticationPrincipal User user) {
        ResponseUserDto userInfo = userService.info(user.getId());
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    @Operation(summary = "마이페이지 수정")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = RequestUserDto.class)))
    @PostMapping("/users/mypage")
    public ResponseEntity<?> edit(@AuthenticationPrincipal User user,
                                  @RequestBody @Valid RequestUserDto requestUserDto,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getValidationErrorResponse(bindingResult);
        }
        userService.infoUpdate(requestUserDto, user.getId());
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
                                           @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        if(userService.passwordCheck(userId, password)) {
            userService.setPassword(userId, newPassword);
            return new ResponseEntity<>("비밀번호 변경 성공", HttpStatus.OK);
        }
        return new ResponseEntity<>("현재 비밀번호 인증 실패", HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "체형 정보 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ResponseMeasurementDto.class)))
    @GetMapping("/users/mysize")
    public ResponseEntity<?> measurementEdit(@AuthenticationPrincipal User user) {
        ResponseMeasurementDto measurement = userService.measurementInfo(user.getId());
        return new ResponseEntity<>(measurement, HttpStatus.OK);
    }

    @Operation(summary = "체형 정보 수정")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = MeasurementDto.class)))
    @PostMapping("/users/mysize")
    public ResponseEntity<?> measurementEdit(@AuthenticationPrincipal User user,
                                             @RequestBody @Valid RequestMeasurementDto requestMeasurementDto,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getValidationErrorResponse(bindingResult);
        }
        MeasurementDto measurementDto = MeasurementMapper.INSTANCE.toMeasurementDto(requestMeasurementDto);
        userService.measurementUpdate(measurementDto, user.getId());
        return new ResponseEntity<>(measurementDto, HttpStatus.OK);
    }

    @Operation(summary = "체형 실루엣 이미지 제공")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ResponseSilhouetteUrlDto.class)))
    @PostMapping("/users/mysize/silhouette")
    public ResponseEntity<?> silhouetteFromBody(@RequestParam("bodyFile") MultipartFile bodyFile,
                                                @RequestParam("type") String type,
                                                @AuthenticationPrincipal User user) throws IOException {
        Long userId = user.getId();
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
    public ResponseEntity<?> favoriteProduct(@AuthenticationPrincipal User user, Pageable pageable) {
        Page<ResponseProductPreviewDto> products = favoriteService.userFavoriteProduct(user.getId(), pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 상품 조회 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/users/favorite_goods/preview")
    public ResponseEntity<?> favoriteProductPreview(@AuthenticationPrincipal User user) {
        List<ResponseProductPreviewDto> products = favoriteService.userFavoriteProductPreview(user.getId());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 상품 등록")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"유저 즐겨찾기 상품 등록 완료\"")))
    @PostMapping("/users/favorite_goods/{productId}")
    public ResponseEntity<?> saveFavoriteProduct(@PathVariable("productId") @NotEmpty Long productId,
                                                 @AuthenticationPrincipal User user) {
        favoriteService.saveFavoriteProduct(user.getId(), productId);
        return new ResponseEntity<>("유저 즐겨찾기 상품 등록 완료", HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 상품 삭제")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"유저 즐겨찾기 상품 삭제 완료\"")))
    @DeleteMapping("/users/favorite_goods/{productId}")
    public ResponseEntity<?> deleteFavoriteProduct(@PathVariable("productId") @NotEmpty Long productId,
                                                   @AuthenticationPrincipal User user) {
        favoriteService.deleteFavoriteProduct(user.getId(), productId);
        return new ResponseEntity<>("유저 즐겨찾기 상품 삭제 완료", HttpStatus.OK);
    }

    @Operation(summary = "유저 최근 본 상품 조회 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/users/recent/preview")
    public ResponseEntity<?> recentProductPreview(@AuthenticationPrincipal User user) {
        List<ResponseProductPreviewDto> products = userService.recentProductPreview(user.getId());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "유저 최근 본 상품 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/users/recent")
    public ResponseEntity<?> recentProduct(@AuthenticationPrincipal User user, Pageable pageable) {
        Page<ResponseProductPreviewDto> products = userService.recentProduct(user.getId(), pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "체형 스마트 분석")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ResponseMeasurementDto.class)))
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
    public ResponseEntity<?> recommendProductPreview(@AuthenticationPrincipal User user) {
        List<Long> productIds = findRecommendedProductIds(user.getId());
        List<ResponseProductPreviewDto> products = productService.recommendProduct(productIds, true);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "추천 상품 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/users/recommendation")
    public ResponseEntity<?> recommendProduct(@AuthenticationPrincipal User user) {
        List<Long> productIds = findRecommendedProductIds(user.getId());
        List<ResponseProductPreviewDto> products = productService.recommendProduct(productIds, false);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Cacheable(value = "recommendation", key = "#userId")
    public List<Long> findRecommendedProductIds(Long userId) {
        Products recommendedProducts = productService.productWithRecentRecommendation(userId);

        if(!recommendedProducts.isEmpty()) {
            return new ArrayList<>(recommendedProducts.getProductIds());
        }

        URI uri = UriComponentsBuilder.fromUriString(ML_PRODUCT_RECOMMENDATION_API)
                .build()
                .toUri();
        RequestRecommendProductDto requestRecommendProductDto = userService.recentProductIds(userId);
        ResponseRecommendProductDto responseMeasurementDto = restTemplate.postForObject(uri, requestRecommendProductDto, ResponseRecommendProductDto.class);
        List<Long> productIds = responseMeasurementDto.getProduct_ids();

        productIds.forEach(productId -> {
            productService.saveRecentRecommendation(userId, productId);
        });

        return productIds;
    }

    @Operation(summary = "비슷한 체형 고객 pick 조회 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/users/recommendation/pick/preview")
    public ResponseEntity<?> recommendProductPreviewOnPick(@AuthenticationPrincipal User user) {
        List<Long> productIds = findRecommendedProductIdsOnPick(user.getId());
        List<ResponseProductPreviewDto> products = productService.recommendProduct(productIds, true);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "비슷한 체형 고객 pick 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProductPreviewDto.class))))
    @GetMapping("/users/recommendation/pick")
    public ResponseEntity<?> recommendProductOnPick(@AuthenticationPrincipal User user) {
        List<Long> productIds = findRecommendedProductIdsOnPick(user.getId());
        List<ResponseProductPreviewDto> products = productService.recommendProduct(productIds, false);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Cacheable(value = "recommendationOnPick", key = "#userId")
    public List<Long> findRecommendedProductIdsOnPick(Long userId) {
        Products recommendedProducts = productService.productWithUserRecommendation(userId);

        if (!recommendedProducts.isEmpty()) {
            return new ArrayList<>(recommendedProducts.getProductIds());
        }

        URI uri = UriComponentsBuilder.fromUriString(ML_PRODUCT_RECOMMENDATION_WITH_MEASUREMENT_API)
                .build()
                .toUri();
        RequestRecommendProductOnUserDto requestRecommendProductOnUserDto = RequestRecommendProductOnUserDto.builder()
                .user_id(userId)
                .build();
        ResponseRecommendProductOnUserDto responseRecommendProductOnUserDto = restTemplate.postForObject(uri, requestRecommendProductOnUserDto, ResponseRecommendProductOnUserDto.class);
        List<Long> productIds = responseRecommendProductOnUserDto.getProduct_ids();

        productIds.forEach(productId -> {
            productService.saveUserRecommendation(userId, productId);
        });

        return productIds;
    }

    @Operation(summary = "회원 탈퇴")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"회원 탈퇴 완료\"")))
    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal User user,
                                        @RequestBody @Valid RequestUserCheckDto requestUserCheckDto,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getValidationErrorResponse(bindingResult);
        }
        String email = requestUserCheckDto.getEmail();
        String password = requestUserCheckDto.getPassword();
        Long currentUserId = user.getId();
        if (userService.isValidEmailAndPassword(email, password) && userService.isSameUser(currentUserId, email)) {
            userService.delete(currentUserId);
            return new ResponseEntity<>("회원 탈퇴 완료", HttpStatus.OK);
        }
        return new ResponseEntity<>("회원 탈퇴 실패", HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "유저 즐겨찾기 쇼핑몰 등록")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"즐겨찾기 등록 완료\"")))
    @PostMapping("/favorites/{mallId}")
    public ResponseEntity<?> setFavoriteMall(@PathVariable("mallId") @NotEmpty Long mallId,
                                             @AuthenticationPrincipal User user) {
        favoriteService.saveFavoriteMall(user.getId(), mallId);
        return new ResponseEntity<>("즐겨찾기 등록 완료", HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 쇼핑몰 삭제")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"즐겨찾기 삭제 완료\"")))
    @DeleteMapping("/favorites/{mallId}")
    public ResponseEntity<?> deleteFavoriteMall(@PathVariable("mallId") @NotEmpty Long mallId,
                                                @AuthenticationPrincipal User user) {
        favoriteService.deleteFavoriteMall(user.getId(), mallId);
        return new ResponseEntity<>("즐겨찾기 삭제 완료", HttpStatus.OK);
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
