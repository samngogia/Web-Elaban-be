package NgoGiaSam.Web_Elaban_be.security;

import NgoGiaSam.Web_Elaban_be.filter.JwtFilter;
import NgoGiaSam.Web_Elaban_be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
public class SecurityConfiguration {


    @Autowired
    private JwtFilter jwtFilter;


    // mã hóa mật khẩu
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // authentication provider

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {

        DaoAuthenticationProvider dap = new DaoAuthenticationProvider();
        dap.setUserDetailsService(userService);
        dap.setPasswordEncoder(passwordEncoder());
        return dap;
    }

    // phân quyền
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//        http.authorizeHttpRequests(config -> config
//
//                // ===== PUBLIC RESOURCES =====
//                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//
//                // Static files & images
//                .requestMatchers("/images/**", "/image/**", "/product_image/**",
//                        "/product-images/**", "/uploads/**", "/static/**",
//                        "/*.png", "/*.jpg", "/*.jpeg", "/*.gif").permitAll()
//
//                // Auth public
//                .requestMatchers("/account/login", "/account/register").permitAll()
//
//                // REVIEWS - Cho phép public hoàn toàn (đọc + tạo review)
//                .requestMatchers("/api/reviews/search/**").permitAll()
////                .requestMatchers(HttpMethod.GET, "/api/reviews/search/**").permitAll()
//                .requestMatchers("/api/reviews/**").permitAll()
////                .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
//                .requestMatchers(HttpMethod.POST, "/api/voucher/apply").permitAll()
//
////                        .requestMatchers(HttpMethod.GET, "/api/blog/posts/*/comments").permitAll()
//                        .requestMatchers("/api/blog/admin/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")
//                // Các GET public khác
//                .requestMatchers(HttpMethod.GET,
//                        "/products/**",
//                        "/api/products/**",
//                        "/api/recommendations/**",
//                        "/reviews/**",
//                        "/reviews/search/**",
//                        "/api/blog/**"
//                ).permitAll()
//
//                // Wishlist GET public
//                .requestMatchers(HttpMethod.GET, "/api/wishlist/**").permitAll()
//
//                // ===== YÊU CẦU ĐĂNG NHẬP =====
//                .requestMatchers("/cart/**").authenticated()
//                .requestMatchers("/orders/my-orders").authenticated()
//                .requestMatchers("/api/addresses/**").authenticated()
//                .requestMatchers(HttpMethod.POST, "/api/wishlist/**").authenticated()
//                .requestMatchers(HttpMethod.DELETE, "/api/wishlist/**").authenticated()
//                .requestMatchers(HttpMethod.PUT, "/api/profile/**").authenticated()
//
//                        .requestMatchers(HttpMethod.POST, "/api/blog/posts/*/comments").authenticated()
//
//                // ===== ADMIN ONLY =====
//                .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN")
//                .requestMatchers(HttpMethod.POST, "/products/**").hasAnyAuthority("ROLE_ADMIN")
//                .requestMatchers(HttpMethod.PATCH, "/products/**").hasAnyAuthority("ROLE_ADMIN")
//                .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyAuthority("ROLE_ADMIN")
//                .requestMatchers("/admin/categories/**", "/admin/users/**", "/admin/orders/**").hasAnyAuthority("ROLE_ADMIN")
//// Admin
//                .requestMatchers("/api/voucher/admin/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")
//                        .requestMatchers(HttpMethod.POST, "/api/voucher/use/**").authenticated()
//
//                .anyRequest().authenticated()
//        );
        http.authorizeHttpRequests(config -> config

                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ===== PUBLIC (Khách vãng lai) =====
                .requestMatchers("/images/**", "/uploads/**", "/static/**",
                        "/product_image/**", "/*.png", "/*.jpg").permitAll()
                .requestMatchers("/account/register", "/account/login",
                        "/account/activate", "/account/forgot-password",
                        "/account/reset-password").permitAll()
                .requestMatchers(HttpMethod.GET,
                        "/products/**", "/api/products/**",
                        "/api/recommendations/**",
                        "/reviews/**", "/reviews/search/**",
                        "/api/reviews/**", "/api/reviews/search/**",
                        "/api/blog/**",
                        "/payment_method/**", "/shipping_method/**"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/voucher/apply").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/wishlist/check").permitAll()

                // ===== USER đã đăng nhập =====
                .requestMatchers("/cart/**").authenticated()
                .requestMatchers("/order/**").authenticated()
                .requestMatchers("/api/addresses/**").authenticated()
                .requestMatchers("/api/wishlist/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/profile/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/profile/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/reviews").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/blog/posts/*/comments").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/voucher/use/**").authenticated()

                // ===== STAFF (+ ADMIN có hết) =====
                .requestMatchers(
                        "/admin/products/**",
                        "/admin/categories/**",
                        "/admin/orders/**",
                        "/admin/reviews/**",
                        "/api/blog/admin/**"
                ).hasAnyAuthority("ROLE_STAFF", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/products/**")
                .hasAnyAuthority("ROLE_STAFF", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/products/**")
                .hasAnyAuthority("ROLE_STAFF", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/products/**")
                .hasAnyAuthority("ROLE_STAFF", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/products/**")
                .hasAnyAuthority("ROLE_STAFF", "ROLE_ADMIN")

                // ===== ADMIN only =====
                .requestMatchers("/admin/users/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/admin/dashboard/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/voucher/admin/**").hasAuthority("ROLE_ADMIN")

                // Fallback admin/**
                .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_STAFF")

                .anyRequest().authenticated()
        );

        // ✅ CORS
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration corsConfig = new CorsConfiguration();
            corsConfig.addAllowedOrigin(Endpoints.FRONT_END_HOST);
            corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
            return corsConfig;
        }));

        // ✅ JWT
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // ✅ Stateless
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
