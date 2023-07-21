package yeolJyeongKong.mall.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yeolJyeongKong.mall.domain.dto.MallDto;
import yeolJyeongKong.mall.domain.dto.SignUpDto;
import yeolJyeongKong.mall.domain.entity.Favorite;
import yeolJyeongKong.mall.domain.entity.Mall;
import yeolJyeongKong.mall.domain.entity.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class FavoriteServiceTest {

    @Autowired
    FavoriteService favoriteService;
    @Autowired
    UserService userService;
    @Autowired
    MallService mallService;

    @Test
    @DisplayName("유저 즐겨찾기 쇼핑몰 테스트")
    void favoriteMallTest() {
        User user = userService.save(new SignUpDto("tes", "password", "test@test.com", "M", 1, 2, 3));
        Mall mall1 = mallService.save(new MallDto("testMall1", "testMall.com", "image.jpg", "desc", new ArrayList<>()));
        Mall mall2 = mallService.save(new MallDto("testMall2", "testMall.com", "image.jpg", "desc", new ArrayList<>()));
        Mall mall3 = mallService.save(new MallDto("testMall3", "testMall.com", "image.jpg", "desc", new ArrayList<>()));

        Favorite savedFavorite1 = favoriteService.setFavoriteMall(user.getId(), mall1.getId());
        Favorite savedFavorite2 = favoriteService.setFavoriteMall(user.getId(), mall2.getId());
        Favorite savedFavorite3 = favoriteService.setFavoriteMall(user.getId(), mall3.getId());

        List<MallDto> savedFavorite = new ArrayList<>();
        savedFavorite.add(createMallDto(savedFavorite1));
        savedFavorite.add(createMallDto(savedFavorite2));
        savedFavorite.add(createMallDto(savedFavorite3));

        List<MallDto> findFavorites = favoriteService.userFavoriteMall(user.getId());
        for (int i=0; i<findFavorites.size(); i++) {
            MallDto savedMallDto = savedFavorite.get(i);
            MallDto findMallDto = findFavorites.get(i);
            checkMallDto(savedMallDto, findMallDto);
        }

        favoriteService.deleteFavoriteMall(user.getId(), mall1.getId());
        favoriteService.deleteFavoriteMall(user.getId(), mall2.getId());
        favoriteService.deleteFavoriteMall(user.getId(), mall3.getId());

        List<MallDto> deletedUserList = favoriteService.userFavoriteMall(user.getId());
        assertThat(deletedUserList.size()).isEqualTo(0);
    }

    private static void checkMallDto(MallDto savedMallDto, MallDto findMallDto) {
        assertThat(savedMallDto.getName()).isEqualTo(findMallDto.getName());
        assertThat(savedMallDto.getUrl()).isEqualTo(findMallDto.getUrl());
        assertThat(savedMallDto.getImage()).isEqualTo(findMallDto.getImage());
        assertThat(savedMallDto.getDescription()).isEqualTo(findMallDto.getDescription());
    }

    private static MallDto createMallDto(Favorite favorite) {
        Mall mall = favorite.getMall();
        return new MallDto(mall.getName(), mall.getUrl(), mall.getImage(), mall.getDescription(), new ArrayList<>());
    }
}