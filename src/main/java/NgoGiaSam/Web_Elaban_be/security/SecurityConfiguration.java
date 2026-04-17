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

        http.authorizeHttpRequests(config -> config

                // ===== PUBLIC RESOURCES =====
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Static files & images
                .requestMatchers("/images/**", "/image/**", "/product_image/**",
                        "/product-images/**", "/uploads/**", "/static/**",
                        "/*.png", "/*.jpg", "/*.jpeg", "/*.gif").permitAll()

                // Auth public
                .requestMatchers("/account/login", "/account/register").permitAll()

                // REVIEWS - Cho phép public hoàn toàn (đọc + tạo review)
                .requestMatchers("/api/reviews/search/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reviews/search/**").permitAll()
                .requestMatchers("/api/reviews/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()




                // Các GET public khác
                .requestMatchers(HttpMethod.GET,
                        "/products/**",
                        "/api/products/**",
                        "/api/recommendations/**",
                        "/reviews/**",
                        "/reviews/search/**"
                ).permitAll()

                // Wishlist GET public
                .requestMatchers(HttpMethod.GET, "/api/wishlist/**").permitAll()

                // ===== YÊU CẦU ĐĂNG NHẬP =====
                .requestMatchers("/cart/**").authenticated()
                .requestMatchers("/orders/my-orders").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/wishlist/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/wishlist/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/profile/**").authenticated()

                // ===== ADMIN ONLY =====
                .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/products/**").hasAnyAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/products/**").hasAnyAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyAuthority("ROLE_ADMIN")
                .requestMatchers("/admin/categories/**", "/admin/users/**", "/admin/orders/**").hasAnyAuthority("ROLE_ADMIN")

                // Fallback
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
