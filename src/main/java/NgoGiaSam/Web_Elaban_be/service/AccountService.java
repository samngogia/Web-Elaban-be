package NgoGiaSam.Web_Elaban_be.service;

import NgoGiaSam.Web_Elaban_be.dao.RoleRespository;
import NgoGiaSam.Web_Elaban_be.dao.UserRespository;
import NgoGiaSam.Web_Elaban_be.enity.ErrorLog;
import NgoGiaSam.Web_Elaban_be.enity.Role;
import NgoGiaSam.Web_Elaban_be.enity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class AccountService {




    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRespository userRespository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RoleRespository roleRepository;

    @Autowired
    private JavaMailSender mailSender;


    @Transactional
    public ResponseEntity<?> registerUser(User user) {
        // Kiểm tra username/email tồn tại
        if (userRespository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại!");
        }
        if (userRespository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã được sử dụng!");
        }

        // Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);

        // Tạo OTP 6 số thay vì UUID
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setActivationCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));

        // Gán role USER
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(List.of(userRole));

        userRespository.save(user);

        // Gửi OTP qua email
        sendOtpEmail(user.getEmail(), user.getUsername(), otp);

        return ResponseEntity.ok("Đăng ký thành công! Vui lòng kiểm tra email để lấy mã OTP.");
    }

    private String generateActivationCode() {
        // Generate random UUID code
        return UUID.randomUUID().toString();
    }

    private void sendOtpEmail(String email, String username, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("ElaBan - Mã xác thực tài khoản");
        message.setText(
                "Xin chào " + username + ",\n\n" +
                        "Mã OTP xác thực tài khoản của bạn là:\n\n" +
                        "        " + otp + "\n\n" +
                        "Mã có hiệu lực trong 10 phút.\n" +
                        "Vui lòng không chia sẻ mã này với ai.\n\n" +
                        "Trân trọng,\nElaBan"
        );
        mailSender.send(message);
    }

    public ResponseEntity<?> activateAccount(String email, String otp) {
        User user = userRespository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("Email không tồn tại!");
        }
        if (user.isEnabled()) {
            return ResponseEntity.ok("Tài khoản đã được kích hoạt trước đó!");
        }
        if (!otp.equals(user.getActivationCode())) {
            return ResponseEntity.badRequest().body("Mã OTP không đúng!");
        }

        user.setEnabled(true);
        user.setActivationCode(null);
        user.setOtpExpiry(null);
        userRespository.save(user);


        return ResponseEntity.ok("Kích hoạt tài khoản thành công!");
    }

    public ResponseEntity<?> resendOtp(String email) {
        User user = userRespository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body("Email không tồn tại!");
        if (user.isEnabled()) return ResponseEntity.badRequest().body("Tài khoản đã kích hoạt!");

        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setActivationCode(otp);
        userRespository.save(user);
        sendOtpEmail(email, user.getUsername(), otp);

        return ResponseEntity.ok("Đã gửi lại OTP!");
    }
    // Gửi OTP reset mật khẩu
    public ResponseEntity<?> forgotPassword(String email) {
        User user = userRespository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(new ErrorLog("Email không tồn tại!"));
        }

        // Tạo OTP 6 số
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        user.setResetOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10)); // hết hạn sau 10 phút
        userRespository.save(user);

        // Gửi email
        String subject = "Mã OTP đặt lại mật khẩu - ElaBan";
        String text = "<html><body>"
                + "<p>Mã OTP đặt lại mật khẩu của bạn là:</p>"
                + "<h1 style='letter-spacing:8px;'>" + otp + "</h1>"
                + "<p>Mã có hiệu lực trong <b>10 phút</b>.</p>"
                + "<p>Nếu bạn không yêu cầu, hãy bỏ qua email này.</p>"
                + "</body></html>";

        emailService.sendMessage("ngogiasamc3@gmail.com", email, subject, text);
        return ResponseEntity.ok("OTP đã được gửi đến email của bạn!");
    }

    // Xác nhận OTP và đổi mật khẩu
    public ResponseEntity<?> resetPassword(String email, String otp, String newPassword) {
        User user = userRespository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(new ErrorLog("Email không tồn tại!"));
        }

        if (user.getResetOtp() == null || !user.getResetOtp().equals(otp)) {
            return ResponseEntity.badRequest().body(new ErrorLog("OTP không đúng!"));
        }

        if (user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            return ResponseEntity.badRequest().body(new ErrorLog("OTP đã hết hạn!"));
        }

        // Đổi mật khẩu
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetOtp(null);
        user.setOtpExpiry(null);
        userRespository.save(user);

        return ResponseEntity.ok("Đặt lại mật khẩu thành công!");
    }
}
