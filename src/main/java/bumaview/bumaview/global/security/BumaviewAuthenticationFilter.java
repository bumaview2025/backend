package bumaview.bumaview.global.security;

import bumaview.bumaview.domain.user.domain.entity.UserEntity;
import bumaview.bumaview.domain.user.infra.repository.UserRepository;
import bumaview.bumaview.global.security.jwt.JwtProvider;
import bumaview.bumaview.global.security.user.BumaviewUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class BumaviewAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String accessToken = jwtProvider.getAccessToken(authorizationHeader);
                Long userId = jwtProvider.getUserIdFromToken(accessToken);

                UserEntity userEntity = userRepository.findById(userId)
                        .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

                UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(new BumaviewUserDetails(userEntity), null, userEntity.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
