package fittering.mall.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.util.StringUtils;

import static fittering.mall.domain.entity.QCategory.category;
import static fittering.mall.domain.entity.QFavorite.favorite;
import static fittering.mall.domain.entity.QMall.mall;
import static fittering.mall.domain.entity.QProduct.product;
import static fittering.mall.domain.entity.QRecent.recent;
import static fittering.mall.domain.entity.QRecentRecommendation.recentRecommendation;
import static fittering.mall.domain.entity.QSubCategory.subCategory;
import static fittering.mall.domain.entity.QUser.user;
import static fittering.mall.domain.entity.QUserRecommendation.userRecommendation;

public class EqualMethod {

    public static BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.id.eq(userId) : null;
    }

    public static BooleanExpression productIdEq(Long productId) {
        return productId != null ? product.id.eq(productId) : null;
    }

    /**
     * @param mallId
     * null     : 쇼핑몰 구분X
     * not null : 특정 쇼핑몰
     */
    public static BooleanExpression mallIdEq(Long mallId) {
        return mallId != null ? mall.id.eq(mallId) : Expressions.asBoolean(true).isTrue();
    }

    public static BooleanExpression categoryIdEq(Long categoryId) {
        return categoryId != null ? category.id.eq(categoryId) : null;
    }

    public static BooleanExpression recentIdEq(Long recentId) {
        return recentId != null ? recent.id.eq(recentId) : null;
    }

    public static BooleanExpression subCategoryIdEq(Long subCategoryId) {
        return subCategoryId != null ? subCategory.id.eq(subCategoryId) : null;
    }

    public static BooleanExpression favoriteIdEq(Long favoriteId) {
        return favoriteId != null ? favorite.id.eq(favoriteId) : null;
    }

    public static BooleanExpression recentRecommendationIdEq(Long recentRecommendationId) {
        return recentRecommendationId != null ? recentRecommendation.id.eq(recentRecommendationId) : null;
    }

    public static BooleanExpression userRecommendationIdEq(Long userRecommendationId) {
        return userRecommendationId != null ? userRecommendation.id.eq(userRecommendationId) : null;
    }

    public static BooleanExpression mallNameEq(String mallName) {
        return StringUtils.hasText(mallName) ? mall.name.eq(mallName) : null;
    }

    /**
     * @param gender
     * 'M', 'F' : 남성, 여성
     * else     : 성별 무관
     */
    public static BooleanExpression genderEq(String gender) {
        if(gender.equals("M") || gender.equals("F")) {
            return product.gender.eq(gender);
        }
        return Expressions.asBoolean(true).isTrue();
    }

    public static BooleanExpression productNameContains(String productName) {
        return StringUtils.hasText(productName) ? product.name.contains(productName) : null;
    }
}
