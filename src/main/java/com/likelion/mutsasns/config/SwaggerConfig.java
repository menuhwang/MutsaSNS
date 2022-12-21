package com.likelion.mutsasns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.HttpAuthenticationScheme;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.List;

import static com.likelion.mutsasns.security.config.WebSecurityConfig.WHITE_LIST;

@Configuration
@EnableWebMvc
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .securitySchemes(Arrays.asList(HttpAuthenticationScheme.JWT_BEARER_BUILDER.name("JWT").build()))
                .securityContexts(Arrays.asList(securityContext()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.likelion.mutsasns"))
                .paths(PathSelectors.any())
                .build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .operationSelector(o -> !o.requestMappingPattern().matches(String.join("|", WHITE_LIST)))
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        return Arrays.asList(SecurityReference.builder()
                .scopes(new AuthorizationScope[0])
                .reference("JWT")
                .build());
    }
}
