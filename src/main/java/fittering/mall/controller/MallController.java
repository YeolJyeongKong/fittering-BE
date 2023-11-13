package fittering.mall.controller;

import fittering.mall.controller.dto.request.RequestMallDto;
import fittering.mall.controller.dto.response.ResponseMallDto;
import fittering.mall.controller.dto.response.ResponseMallNameAndIdDto;
import fittering.mall.controller.dto.response.ResponseMallPreviewDto;
import fittering.mall.controller.dto.response.ResponseMallWithProductDto;
import fittering.mall.domain.entity.User;
import fittering.mall.domain.mapper.MallMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import fittering.mall.service.FavoriteService;
import fittering.mall.service.MallService;
import fittering.mall.service.RankService;

import java.util.List;

import static fittering.mall.controller.ControllerUtils.getValidationErrorResponse;

@Tag(name = "쇼핑몰", description = "쇼핑몰 서비스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MallController {

    private static final int MALL_RANK_SIZE = 4;
    private static final int MOBILE_MALL_RANK_SIZE = 3;

    private final MallService mallService;
    private final RankService rankService;
    private final FavoriteService favoriteService;

    @Operation(summary = "쇼핑몰 등록")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"), examples = @ExampleObject(value = "\"쇼핑몰 등록 완료\"")))
    @PostMapping("/auth/malls")
    public ResponseEntity<?> save(@RequestBody @Valid RequestMallDto requestMallDto,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getValidationErrorResponse(bindingResult);
        }
        mallService.save(MallMapper.INSTANCE.toMallDto(requestMallDto));
        return new ResponseEntity<>("쇼핑몰 등록 완료", HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ResponseMallDto.class)))
    @GetMapping("/auth/malls/{mallId}")
    public ResponseEntity<?> mallRank(@PathVariable("mallId") @NotEmpty Long mallId,
                                      @AuthenticationPrincipal User user) {
        ResponseMallDto responseMallDto = mallService.findById(user.getId(), mallId);
        rankService.updateViewOnMall(user.getId(), mallId);
        return new ResponseEntity<>(responseMallDto, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 이름 및 ID 리스트 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseMallNameAndIdDto.class))))
    @GetMapping("/malls/list")
    public ResponseEntity<?> mallNameAndIdList() {
        List<ResponseMallNameAndIdDto> mallList = mallService.findMallNameAndIdList();
        return new ResponseEntity<>(mallList, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 전체 리스트 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseMallWithProductDto.class))))
    @GetMapping("/malls/preview/list")
    public ResponseEntity<?> mallList() {
        List<ResponseMallWithProductDto> mallList = mallService.findAll();
        return new ResponseEntity<>(mallList, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 랭킹 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseMallWithProductDto.class))))
    @GetMapping("/auth/malls/rank")
    public ResponseEntity<?> mallRank(@AuthenticationPrincipal User user) {
        List<ResponseMallWithProductDto> malls = rankService.mallRank(user.getId());
        return new ResponseEntity<>(malls, HttpStatus.OK);
    }

    @Operation(summary = "쇼핑몰 랭킹 조회 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ResponseMallPreviewDto.class)))
    @GetMapping("/auth/malls/rank/preview")
    public ResponseEntity<?> mallRankPreview(@AuthenticationPrincipal User user, Pageable pageable) {
        List<ResponseMallPreviewDto> malls = rankService.mallRankPreview(user.getId(), pageable, MALL_RANK_SIZE);
        return new ResponseEntity<>(malls, HttpStatus.OK);
    }

    @Operation(summary = "모바일 환경 쇼핑몰 랭킹 조회 (미리보기)")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = ResponseMallPreviewDto.class)))
    @GetMapping("/auth/malls/rank/preview/mobile")
    public ResponseEntity<?> mallRankPreviewMobile(@AuthenticationPrincipal User user, Pageable pageable) {
        List<ResponseMallPreviewDto> malls = rankService.mallRankPreview(user.getId(), pageable, MOBILE_MALL_RANK_SIZE);
        return new ResponseEntity<>(malls, HttpStatus.OK);
    }

    @Operation(summary = "즐겨찾기 쇼핑몰 상세 조회")
    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseMallWithProductDto.class))))
    @GetMapping("/auth/malls/favorite_malls")
    public ResponseEntity<?> favoriteMall(@AuthenticationPrincipal User user) {
        List<ResponseMallWithProductDto> malls = favoriteService.userFavoriteMall(user.getId());
        return new ResponseEntity<>(malls, HttpStatus.OK);
    }
}
