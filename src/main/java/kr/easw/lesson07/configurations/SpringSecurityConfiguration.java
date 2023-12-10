package kr.easw.lesson07.configurations;

import kr.easw.lesson07.Constants;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// 스프링 웹 시큐리티를 활성화합니다.
@EnableWebSecurity
// Configuration 어노테이션을 사용하여 해당 클래스가 스프링 설정 클래스임을 선언합니다.
@Configuration
public class SpringSecurityConfiguration {
    // 어플리케이션에 사용할 비밀번호 인코더를 미리 생성합니다.
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // @Bean 어노테이션을 사용하여 해당 메서드가 스프링 빈임을 선언합니다.
    @Bean
    // @SneakyThrows를 사용하여 예외를 무시합니다.
    // 이는 lombok의 API입니다.
    @SneakyThrows
    // HttpSecurity를 파라미터로 받아 SecurityFilterChain을 반환하는 메서드입니다.
    SecurityFilterChain configureHttpSecurity(HttpSecurity security) {

        security
                // csrf 보호를 비활성화합니다.
                // 활성화되었을 경우, 페이지 내의 API 호출이 실패할 수 있습니다.
                .csrf(AbstractHttpConfigurer::disable)
                // 모든 요청에 대해 인증을 넘깁니다.
                // 원래대로라면 권한 설정을 해야하나, 이는 다음 실습에서 진행합니다.
                .authorizeHttpRequests(registry -> {
                    registry.anyRequest().permitAll();
                })
                // 로그아웃 엔드포인트를 설정합니다.
                .logout(customizer -> {
                    customizer.logoutUrl("/logout");
                })
                // 로그인 엔드포인트를 설정합니다.
                .formLogin(customizer -> {
                    customizer
                            // 로그인 페이지를 /login으로 설정합니다.
                            .loginProcessingUrl("/login")
                            // 로그인 성공시 리다이렉트할 페이지를 설정합니다.
                            .defaultSuccessUrl("/")
                            // 로그인 실패시 리다이렉트할 페이지를 설정합니다.
                            .failureUrl("/login?error=true")
                    ;
                })
        ;


        return security.build();
    }

    // @Bean 어노테이션을 사용하여 해당 메서드가 스프링 빈임을 선언합니다.
    @Bean
    public UserDetailsService provideUsers() {
        // 디버그를 위해 메모리에 저장되는 계정을 설정합니다.
        return new InMemoryUserDetailsManager(
                // guest 계정을 생성합니다.
                User.withUsername("guest").password(encoder().encode("guest")).authorities(Constants.AUTHORITY_GUEST).build(),
                // admin 계정을 생성합니다.
                User.withUsername("admin").password(encoder().encode("admin")).authorities(Constants.AUTHORITY_ADMIN).build()
        );
    }

    // @Bean 어노테이션을 사용하여 해당 메서드가 스프링 빈임을 선언합니다.
    @Bean
    public BCryptPasswordEncoder encoder() {
        // 사용되는 비밀번호 인코더를 BCrypt로 설정합니다.
        return encoder;
    }

}
