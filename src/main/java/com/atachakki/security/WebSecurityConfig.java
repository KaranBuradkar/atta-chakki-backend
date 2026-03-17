package com.atachakki.security;

import com.atachakki.security.oauth2.OAuth2SuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class WebSecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);
    private final OAuth2SuccessHandler oauth2SuccessHandler;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtAuthFilter jwtAuthFilter;

    public WebSecurityConfig(
            OAuth2SuccessHandler oauth2SuccessHandler,
            HandlerExceptionResolver handlerExceptionResolver,
            JwtAuthFilter jwtAuthFilter
    ) {
        this.oauth2SuccessHandler = oauth2SuccessHandler;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity https) throws Exception {
        https.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // OAuth2 provider success or fail
                .oauth2Login(a -> {
                    a.successHandler(oauth2SuccessHandler);
                    a.failureHandler(
                            (request, response, e)
                                    -> {
                                log.warn("Oauth2 Failure: ", e);
                                response.sendRedirect("/");
                            }
                    );
                })
                // request validator
                .authorizeHttpRequests(req -> { req
                        .requestMatchers( "/", "/public/**", "/auth/register/**", "/auth/login/**"
                        ).permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/swagger-config/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated();
                })
                // Token validation filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // any occurred exception handler
                .exceptionHandling(this::resolveConfigure);

        return https.build();
    }

    private void resolveConfigure(ExceptionHandlingConfigurer<HttpSecurity> configurer) {
        configurer.accessDeniedHandler(
                (request, response, e)
                        -> handlerExceptionResolver.resolveException(request, response, null, e)
        );
    }
}
