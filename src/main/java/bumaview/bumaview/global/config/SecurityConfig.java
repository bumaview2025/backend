package bumaview.bumaview.global.config;

import bumaview.bumaview.domain.user.domain.entity.Role;
import bumaview.bumaview.domain.user.infra.repository.UserRepository;
import bumaview.bumaview.global.security.BumaviewAuthenticationFilter;
import bumaview.bumaview.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // 모두 접근 가능
                        .requestMatchers(HttpMethod.GET, "/auth/link").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers("/login/oauth2/code/**").permitAll()
                        
                        // 사용자, 운영자 모두 접근 가능 (인증 필요)
                        .requestMatchers(HttpMethod.DELETE, "/auth/logout").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/questions").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/interview/portfolio").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/interview").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/interview").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/interview/answerlist").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/questions").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                        
                        // 운영자만 접근 가능
                        .requestMatchers(HttpMethod.GET, "/questions/sample").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/questions/file").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PATCH, "/questions").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/questions").hasRole(Role.ADMIN.name())
                        
                        .anyRequest().authenticated()
                )
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new BumaviewAuthenticationFilter(jwtProvider, userRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
