package yeolJyeongKong.mall.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yeolJyeongKong.mall.domain.dto.MallDto;
import yeolJyeongKong.mall.service.FavoriteService;
import yeolJyeongKong.mall.service.RankService;

import java.awt.print.Pageable;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MallController {

    private final RankService rankService;
    private final FavoriteService favoriteService;

    @GetMapping("/malls/preview/mobile")
    public ResponseEntity<?> mallRankPreviewMobile(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   Pageable pageable) {
        List<MallDto> mallDtos = rankService.mallRankPreview(principalDetails.getUser().getId(),
                                                                     pageable, 3);
        return new ResponseEntity<>(mallDtos, HttpStatus.OK);
    }

    @GetMapping("/malls/preview/mobile")
    public ResponseEntity<?> mallRankPreview(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                             Pageable pageable) {
        List<MallDto> mallDtos = rankService.mallRankPreview(principalDetails.getUser().getId(),
                                                                     pageable, 4);
        return new ResponseEntity<>(mallDtos, HttpStatus.OK);
    }

    @GetMapping("/malls")
    public ResponseEntity<?> mallRank(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<MallDto> mallDtos = rankService.mallRank(principalDetails.getUser().getId());
        return new ResponseEntity<>(mallDtos, HttpStatus.OK);
    }

    /**
     * user flow에 맞게 추후 수정
     */
    @GetMapping("/malls/favorite_malls")
    public ResponseEntity<?> mallRank(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<MallDto> mallDtos = favoriteService.userFavoriteMall(principalDetails.getUser().getId());
        return new ResponseEntity<>(mallDtos, HttpStatus.OK);
    }
}
