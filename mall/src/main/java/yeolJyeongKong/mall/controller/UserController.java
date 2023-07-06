package yeolJyeongKong.mall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import yeolJyeongKong.mall.service.FavoriteService;
import yeolJyeongKong.mall.service.ProductService;
import yeolJyeongKong.mall.service.UserService;

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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = UserDto.class))),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = UserDto.class)))
    })
    @GetMapping("/user/edit")
    public ResponseEntity<?> edit(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        UserDto userDto = userService.info(principalDetails.getUser().getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Operation(summary = "마이페이지 수정 메소드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = UserDto.class))),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = UserDto.class)))
    })
    @PostMapping("/user/edit")
    public ResponseEntity<?> edit(UserDto userDto,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        userService.infoUpdate(userDto, principalDetails.getUser().getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Operation(summary = "체형 정보 조회 메소드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = MeasurementDto.class))),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = MeasurementDto.class)))
    })
    @GetMapping("/user/mysize/edit")
    public ResponseEntity<?> measurementEdit(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        MeasurementDto measurementDto = userService.measurementInfo(principalDetails.getUser().getId());
        return new ResponseEntity<>(measurementDto, HttpStatus.OK);
    }

    @Operation(summary = "체형 정보 수정 메소드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = MeasurementDto.class))),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = MeasurementDto.class)))
    })
    @PostMapping("/user/mysize/edit")
    public ResponseEntity<?> measurementEdit(MeasurementDto measurementDto,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        userService.measurementUpdate(measurementDto, principalDetails.getUser().getId());
        return new ResponseEntity<>(measurementDto, HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 상품 조회 메소드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class)))),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    })
    @GetMapping("/user/favorite_goods")
    public ResponseEntity<?> favoriteProduct(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 Pageable pageable) {
        Page<ProductPreviewDto> products = productService.productWithUserFavorite(
                                                principalDetails.getUser().getId(), pageable
                                           );
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "유저 최근 본 상품 조회 메소드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class)))),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    })
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
    @Operation(summary = "추천 상품 조회 메소드 (미리보기)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class)))),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    })
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
    @Operation(summary = "추천 상품 조회 메소드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class)))),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPreviewDto.class))))
    })
    @GetMapping("/user/recommendation")
    public ResponseEntity<?> recommendProduct(@RequestParam RecommendProductsDto recommendProductsDto) {
        List<Long> productIds = recommendProductsDto.getProductIds();
        List<ProductPreviewDto> products = productService.recommendProduct(productIds, false);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 쇼핑몰 등록 메소드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"즐겨찾기 등록 완료\""))),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"즐겨찾기 등록 실페\"")))
    })
    @PostMapping("/favorite/{userId}/{mallId}")
    public ResponseEntity<?> setFavoriteMall(@PathVariable("userId") Long userId,
                                             @PathVariable("mallId") Long mallId) {
        favoriteService.setFavoriteMall(userId, mallId);
        return new ResponseEntity<>("즐겨찾기 등록 완료", HttpStatus.OK);
    }

    @Operation(summary = "유저 즐겨찾기 쇼핑몰 삭제 메소드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"즐겨찾기 삭제 완료\""))),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"즐겨찾기 삭제 실패\"")))
    })
    @DeleteMapping("/favorite/{userId}/{mallId}")
    public ResponseEntity<?> deleteFavoriteMall(@PathVariable("userId") Long userId,
                                                @PathVariable("mallId") Long mallId) {
        favoriteService.deleteFavoriteMall(userId, mallId);
        return new ResponseEntity<>("즐겨찾기 삭제 완료", HttpStatus.OK);
    }
}
