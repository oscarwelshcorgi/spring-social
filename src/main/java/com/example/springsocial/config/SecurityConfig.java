package com.example.springsocial.config;

import com.example.springsocial.config.oauth.CustomOAuth2UserService;
import com.example.springsocial.domain.eums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig { //WebSecurityConfigurerAdapter was deprecated

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  //기본값을 유지하면서 CSRF 보호를 비활성화
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .formLogin(formLogin -> formLogin.disable()) //폼 기반 로그인을 비활성화
                .httpBasic(httpBasic -> httpBasic.disable()) //HTTP 기본 인증을 비활성화
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                            .requestMatchers("/", "/index", "/*", "/..js/**").permitAll() // URL 별 권한 관리
                            .requestMatchers("/api/v1/**").hasRole(Role.USER.name()) // /api/v1/** 은 USER 권한만 접근 가능
                            .anyRequest().authenticated() // anyRerquest : 설정된 값들 이외 나머지 URL 나타냄. authenticated : 인증된 사용자
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login.userInfoEndpoint(userInfoEndpointConfig -> // oauth2 로그인 성공 후 가져올 때의 설정들
                                userInfoEndpointConfig.userService(customOAuth2UserService) // 리소스 서버에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능 명시
                        )
                )
                .logout((logout) -> logout.logoutSuccessUrl("/"))
        ;
        return http.build();
    }
}