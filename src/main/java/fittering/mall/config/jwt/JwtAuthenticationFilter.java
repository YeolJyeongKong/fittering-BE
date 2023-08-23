package fittering.mall.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

import static fittering.mall.config.AcceptedUrl.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        //헤더에서 토큰 가져오기
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        if (token == null) {
            String requestURI = ((HttpServletRequest) request).getRequestURI();

            for (String acceptedUrl : ACCEPTED_URL_LIST) {
                if (requestURI.equals(acceptedUrl)) {
                    chain.doFilter(request, response);
                    return;
                }
            }

            if (isNoAuthUrl(requestURI)) {
                chain.doFilter(request, response);
                return;
            }

            throw new JwtException("토큰이 빈 값입니다.");
        }

        if (jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token); //유저 정보
            SecurityContextHolder.getContext().setAuthentication(authentication); //SecurityContext에 객체 저장
        }

        //다음 Filter 실행
        chain.doFilter(request, response);
    }

    private static boolean isNoAuthUrl(String requestURI) {
        if (requestURI.startsWith(NO_AUTH_URL)) {
            return !requestURI.startsWith(AUTH_URL);
        }
        return false;
    }
}