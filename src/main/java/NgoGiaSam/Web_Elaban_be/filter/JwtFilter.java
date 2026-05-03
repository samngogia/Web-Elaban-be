package NgoGiaSam.Web_Elaban_be.filter;

import NgoGiaSam.Web_Elaban_be.service.JwtService;
import NgoGiaSam.Web_Elaban_be.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {



    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        System.out.println(">>> JWT Filter called for path: " + path);   // <--- thêm dòng này để debug

// ✅ SKIP account API (login, register, activate...)
        if (path != null && path.startsWith("/account/") || path.startsWith("/users/search")) {
            System.out.println(">>> JWT Filter: SKIPPING account path: " + path);
            filterChain.doFilter(request, response);
            return;
        }
        // BỎ QUA HOÀN TOÀN cho tất cả review
        if (path != null && path.contains("/reviews")) {        // <--- thay đổi điều kiện
            System.out.println(">>> JWT Filter: SKIPPING review path: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        // Chỉ xử lý JWT cho các path cần authentication
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;
        String username = null;

        try {
            token = authHeader.substring(7);
            username = jwtService.extracUsername(token);
        } catch (Exception e) {
            System.out.println(">>> JWT Filter: Token invalid or expired: " + e.getMessage());
        }

        if (token != null && username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                UserDetails userDetails = userService.loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails)) {
                    List<String> roles = null;
                    try {
                        roles = jwtService.extractClaim(token, claims -> (List<String>) claims.get("roles"));
                    } catch (Exception ignored) {}

                    List<GrantedAuthority> authorities = new ArrayList<>();
                    if (roles != null && !roles.isEmpty()) {
                        for (String role : roles) {
                            String formatted = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                            authorities.add(new SimpleGrantedAuthority(formatted));
                        }
                    } else {
                        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    }

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                System.out.println(">>> JWT Filter: Authentication error: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    // Hàm này quyết định path nào KHÔNG cần JWT
    private boolean isPublicPath(String path) {
        if (path == null) return false;

        return path.startsWith("/api/reviews") ||
                path.startsWith("/reviews") ||
                path.startsWith("/api/products") ||
                path.startsWith("/products") ||
                path.startsWith("/images") ||
                path.startsWith("/product_image") ||
                path.startsWith("/uploads") ||
                path.startsWith("/static") ||
                path.startsWith("/account/") ||
                path.startsWith("/api/reviews") ||
                path.startsWith("/reviews") ||
                path.startsWith("/api/chat");
    }
}