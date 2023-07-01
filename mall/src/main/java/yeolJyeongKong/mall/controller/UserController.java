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
}
