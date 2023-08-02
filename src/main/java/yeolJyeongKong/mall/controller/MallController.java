package yeolJyeongKong.mall.controller;

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
import yeolJyeongKong.mall.config.PrincipalDetails;
import yeolJyeongKong.mall.domain.dto.MallDto;
import yeolJyeongKong.mall.domain.dto.MallPreviewDto;
import yeolJyeongKong.mall.service.FavoriteService;
import yeolJyeongKong.mall.service.MallService;
import yeolJyeongKong.mall.service.RankService;

import java.util.List;

@Tag(name = "쇼핑몰", description = "쇼핑몰 서비스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MallController {

    private final MallService mallService;
    private final RankService rankService;
    private final FavoriteService favoriteService;

    @Operation(summary = "쇼핑몰 등록 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"쇼핑몰 등록 완료\"")))
    @PostMapping("/malls/add")
    public ResponseEntity<?> save(@RequestBody MallDto mallDto) {
        mallService.save(mallDto);
        return new ResponseEntity<>("쇼핑몰 등록 완료", HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 랭킹 조회 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = MallDto.class))))
    @GetMapping("/malls/rank")
    public ResponseEntity<?> mallRank(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<MallDto> mallDtos = rankService.mallRank(principalDetails.getUser().getId());
        return new ResponseEntity<>(mallDtos, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 랭킹 조회 메소드 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = MallPreviewDto.class)))
    @GetMapping("/malls/rank/preview")
    public ResponseEntity<?> mallRankPreview(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                             Pageable pageable) {
        List<MallPreviewDto> mallDtos = rankService.mallRankPreview(principalDetails.getUser().getId(),
                pageable, 4);
        return new ResponseEntity<>(mallDtos, HttpStatus.OK);
    }

    @Operation(summary = "모바일 환경 쇼핑몰 랭킹 조회 메소드 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = MallPreviewDto.class)))
    @GetMapping("/malls/rank/preview/mobile")
    public ResponseEntity<?> mallRankPreviewMobile(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   Pageable pageable) {
        List<MallPreviewDto> mallDtos = rankService.mallRankPreview(principalDetails.getUser().getId(),
                                                                    pageable, 3);
        return new ResponseEntity<>(mallDtos, HttpStatus.OK);
    }

    /**
     * user flow에 맞게 추후 수정
     */
    @Operation(summary = "즐겨찾기 쇼핑몰 상세 조회 메소드")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = MallDto.class)))
    @GetMapping("/malls/favorite_malls")
    public ResponseEntity<?> favoriteMall(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<MallDto> mallDtos = favoriteService.userFavoriteMall(principalDetails.getUser().getId());
        return new ResponseEntity<>(mallDtos, HttpStatus.OK);
    }
}
