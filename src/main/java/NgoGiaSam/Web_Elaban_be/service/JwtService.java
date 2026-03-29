package NgoGiaSam.Web_Elaban_be.service;
import NgoGiaSam.Web_Elaban_be.enity.Role;
import NgoGiaSam.Web_Elaban_be.enity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;


@Service
public class JwtService {
    public static final String SERECT = "638CBE3A9AA3A03AD57321D21D129ED23A45C380453A1B2A123A45C67890ABCDEF";

    //Tạo JWT dựa trên tên đăng nhập

    @Autowired
    private UserService userService;

    public String generateToken(String username){
        Map<String,Object> claims = new HashMap<>();

        User user = userService.findByUsername(username);

        boolean isAdmin = false;
        boolean isStaff = false;
        boolean isCustomer = false;

        if(user != null && user.getRoles().size()>0){
            List<Role> list =   user.getRoles();
            for(Role role : list){
                if(role.getName().equals("ADMIN")){
                    isAdmin = true;
                }
                if(role.getName().equals("STAFF")){
                    isStaff = true;
                }if(role.getName().equals("CUSTOMER")){
                    isCustomer = true;
                }
            }
        }
        claims.put("isAdmin", isAdmin);
        claims.put("isStaff", isStaff);
        claims.put("isCustomer", isCustomer);
        claims.put("userId", user != null ? user.getId() : null);


        return createToken(claims,username);

    }


    //Tạo JWT với các claim đã chọn
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+100*24*60*60*100)) // JWT hết trong 30 ngày
                .signWith(getSigneKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    //lấy serect key

    private Key getSigneKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SERECT);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    private Claims extraAllClaims(String token){
        return Jwts.parser().setSigningKey(getSigneKey()).parseClaimsJws(token).getBody();
    }

    //trích xuất thông tin cho 1 claim

    public <T> T extractClaim(String token, Function<Claims,T> claimsTFunction){
        final Claims claims = extraAllClaims(token);
        return claimsTFunction.apply(claims);
    }


    //kiểm tra thời gian hết hạn từ JWT

    public  Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }


    public  String extracUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }


    // Kiểm tra cái JWT đã hết hạn

    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extracUsername(token);
        return (username.equals(userDetails.getUsername())&&!isTokenExpired(token));
    }


}
