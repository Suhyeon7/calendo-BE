package com.dna.calendo.config.auth;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import com.dna.calendo.google.Role;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(
                        (csrfConfig) -> csrfConfig.disable()
                )
                .headers(
                        (headerConfig) -> headerConfig.frameOptions(
                                frameOptionsConfig -> frameOptionsConfig.disable()
                        )
                )
                .authorizeHttpRequests((authorizeRequest) -> authorizeRequest
                        .requestMatchers("/posts/new", "/comments/save").hasRole(Role.USER.name())
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/login/*", "/oauth2/**","/logout/*", "/posts/**","/login/oauth2/**" , "/comments/**").permitAll()
                        .requestMatchers("/login/oauth2/code/google").permitAll()

                        //.requestMatchers("/", "/css/**", "/images/**", "/js/**", "/oauth2/**").permitAll()

                        .anyRequest().authenticated()
                )
//                .logout( // 로그아웃 성공 시 / 주소로 이동
//                        (logoutConfig) -> logoutConfig.logoutSuccessUrl("/")
//                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")  // 로그아웃 성공 시 루트로 리다이렉트
                        .invalidateHttpSession(true)  // 로그아웃 시 세션 무효화
                        .deleteCookies("JSESSIONID")  // 세션 쿠키 삭제
                )


                // OAuth2 로그인 기능에 대한 여러 설정
                //.oauth2Login(Customizer.withDefaults()); // 아래 코드와 동일한 결과
                .oauth2Login(oauth -> oauth
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                                .defaultSuccessUrl("/", true)  // 로그인 성공 시 루트로 리디렉션
                );

        //.oauth2Login(Customizer.withDefaults()); // 아래 코드와 동일한 결과

        /*
                .oauth2Login(
                        (oauth) ->
                            oauth.userInfoEndpoint(
                                    (endpoint) -> endpoint.userService(customOAuth2UserService)
                            )
                );
        */

        return http.build();
    }

}
