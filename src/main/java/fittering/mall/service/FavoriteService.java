package fittering.mall.service;

import fittering.mall.controller.dto.response.ResponseMallWithProductDto;
import fittering.mall.controller.dto.response.ResponseMallRankProductDto;
import fittering.mall.controller.dto.response.ResponseProductPreviewDto;
import fittering.mall.domain.mapper.MallMapper;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import fittering.mall.domain.RestPage;
import fittering.mall.domain.entity.Favorite;
import fittering.mall.domain.entity.Mall;
import fittering.mall.domain.entity.Product;
import fittering.mall.domain.entity.User;
import fittering.mall.repository.FavoriteRepository;
import fittering.mall.repository.MallRepository;
import fittering.mall.repository.ProductRepository;
import fittering.mall.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private static final int MAX_PRODUCT_COUNT = 5;

    private final FavoriteRepository favoriteRepository;
    private final MallRepository mallRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Favorite saveFavoriteMall(Long userId, Long mallId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user doesn't exist"));
        Mall mall = mallRepository.findById(mallId)
                .orElseThrow(() -> new NoResultException("mall doesn't exist"));
        return favoriteRepository.save(Favorite.builder()
                                        .user(user)
                                        .mall(mall)
                                        .build());
    }

    @Transactional
    public void deleteFavoriteMall(Long userId, Long mallId) {
        favoriteRepository.deleteByUserIdAndMallId(userId, mallId);
    }

    public List<ResponseMallWithProductDto> userFavoriteMall(Long userId) {

        List<Favorite> favoriteMalls = favoriteRepository.userFavoriteMall(userId);
        List<ResponseMallWithProductDto> userFavoriteMallDtos = new ArrayList<>();

        favoriteMalls.forEach(favorite -> {
            Mall mall = favorite.getMall();
            List<Product> products = mall.getProducts();
            List<ResponseMallRankProductDto> productDtos = new ArrayList<>();

            getProductDtos(products, productDtos);
            userFavoriteMallDtos.add(MallMapper.INSTANCE.toResponseMallWithProductDto(mall, productDtos, 0, true));
        });
        return userFavoriteMallDtos;
    }

    @Transactional
    public Favorite saveFavoriteProduct(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user doesn't exist"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResultException("product doesn't exist"));
        return favoriteRepository.save(Favorite.builder()
                .user(user)
                .product(product)
                .build());
    }

    @Transactional
    public void deleteFavoriteProduct(Long userId, Long productId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }

    public RestPage<ResponseProductPreviewDto> userFavoriteProduct(Long userId, Pageable pageable) {
        return new RestPage<>(favoriteRepository.userFavoriteProduct(userId, pageable));
    }

    public List<ResponseProductPreviewDto> userFavoriteProductPreview(Long userId) {
        return favoriteRepository.userFavoriteProductPreview(userId);
    }

    private void getProductDtos(List<Product> products, List<ResponseMallRankProductDto> productDtos) {
        int productCount = 0;
        for (Product productProxy : products) {
            if (isEnoughProducts(productCount)) break;
            productCount++;
            Product product = productRepository.findById(productProxy.getId())
                    .orElseThrow(() -> new NoResultException("product dosen't exist"));
            productDtos.add(ResponseMallRankProductDto.builder()
                    .productId(product.getId())
                    .productImage(product.getImage())
                    .build());
        }
    }

    private static boolean isEnoughProducts(int count) {
        return count >= MAX_PRODUCT_COUNT;
    }
}
