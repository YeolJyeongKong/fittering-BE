package yeolJyeongKong.mall.config.auth;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeolJyeongKong.mall.domain.entity.User;
import yeolJyeongKong.mall.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        //이메일 체크
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
            return new PrincipalDetails(user);
        } else {
            // DB에 정보가 존재하지 않으므로 exception 호출
            throw new UsernameNotFoundException("user doesn't exist, email : " + email);
        }
    }
}