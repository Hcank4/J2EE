package com.example.Lab6.config;

import com.example.Lab6.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AccountService accountService;

    // Cấu hình mã hóa mật khẩu Bcrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Cấu hình phân quyền đường dẫn
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        // Các đường dẫn Thêm, Sửa, Xóa yêu cầu quyền ADMIN
                        .requestMatchers("/products/add", "/products/edit/**", "/products/delete/**").hasRole("ADMIN")
                        // Đường dẫn xem danh sách yêu cầu quyền USER hoặc ADMIN
                        .requestMatchers("/products").hasAnyRole("USER", "ADMIN")
                        // Bất kỳ yêu cầu nào khác cũng phải đăng nhập
                        .anyRequest().authenticated()
                )
                // Sử dụng form đăng nhập mặc định của Spring
                .formLogin(form -> form.defaultSuccessUrl("/products", true))
                .logout(withDefaults());

        return http.build();
    }
}