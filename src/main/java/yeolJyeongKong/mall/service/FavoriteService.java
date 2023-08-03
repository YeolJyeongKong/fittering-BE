package yeolJyeongKong.mall.service;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import yeolJyeongKong.mall.domain.RestPage;
import yeolJyeongKong.mall.domain.dto.MallDto;
import yeolJyeongKong.mall.domain.dto.MallRankProductDto;
import yeolJyeongKong.mall.domain.dto.ProductPreviewDto;
import yeolJyeongKong.mall.domain.entity.Favorite;
import yeolJyeongKong.mall.domain.entity.Mall;
import yeolJyeongKong.mall.domain.entity.Product;
import yeolJyeongKong.mall.domain.entity.User;
import yeolJyeongKong.mall.repository.FavoriteRepository;
import yeolJyeongKong.mall.repository.MallRepository;
import yeolJyeongKong.mall.repository.ProductRepository;
import yeolJyeongKong.mall.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MallRepository mallRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Cacheable(value = "UserFavoriteMall", key = "#userId")
    public List<MallDto> userFavoriteMall(Long userId) {

        List<Favorite> favoriteMalls = favoriteRepository.userFavoriteMall(userId);
        List<MallDto> result = new ArrayList<>();

        for (Favorite favorite : favoriteMalls) {

            Mall mall = favorite.getMall();
            List<Product> products = mall.getProducts();
            List<MallRankProductDto> productDtos = new ArrayList<>();

            int productCount = 0;
            for (Product productProxy : products) {
                if(productCount++ == 5) break;
                Product product = productRepository.findById(productProxy.getId())
                        .orElseThrow(() -> new NoResultException("product dosen't exist"));
                productDtos.add(new MallRankProductDto(product.getId(), product.getImage()));
            }

            result.add(new MallDto(mall.getId(), mall.getName(), mall.getUrl(), mall.getImage(),
                    mall.getDescription(), 0, productDtos));
        }

        return result;
    }

    @Transactional
    public Favorite saveFavoriteMall(Long userId, Long mallId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user doesn't exist"));

        Mall mall = mallRepository.findById(mallId)
                .orElseThrow(() -> new NoResultException("mall doesn't exist"));

        return favoriteRepository.save(new Favorite(user, mall));
    }

    @CacheEvict(value = "UserFavoriteMall", key = "#userId")
    @Transactional
    public void deleteFavoriteMall(Long userId, Long mallId) {
        favoriteRepository.deleteByUserIdAndMallId(userId, mallId);
    }

    public RestPage<ProductPreviewDto> userFavoriteProduct(Long userId, Pageable pageable) {
        return new RestPage<>(favoriteRepository.userFavoriteProduct(userId, pageable));
    }

    @Transactional
    public Favorite saveFavoriteProduct(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("user doesn't exist"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResultException("product doesn't exist"));

        return favoriteRepository.save(new Favorite(user, product));
    }

    @Transactional
    public void deleteFavoriteProduct(Long userId, Long productId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }
}
