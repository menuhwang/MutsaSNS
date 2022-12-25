package com.likelion.mutsasns.support.annotation;

import com.likelion.mutsasns.security.config.WebSecurityConfig;
import com.likelion.mutsasns.security.entrypoint.CustomAccessDeniedEntryPoint;
import com.likelion.mutsasns.security.entrypoint.CustomAuthenticationEntryPoint;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WebMvcTest
@ImportAutoConfiguration(WebSecurityConfig.class)
@Import({CustomAuthenticationEntryPoint.class, CustomAccessDeniedEntryPoint.class})
public @interface WebMvcTestWithSecurity {
    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
    Class<?>[] controllers() default {};
}
