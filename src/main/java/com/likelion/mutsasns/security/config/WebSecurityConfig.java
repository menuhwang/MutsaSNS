package com.likelion.mutsasns.security.config;

import com.likelion.mutsasns.security.entrypoint.CustomAccessDeniedEntryPoint;
import com.likelion.mutsasns.security.entrypoint.CustomAuthenticationEntryPoint;
import com.likelion.mutsasns.security.filter.JwtAuthenticationFilter;
import com.likelion.mutsasns.security.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtProvider jwtProvider;

    public static final String[] POST_AUTHENTICATED_REGEX_LIST = {
            "^/api/v1/posts$",
            "^/api/v1/posts/\\d/comments$"
    };

    public static final String[] PUT_AUTHENTICATED_REGEX_LIST = {
            "^/api/v1/posts/\\d$",
            "^/api/v1/posts/\\d/comments/\\d$"
    };

    public static final String[] DELETE_AUTHENTICATED_REGEX_LIST = {
            "^/api/v1/posts/\\d$",
            "^/api/v1/posts/\\d/comments/\\d$"
    };

    public static final String[] ADMIN_ONLY_REGEX_LIST = {
            "^/api/v1/users/\\d/role/change$"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.cors();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeHttpRequests()
                .regexMatchers(HttpMethod.POST, POST_AUTHENTICATED_REGEX_LIST).authenticated()
                .regexMatchers(HttpMethod.PUT, PUT_AUTHENTICATED_REGEX_LIST).authenticated()
                .regexMatchers(HttpMethod.DELETE, DELETE_AUTHENTICATED_REGEX_LIST).authenticated()
                .regexMatchers(ADMIN_ONLY_REGEX_LIST).hasRole("ADMIN");

        http.exceptionHandling().accessDeniedHandler(new CustomAccessDeniedEntryPoint())
                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint());

        http.addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
