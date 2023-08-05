package fittering.mall.controller;

import fittering.mall.domain.dto.controller.request.RequestMallDto;
import fittering.mall.domain.dto.controller.response.ResponseMallDto;
import fittering.mall.domain.dto.controller.response.ResponseMallPreviewDto;
import fittering.mall.domain.mapper.MallMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import fittering.mall.config.auth.PrincipalDetails;
import fittering.mall.service.FavoriteService;
import fittering.mall.service.MallService;
import fittering.mall.service.RankService;

import java.util.List;

@Tag(name = "쇼핑몰", description = "쇼핑몰 서비스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MallController {

    private static final int MALL_RANL_SIZE = 4;
    private static final int MOBILE_MALL_RANK_SIZE = 3;
    private final MallService mallService;
    private final RankService rankService;
    private final FavoriteService favoriteService;

    @Operation(summary = "쇼핑몰 등록")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"쇼핑몰 등록 완료\"")))
    @PostMapping("/malls")
    public ResponseEntity<?> save(@RequestBody RequestMallDto requestMallDto) {
        mallService.save(MallMapper.INSTANCE.toMallDto(requestMallDto));
        return new ResponseEntity<>("쇼핑몰 등록 완료", HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseMallDto.class))))
    @GetMapping("/malls/{mallId}")
    public ResponseEntity<?> mallRank(@PathVariable("mallId") Long mallId,
                                      @AuthenticationPrincipal PrincipalDetails principalDetails) {
        ResponseMallDto responseMallDto = mallService.findById(mallId);
        rankService.updateViewOnMall(principalDetails.getUser().getId(), mallId);
        return new ResponseEntity<>(responseMallDto, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 랭킹 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseMallDto.class))))
    @GetMapping("/malls/rank")
    public ResponseEntity<?> mallRank(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<ResponseMallDto> malls = rankService.mallRank(principalDetails.getUser().getId());
        return new ResponseEntity<>(malls, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 랭킹 조회 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ResponseMallPreviewDto.class)))
    @GetMapping("/malls/rank/preview")
    public ResponseEntity<?> mallRankPreview(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                             Pageable pageable) {
        List<ResponseMallPreviewDto> malls = rankService.mallRankPreview(principalDetails.getUser().getId(),
                                                                         pageable, MALL_RANL_SIZE);
        return new ResponseEntity<>(malls, HttpStatus.OK);
    }

    @Operation(summary = "모바일 환경 쇼핑몰 랭킹 조회 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ResponseMallPreviewDto.class)))
    @GetMapping("/malls/rank/preview/mobile")
    public ResponseEntity<?> mallRankPreviewMobile(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   Pageable pageable) {
        List<ResponseMallPreviewDto> malls = rankService.mallRankPreview(principalDetails.getUser().getId(),
                                                                         pageable, MOBILE_MALL_RANK_SIZE);
        return new ResponseEntity<>(malls, HttpStatus.OK);
    }

    @Operation(summary = "즐겨찾기 쇼핑몰 상세 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ResponseMallDto.class)))
    @GetMapping("/malls/favorite_malls")
    public ResponseEntity<?> favoriteMall(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<ResponseMallDto> malls = favoriteService.userFavoriteMall(principalDetails.getUser().getId());
        return new ResponseEntity<>(malls, HttpStatus.OK);
    }
}
