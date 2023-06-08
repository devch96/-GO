package com.hansung.capstone;

import static org.springframework.security.config.Customizer.withDefaults;

import com.hansung.capstone.user.JwtAccessDeniedHandler;
import com.hansung.capstone.user.JwtAuthenticationEntryPoint;
import com.hansung.capstone.user.JwtAuthenticationFilter;
import com.hansung.capstone.user.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers("/api/users/signup").permitAll()
                                        .requestMatchers("/api/users/signin").permitAll()
                                        .requestMatchers("/api/users/nickname/duplicate-check").permitAll()
                                        .requestMatchers("/api/users/email/duplicate-check").permitAll()
                                        .requestMatchers("/api/users/findID").permitAll()
                                        .requestMatchers("/api/community/post/list/**").permitAll()
                                        .requestMatchers("/api/community/post/list/scrap").authenticated()
                                        .requestMatchers("/api/community/post/detail").permitAll()
                                        .requestMatchers("/api/users/riding/rank").permitAll()
                                        .requestMatchers("/api/user-course/list").permitAll()
                                        .requestMatchers("/api/user-course/detail").permitAll()
                                        .requestMatchers("/api/email/send").permitAll()
                                        .requestMatchers("/api/email/confirm").permitAll()
                                        .requestMatchers("/profile-image/**").permitAll()
                                        .requestMatchers("/image/**").permitAll()
                                        .requestMatchers("/swagger-ui/**").permitAll()
                                        .requestMatchers("/v3/api-docs/**").permitAll()
                                        .requestMatchers("/api/auth/reissue").permitAll()
                                        .anyRequest().authenticated()
                )
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
                .headers()
                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                        XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}