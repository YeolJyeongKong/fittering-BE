package fittering.mall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fittering.mall.domain.entity.User;
import fittering.mall.repository.querydsl.UserRepositoryCustom;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderLoginId(String providerLoginId);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
}
