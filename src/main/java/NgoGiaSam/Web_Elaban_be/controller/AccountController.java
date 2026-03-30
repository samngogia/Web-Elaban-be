package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.enity.User;
import NgoGiaSam.Web_Elaban_be.security.JwtResponse;
import NgoGiaSam.Web_Elaban_be.security.LoginRequest;
import NgoGiaSam.Web_Elaban_be.service.AccountService;
import NgoGiaSam.Web_Elaban_be.service.JwtService;
import NgoGiaSam.Web_Elaban_be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins = "http://localhost:3000")
public class AccountController {
    @Autowired
    private AccountService accountService;



    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;



    @CrossOrigin(origins =  "http://localhost:3000")

    @PostMapping("/register")//dang-ky
    public ResponseEntity<?> registerUser(@Validated @RequestBody User user){
        ResponseEntity<?> response= accountService.registerUser(user);
        return response;
    }



    @GetMapping("/activate") //kich-hoat

    public ResponseEntity<?> activateAccount(@RequestParam String email, @RequestParam String maKickHoat){
        ResponseEntity<?> response= accountService.activateAccount(email,maKickHoat);
        return response;
    }


    @PostMapping("/login")//dang-nhap
    public ResponseEntity<?> dangNhap(@RequestBody LoginRequest loginRequest){
        //xác thực người dùng bằng tên đăng nhặp và mật khẩu
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword())
            );
            //Nêếu ác thực thành công, tạo token JWT
            if(authentication.isAuthenticated()){
                final  String jwt = jwtService.generateToken(loginRequest.getUsername());
                return ResponseEntity.ok(new JwtResponse(jwt));
            }
        }catch(AuthenticationException e){
            //xác thực không thành công, tr về lõi hoawvjw thông báo
            return ResponseEntity.badRequest().body("Tên đăng nhập hoặc mật khâu không đúng");

        }
        return ResponseEntity.badRequest().body("Xác thực không thành công!");
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        return accountService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword) {
        return accountService.resetPassword(email, otp, newPassword);
    }
}
