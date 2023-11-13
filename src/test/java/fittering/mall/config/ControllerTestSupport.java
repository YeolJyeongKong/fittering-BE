package fittering.mall.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import fittering.mall.config.jwt.JwtAuthenticationFilter;
import fittering.mall.config.jwt.JwtTokenProvider;
import fittering.mall.controller.*;
import fittering.mall.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(controllers = {
        CategoryController.class,
        MallController.class,
        ProductController.class,
        SearchController.class,
        SignController.class,
        SwaggerController.class,
        UserController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

    @MockBean
    protected CategoryService categoryService;

    @MockBean
    protected MallService mallService;

    @MockBean
    protected RankService rankService;

    @MockBean
    protected FavoriteService favoriteService;

    @MockBean
    protected ProductService productService;

    @MockBean
    protected UserService userService;

    @MockBean
    protected SizeService sizeService;

    @MockBean
    protected SearchService searchService;

    @MockBean
    protected S3Service s3Service;

    @MockBean
    protected RestTemplate restTemplate;

    @MockBean
    protected RedisTemplate<String, Object> redisTemplate;
}
