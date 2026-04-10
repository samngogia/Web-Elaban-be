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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();

        if (path.equals("/api/chat")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extracUsername(token);

        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(username);

            if (jwtService.validateToken(token, userDetails)) {
                // Cách 1: Đọc trực tiếp danh sách roles từ Claim (Linh hoạt nhất)
                List<String> roles = jwtService.extractClaim(token, claims ->
                        (List<String>) claims.get("roles")
                );

                List<GrantedAuthority> authorities = new ArrayList<>();

                if (roles != null) {
                    roles.forEach(role -> {
                        String authority = role.startsWith("ROLE_")
                                ? role
                                : "ROLE_" + role;

                        authorities.add(new SimpleGrantedAuthority(authority));
                    });
                } else {
                    Boolean isAdmin = jwtService.extractClaim(
                            token,
                            claims -> claims.get("isAdmin", Boolean.class)
                    );

                    authorities.add(new SimpleGrantedAuthority(
                            isAdmin ? "ROLE_ADMIN" : "ROLE_USER"
                    ));
                }
                System.out.println("Roles from token = " + roles);
                System.out.println("Authorities = " + authorities);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }}