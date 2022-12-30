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

    private final String[] SWAGGER = {
            "/v3/api-docs",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger/**"
    };

    public static final String[] GET_WHITE_LIST = {
            "/api/v1/posts/**"
    };

    public static final String[] WHITE_LIST = {
            "/api/v1/hello/**",
            "/api/v1/users/login",
            "/api/v1/users/join"
    };

    public static final String[] ADMIN_ONLY = {
            "/api/v1/users/*/role/change"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.cors();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeHttpRequests()
                .antMatchers(SWAGGER).permitAll() // swagger 시큐리티 제한 해제 설정
                .antMatchers(WHITE_LIST).permitAll()
                .antMatchers(HttpMethod.GET, GET_WHITE_LIST).permitAll()
                .antMatchers(ADMIN_ONLY).hasRole("ADMIN")
                .anyRequest().authenticated();

        http.exceptionHandling().accessDeniedHandler(new CustomAccessDeniedEntryPoint())
            .and()
            .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint());

        http.addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
