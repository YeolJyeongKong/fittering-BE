package yeolJyeongKong.mall.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import yeolJyeongKong.mall.domain.RestPage;
import yeolJyeongKong.mall.domain.dto.*;
import yeolJyeongKong.mall.domain.entity.*;
import yeolJyeongKong.mall.repository.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    MallService mallService;
    @Autowired
    FavoriteService favoriteService;
    @Autowired
    RankService rankService;

    @Autowired
    FavoriteRepository favoriteRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RankRepository rankRepository;
    @Autowired
    MallRepository mallRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    MeasurementRepository measurementRepository;
    @Autowired
    RecentRecommendationRepository recentRecommendationRepository;
    @Autowired
    UserRecommendationRepository userRecommendationRepository;

    private User user;
    private Category category;
    private Mall mall;
    private Product product;

    @BeforeEach
    void setUp() {
        user = userService.save(new SignUpDto("test", "password", "test@test.com", "M", 1, 2, 3));
        category = categoryService.save("top");
        mall = mallService.save(new MallDto("testMall", "testMall.com",
                "image.jpg", "desc", new ArrayList<>()));
        List<String> descImgsStr = new ArrayList<>(){{ add("descImage.jpg"); }};
        List<DescriptionImage> descImgs = new ArrayList<>(){{ add(new DescriptionImage(descImgsStr.get(0))); }};
        product = productService.save(new Product(
                new ProductDetailDto(10000, "tp1", "M", 0,
                        "image.jpg", "top",
                        "testMall", null, null, descImgsStr),
                category, mall, descImgs));
    }

    @Test
    void findById() {
        User findUser = userService.findById(user.getId());
        assertThat(findUser).isEqualTo(user);
    }

    @Test
    void findByEmail() {
        User findUser = userService.findByEmail(user.getEmail());
        assertThat(findUser).isEqualTo(user);
    }

    @Test
    void info() {
        UserDto userInfo = userService.info(user.getId());
        assertThat(userInfo.getEmail()).isEqualTo(user.getEmail());
        assertThat(userInfo.getUsername()).isEqualTo(user.getUsername());
        assertThat(userInfo.getGender()).isEqualTo(user.getGender());
        assertThat(userInfo.getYear()).isEqualTo(user.getYear());
        assertThat(userInfo.getMonth()).isEqualTo(user.getMonth());
        assertThat(userInfo.getDay()).isEqualTo(user.getDay());
    }

    @Test
    void infoUpdate() {
        UserDto userDto = new UserDto("test@test.com", "testUser", "M", 1996, 12, 25);
        userService.infoUpdate(userDto, user.getId());

        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(user.getUsername()).isEqualTo(userDto.getUsername());
        assertThat(user.getGender()).isEqualTo(userDto.getGender());
        assertThat(user.getYear()).isEqualTo(userDto.getYear());
        assertThat(user.getMonth()).isEqualTo(userDto.getMonth());
        assertThat(user.getDay()).isEqualTo(userDto.getDay());
    }

    @Test
    void setPassword() {
        userService.setPassword(user.getId(), "newPassword");
        assertThat(userService.passwordCheck(user.getId(), "newPassword")).isTrue();
    }

    @Test
    void measurementInfo() {
        MeasurementDto measurement = userService.measurementInfo(user.getId());
        measurementCheck(measurement, new MeasurementDto(new Measurement()));

        MeasurementDto newMeasurment = new MeasurementDto(200, 100, 100, 120, 100, 99, 98, 80, 76);
        userService.measurementUpdate(newMeasurment, user.getId());

        measurementCheck(newMeasurment, new MeasurementDto(user.getMeasurement()));
    }

    @Test
    void recentProduct() {
        userService.saveRecentProduct(user.getId(), product.getId());
        List<ProductPreviewDto> products = userService.recentProductPreview(user.getId());
        compareProduct(product, products.get(0));
    }

    @Test
    void favoriteProductTest() {
        favoriteService.saveFavoriteProduct(user.getId(), product.getId());
        RestPage<ProductPreviewDto> findProducts = favoriteService.userFavoriteProduct(user.getId(), PageRequest.of(0, 10));
        compareProduct(product, findProducts.getContent().get(0));

        favoriteService.deleteFavoriteProduct(user.getId(), product.getId());
        RestPage<ProductPreviewDto> deletedProducts = favoriteService.userFavoriteProduct(user.getId(), PageRequest.of(0, 10));
        assertThat(deletedProducts.getContent().size()).isEqualTo(0);
    }

    @Test
    void login() {
        User findUser = userService.login(new LoginDto(user.getEmail(), "password"));
        assertThat(findUser).isEqualTo(user);

        User wrongUser = userService.login((new LoginDto(user.getEmail(), "wrongPassword")));
        assertThat(wrongUser).isNull();
    }

    @Test
    void usernameExist() {
        assertThat(userService.usernameExist("test")).isTrue();
        assertThat(userService.usernameExist("tes")).isFalse();
    }

    @Test
    void emailExist() {
        assertThat(userService.emailExist("test@test.com")).isTrue();
        assertThat(userService.emailExist("tes@tes.com")).isFalse();
    }

    @Test
    void delete() {
        Favorite favoriteMall = favoriteService.saveFavoriteMall(user.getId(), mall.getId());
        Favorite favoriteProduct = favoriteService.saveFavoriteProduct(user.getId(), product.getId());

        Rank rank = rankService.save(user.getId(), mall.getId());
        Recent recent = userService.saveRecentProduct(user.getId(), product.getId());

        RecentRecommendation recentRecommendation = productService.saveRecentRecommendation(user.getId(), product.getId());
        UserRecommendation userRecommendation = productService.saveUserRecommendation(user.getId(), product.getId());

        userService.delete(user.getId());

        assertThat(favoriteRepository.findById(favoriteMall.getId())).isEmpty();
        assertThat(favoriteRepository.findById(favoriteProduct.getId())).isEmpty();
        assertThat(mallRepository.findFavoriteCount(mall.getId())).isEqualTo(0L);
        assertThat(productRepository.findFavoriteCount(product.getId())).isEqualTo(0L);

        assertThat(measurementRepository.findById(user.getMeasurement().getId())).isEmpty();

        assertThat(rankRepository.findById(rank.getId())).isEmpty();
        assertThat(mallRepository.findByRankId(rank.getId())).isEmpty();

        assertThat(productRepository.findRecentCount(recent.getId())).isEqualTo(0L);

        assertThat(recentRecommendationRepository.findById(recentRecommendation.getId())).isEmpty();
        assertThat(productRepository.findRecentRecommendation(recentRecommendation.getId())).isEqualTo(0L);
        assertThat(userRecommendationRepository.findById(userRecommendation.getId())).isEmpty();
        assertThat(productRepository.findUserRecommendation(userRecommendation.getId())).isEqualTo(0L);

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    private static void measurementCheck(MeasurementDto measurementDto, MeasurementDto measurementDto2) {
        assertThat(measurementDto.getHeight()).isEqualTo(measurementDto2.getHeight());
        assertThat(measurementDto.getWeight()).isEqualTo(measurementDto2.getWeight());
        assertThat(measurementDto.getArm()).isEqualTo(measurementDto2.getArm());
        assertThat(measurementDto.getLeg()).isEqualTo(measurementDto2.getLeg());
        assertThat(measurementDto.getShoulder()).isEqualTo(measurementDto2.getShoulder());
        assertThat(measurementDto.getWaist()).isEqualTo(measurementDto2.getWaist());
        assertThat(measurementDto.getChest()).isEqualTo(measurementDto2.getChest());
        assertThat(measurementDto.getThigh()).isEqualTo(measurementDto2.getThigh());
        assertThat(measurementDto.getHip()).isEqualTo(measurementDto2.getHip());
    }

    private static void compareProduct(Product savedProduct, ProductPreviewDto productPreviewDto) {
        assertThat(productPreviewDto.getProductId()).isEqualTo(savedProduct.getId());
        assertThat(productPreviewDto.getProductImage()).isEqualTo(savedProduct.getImage());
        assertThat(productPreviewDto.getProductName()).isEqualTo(savedProduct.getName());
        assertThat(productPreviewDto.getPrice()).isEqualTo(savedProduct.getPrice());
        assertThat(productPreviewDto.getMallName()).isEqualTo(savedProduct.getMall().getName());
        assertThat(productPreviewDto.getMallUrl()).isEqualTo(savedProduct.getMall().getUrl());
    }
}