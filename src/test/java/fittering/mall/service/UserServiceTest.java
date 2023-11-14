package fittering.mall.service;

import fittering.mall.config.IntegrationTestSupport;
import fittering.mall.controller.dto.request.RequestRecommendProductDto;
import fittering.mall.controller.dto.request.RequestUserDto;
import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.controller.dto.response.ResponseUserDto;
import fittering.mall.service.dto.LoginDto;
import fittering.mall.service.dto.MallDto;
import fittering.mall.service.dto.MeasurementDto;
import fittering.mall.service.dto.SignUpDto;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import fittering.mall.domain.entity.*;
import fittering.mall.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MallService mallService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private RecentRepository recentRepository;

    @Autowired
    private RankRepository rankRepository;

    @Autowired
    private MallRepository mallRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private RecentRecommendationRepository recentRecommendationRepository;

    @Autowired
    private UserRecommendationRepository userRecommendationRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.keys("*").forEach(key -> redisTemplate.delete(key));
        recentRepository.deleteAllInBatch();
        rankRepository.deleteAllInBatch();
        recentRecommendationRepository.deleteAllInBatch();
        userRecommendationRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        measurementRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        subCategoryRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        mallRepository.deleteAllInBatch();
    }

    @DisplayName("유저를 저장할 수 있다.")
    @Test
    void saveUser() {
        //given
        SignUpDto request = createUser();

        //when
        User target = userService.save(request);

        //then
        assertThat(target.getId()).isNotNull();
        assertThat(target)
                .extracting("username", "email", "gender", "year", "month", "day")
                .containsExactlyInAnyOrder(
                        request.getUsername(),
                        request.getEmail(),
                        request.getGender(),
                        request.getYear(),
                        request.getMonth(),
                        request.getDay()
                );
    }

    @DisplayName("유저를 id로 조회할 수 있다.")
    @Test
    void findUserById() {
        //given
        User user = userService.save(createUser());

        //when
        User target = userService.findById(user.getId());

        //then
        assertThat(target)
                .extracting("id", "username", "email", "gender", "year", "month", "day")
                .containsExactlyInAnyOrder(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getGender(),
                        user.getYear(),
                        user.getMonth(),
                        user.getDay()
                );
    }

    @DisplayName("등록된 유저가 없으면 id로 조회할 수 없다.")
    @Test
    void findUserByIdWhenNoUser() {
        //given
        Long fakeUserId = 1L;

        //when //then
        assertThatThrownBy(() -> userService.findById(fakeUserId))
                .isInstanceOf(NoResultException.class)
                .hasMessage("user dosen't exist");
    }

    @DisplayName("유저를 email로 조회할 수 있다.")
    @Test
    void findUserByEmail() {
        //given
        User user = userService.save(createUser());

        //when
        User target = userService.findByEmail(user.getEmail());

        //then
        assertThat(target)
                .extracting("id", "username", "email", "gender", "year", "month", "day")
                .containsExactlyInAnyOrder(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getGender(),
                        user.getYear(),
                        user.getMonth(),
                        user.getDay()
                );
    }

    @DisplayName("유저를 email로 조회할 수 있다.")
    @Test
    void findUserByEmailWhenNoUser() {
        //given
        String fakeUserEmail = "fake@email.com";

        //when
        User target = userService.findByEmail(fakeUserEmail);

        //then
        assertThat(target).isNull();
    }

    @DisplayName("유저의 정보를 조회할 수 있다.")
    @Test
    void getUserInfo() {
        //given
        User user = userService.save(createUser());

        //when
        ResponseUserDto target = userService.info(user.getId());

        //then
        assertThat(target)
                .extracting("email", "username", "gender", "year", "month", "day")
                .containsExactlyInAnyOrder(
                        user.getEmail(),
                        user.getUsername(),
                        user.getGender(),
                        user.getYear(),
                        user.getMonth(),
                        user.getDay()
                );
    }

    @DisplayName("유저의 정보를 조회할 수 있다.")
    @Test
    void updateUserInfo() {
        //given
        RequestUserDto requestUserDto = RequestUserDto.builder()
                .email("after@email.com")
                .username("after")
                .gender("M")
                .year(1991)
                .month(2)
                .day(2)
                .build();
        User user = userService.save(createUser());

        //when
        userService.infoUpdate(requestUserDto, user.getId());

        //then
        userRepository.findById(user.getId()).ifPresent(target -> {
            assertThat(target)
                    .extracting("username", "gender", "year", "month", "day")
                    .containsExactlyInAnyOrder(
                            requestUserDto.getUsername(),
                            requestUserDto.getGender(),
                            requestUserDto.getYear(),
                            requestUserDto.getMonth(),
                            requestUserDto.getDay()
                    );
        });
    }

    @DisplayName("등록되지 않은 유저의 정보를 조회할 수 없다.")
    @Test
    void updateUserInfoWhenNoUser() {
        //given
        RequestUserDto requestUserDto = RequestUserDto.builder()
                .email("after@email.com")
                .username("after")
                .gender("M")
                .year(1991)
                .month(2)
                .day(2)
                .build();
        Long fakeUserId = 1L;

        //when //then
        assertThatThrownBy(() -> userService.infoUpdate(requestUserDto, fakeUserId))
                .isInstanceOf(NoResultException.class)
                .hasMessage("user dosen't exist");
    }

    @DisplayName("기존 비밀번호와 새 비밀번호가 같은지 검증합니다.")
    @Test
    void checkPassword() {
        //given
        User user = userService.save(createUser());
        String password = "password";

        //when
        boolean target = userService.passwordCheck(user.getId(), password);

        //then
        assertThat(target).isTrue();
    }

    @DisplayName("새 비밀번호를 등록합니다.")
    @Test
    void setPassword() {
        //given
        User user = userService.save(createUser());
        String password = "password2";

        //when
        userService.setPassword(user.getId(), password);

        //then
        assertThat(userService.passwordCheck(user.getId(), password)).isTrue();
    }

    @DisplayName("유저의 신체 정보를 업데이트합니다.")
    @Test
    void updateMeasurement() {
        //given
        MeasurementDto measurementDto = MeasurementDto.builder()
                .height(100.0)
                .weight(100.0)
                .arm(100.0)
                .leg(100.0)
                .shoulder(100.0)
                .waist(100.0)
                .chest(100.0)
                .thigh(100.0)
                .hip(100.0)
                .build();
        User user = userService.save(createUser());

        //when
        userService.measurementUpdate(measurementDto, user.getId());

        //then
        measurementRepository.findByUserId(user.getId()).ifPresent(target -> {
            assertThat(target)
                    .extracting("height", "weight", "arm", "leg", "shoulder", "waist", "chest", "thigh", "hip")
                    .containsExactlyInAnyOrder(
                            measurementDto.getHeight(),
                            measurementDto.getWeight(),
                            measurementDto.getArm(),
                            measurementDto.getLeg(),
                            measurementDto.getShoulder(),
                            measurementDto.getWaist(),
                            measurementDto.getChest(),
                            measurementDto.getThigh(),
                            measurementDto.getHip()
                    );
        });
    }

    @DisplayName("미리보기 버전에서 유저의 최근 본 상품을 조회할 수 있다.")
    @Test
    void getRecentProductOnPreview() {
        //given
        User user = userService.save(createUser());
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");
        Mall mall = mallService.save(createMall(1));
        Product product1 = productService.save(createProduct(1, topCategory, topSubCategory, mall));
        Product product2 = productService.save(createProduct(2, topCategory, topSubCategory, mall));
        Product product3 = productService.save(createProduct(3, topCategory, topSubCategory, mall));

        recentRepository.save(createRecent(user, product1));
        recentRepository.save(createRecent(user, product2));
        recentRepository.save(createRecent(user, product3));

        //when
        List<ResponseProductPreviewDto> target = userService.recentProductPreview(user.getId());

        //then
        assertThat(target).hasSize(3)
                .extracting("productId", "productImage", "productName", "price", "mallName", "mallUrl")
                .containsExactlyInAnyOrder(
                        tuple(product1.getId(), product1.getImage(), product1.getName(), product1.getPrice(), mall.getName(), mall.getUrl()),
                        tuple(product2.getId(), product2.getImage(), product2.getName(), product2.getPrice(), mall.getName(), mall.getUrl()),
                        tuple(product3.getId(), product3.getImage(), product3.getName(), product3.getPrice(), mall.getName(), mall.getUrl())
                );
    }

    @DisplayName("유저의 최근 본 상품을 조회할 수 있다.")
    @Test
    void getRecentProduct() {
        //given
        User user = userService.save(createUser());
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");
        Mall mall = mallService.save(createMall(1));
        Product product1 = productService.save(createProduct(1, topCategory, topSubCategory, mall));
        Product product2 = productService.save(createProduct(2, topCategory, topSubCategory, mall));
        Product product3 = productService.save(createProduct(3, topCategory, topSubCategory, mall));

        recentRepository.save(createRecent(user, product1));
        recentRepository.save(createRecent(user, product2));
        recentRepository.save(createRecent(user, product3));

        //when
        Page<ResponseProductPreviewDto> target = userService.recentProduct(user.getId(), PageRequest.of(0, 20));

        //then
        assertThat(target).hasSize(3)
                .extracting("productId", "productImage", "productName", "price", "mallName", "mallUrl")
                .containsExactlyInAnyOrder(
                        tuple(product1.getId(), product1.getImage(), product1.getName(), product1.getPrice(), mall.getName(), mall.getUrl()),
                        tuple(product2.getId(), product2.getImage(), product2.getName(), product2.getPrice(), mall.getName(), mall.getUrl()),
                        tuple(product3.getId(), product3.getImage(), product3.getName(), product3.getPrice(), mall.getName(), mall.getUrl())
                );
    }

    @DisplayName("유저의 최근 본 상품 id 리스트를 조회할 수 있다.")
    @Test
    void getRecentProductIds() {
        //given
        User user = userService.save(createUser());
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");
        Mall mall = mallService.save(createMall(1));
        Product product1 = productService.save(createProduct(1, topCategory, topSubCategory, mall));
        Product product2 = productService.save(createProduct(2, topCategory, topSubCategory, mall));
        Product product3 = productService.save(createProduct(3, topCategory, topSubCategory, mall));

        recentRepository.save(createRecent(user, product1));
        recentRepository.save(createRecent(user, product2));
        recentRepository.save(createRecent(user, product3));

        //when
        RequestRecommendProductDto target = userService.recentProductIds(user.getId());

        //then
        assertThat(target.getProduct_ids()).hasSize(3)
                .containsExactlyInAnyOrder(
                        product1.getId(), product2.getId(), product3.getId()
                );
        assertThat(target.getGender()).isEqualTo(user.getGender());
    }

    @DisplayName("유저의 최근 본 상품을 저장할 수 있다.")
    @Test
    void saveRecentProduct() {
        //given
        User user = userService.save(createUser());
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");
        Mall mall = mallService.save(createMall(1));
        Product product = productService.save(createProduct(1, topCategory, topSubCategory, mall));

        //when
        Recent target = userService.saveRecentProduct(user.getId(), product.getId());

        //then
        assertThat(target.getId()).isNotNull();
        assertThat(target)
                .extracting("user.id", "product.id")
                .containsExactlyInAnyOrder(
                        user.getId(), product.getId()
                );
    }

    @DisplayName("존재하지 않는 상품은 최근 본 상품으로 저장할 수 없다.")
    @Test
    void saveRecentProductWhenNoProduct() {
        //given
        Long fakeProductId = 1L;
        User user = userService.save(createUser());

        //when //then
        assertThatThrownBy(() -> userService.saveRecentProduct(user.getId(), fakeProductId))
                .isInstanceOf(NoResultException.class)
                .hasMessage("product dosen't exist");
    }

    @DisplayName("유저의 최근 본 상품인지 확인할 수 있다.")
    @Test
    void isRecentProduct() {
        //given
        User user = userService.save(createUser());
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");
        Mall mall = mallService.save(createMall(1));
        Product product1 = productService.save(createProduct(1, topCategory, topSubCategory, mall));
        Product product2 = productService.save(createProduct(1, topCategory, topSubCategory, mall));

        recentRepository.save(createRecent(user, product1));

        //when
        boolean target = userService.isRecentProduct(user.getId(), product1.getId());
        boolean target2 = userService.isRecentProduct(user.getId(), product2.getId());

        //then
        assertThat(target).isTrue();
        assertThat(target2).isFalse();
    }

    @DisplayName("저장된 유저 정보가 입력됐다면 로그인할 수 있다.")
    @Test
    void login() {
        //given
        User user = userService.save(createUser());
        LoginDto loginDto = LoginDto.builder()
                .email(user.getEmail())
                .password("password")
                .build();

        //when
        User target = userService.login(loginDto);

        //then
        assertThat(target)
                .extracting("id", "username", "password", "email", "gender", "year", "month", "day")
                .containsExactlyInAnyOrder(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getEmail(),
                        user.getGender(),
                        user.getYear(),
                        user.getMonth(),
                        user.getDay()
                );
    }

    @DisplayName("저장되지 않은 유저 정보가 입력됐다면 로그인할 수 없다.")
    @Test
    void loginWhenInvalidUser() {
        //given
        LoginDto loginDto = LoginDto.builder()
                .email("test@email.com")
                .password("password")
                .build();

        //when
        User target = userService.login(loginDto);

        //then
        assertThat(target).isNull();
    }

    @DisplayName("해당 닉네임으로 저장한 유저가 있는지 확인한다.")
    @Test
    void isUsernameExist() {
        //given
        User user = userService.save(createUser());

        //when
        boolean target = userService.usernameExist(user.getUsername());
        boolean target2 = userService.usernameExist("notSavedUser");

        //then
        assertThat(target).isTrue();
        assertThat(target2).isFalse();
    }

    @DisplayName("해당 이메일로 저장한 유저가 있는지 확인한다.")
    @Test
    void isEmailExist() {
        //given
        User user = userService.save(createUser());

        //when
        boolean target = userService.emailExist(user.getEmail());
        boolean target2 = userService.emailExist("noUser@email.com");

        //then
        assertThat(target).isTrue();
        assertThat(target2).isFalse();
    }

    @DisplayName("임시 비밀번호를 생성할 수 있다.")
    @Test
    void getTmpPassword() {
        //given

        //when
        String target = userService.getTmpPassword();

        //then
        assertThat(target.length()).isEqualTo(10);
    }

    @DisplayName("비밀번호를 업데이트할 수 있다.")
    @Test
    void updatePassword() {
        //given
        User user = userService.save(createUser());
        String tempPassword = "temp";

        //when
        userService.updatePassword(tempPassword, user.getEmail());

        //then
        assertThat(passwordEncoder.matches(tempPassword, user.getPassword())).isTrue();
    }

    @DisplayName("비밀번호 갱신은 1시간 이내에 재시도할 수 없다.")
    @Test
    void updatePasswordTokenInHour() {
        //given
        User user = userService.save(createUser());
        LocalDateTime updatedTime = LocalDateTime.of(2023, 11, 13, 1, 0);
        LocalDateTime ReTryUpdatedTimeBeforeHour = LocalDateTime.of(2023, 11, 13, 1, 59);
        LocalDateTime ReTryUpdatedTimeAfter = LocalDateTime.of(2023, 11, 13, 2, 59);

        //when
        boolean result = userService.updatePasswordToken(user.getEmail(), updatedTime);
        boolean retryResultBeforeHour = userService.updatePasswordToken(user.getEmail(), ReTryUpdatedTimeBeforeHour);
        boolean retryResultAfterHour = userService.updatePasswordToken(user.getEmail(), ReTryUpdatedTimeAfter);

        //then
        assertThat(result).isTrue();
        assertThat(retryResultBeforeHour).isFalse();
        assertThat(retryResultAfterHour).isTrue();
    }

    @DisplayName("유저를 삭제할 수 있다.")
    @Test
    void deleteUser() {
        //given
        User user = userService.save(createUser());

        //when
        userService.delete(user.getId());

        //then
        boolean target = userRepository.findById(user.getId()).isEmpty();
        assertThat(target).isTrue();
    }

    @DisplayName("유저의 최근 본 상품 업데이트 시각을 현재 시각으로 초기화한다.")
    @Test
    void updateRecentLastInitializedAtOfUsers() {
        //given
        User user = userService.save(createUser());
        LocalDateTime updatedTime = LocalDateTime.of(2023, 11, 13, 1, 0);

        //when
        userService.updateRecentLastInitializedAtOfUsers(updatedTime);

        //then
        LocalDateTime target = userRepository.findById(user.getId()).get().getRecentLastInitializedAt();
        assertThat(target).isEqualTo(updatedTime);
    }

    @DisplayName("비슷한 체형 유저 기반 추천 상품 정보를 초기화한다.")
    @Test
    void resetUpdatedAtOfUserRecommendation() {
        //given
        User user = userService.save(createUser());
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");
        Mall mall = mallService.save(createMall(1));
        Product product1 = productService.save(createProduct(1, topCategory, topSubCategory, mall));
        Product product2 = productService.save(createProduct(1, topCategory, topSubCategory, mall));

        userRecommendationRepository.save(UserRecommendation.builder()
                .user(user)
                .product(product1)
                .build());
        userRecommendationRepository.save(UserRecommendation.builder()
                .user(user)
                .product(product2)
                .build());

        //when
        userService.resetUpdatedAtOfUserRecommendation();

        //then
        List<UserRecommendation> target = userRecommendationRepository.findByUserId(user.getId());
        assertThat(target).isEmpty();
    }

    @DisplayName("최근 본 상품 기반 추천 상품 정보를 초기화한다.")
    @Test
    void resetUpdatedAtOfRecentRecommendation() {
        //given
        User user = userService.save(createUser());
        Category topCategory = categoryService.save("top");
        SubCategory topSubCategory = categoryService.saveSubCategory("top", "shirt");
        Mall mall = mallService.save(createMall(1));
        Product product1 = productService.save(createProduct(1, topCategory, topSubCategory, mall));
        Product product2 = productService.save(createProduct(1, topCategory, topSubCategory, mall));

        recentRecommendationRepository.save(RecentRecommendation.builder()
                .user(user)
                .product(product1)
                .build());
        recentRecommendationRepository.save(RecentRecommendation.builder()
                .user(user)
                .product(product2)
                .build());

        //when
        userService.resetUpdatedAtOfRecentRecommendation();

        //then
        List<RecentRecommendation> target = recentRecommendationRepository.findByUserId(user.getId());
        assertThat(target).isEmpty();
    }

    private SignUpDto createUser() {
        return SignUpDto.builder()
                .username("testuser")
                .password("password")
                .email("test@email.com")
                .gender("M")
                .year(1990)
                .month(1)
                .day(1)
                .build();
    }

    private MallDto createMall(int number) {
        return MallDto.builder()
                .name("testMall" + number)
                .url("https://www.test-mall.com")
                .image("image.jpg")
                .description("desc")
                .view(0)
                .products(new ArrayList<>())
                .build();
    }

    private Product createProduct(int number, Category category, SubCategory subCategory, Mall mall) {
        return Product.builder()
                .price(10000)
                .name("product" + number)
                .gender("M")
                .type(0)
                .image("image.jpg")
                .view(0)
                .timeView(0)
                .origin("https://test.com/product/" + number)
                .category(category)
                .subCategory(subCategory)
                .mall(mall)
                .disabled(0)
                .build();
    }

    private Recent createRecent(User user, Product product) {
        return Recent.builder()
                .timestamp(LocalDateTime.now())
                .user(user)
                .product(product)
                .build();
    }
}