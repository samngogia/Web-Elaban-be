package NgoGiaSam.Web_Elaban_be.service;

import NgoGiaSam.Web_Elaban_be.dao.UserRespository;
import NgoGiaSam.Web_Elaban_be.enity.ErrorLog;
import NgoGiaSam.Web_Elaban_be.enity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AccountService {




    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRespository userRepository;

    @Autowired
    private EmailService emailService;

    public ResponseEntity<?> registerUser(User user) {
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(new ErrorLog("Username already exists."));
        }

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(new ErrorLog("Email already exists."));
        }

        // Encrypt password
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        // Assign and send activation information
        user.setActivationCode(generateActivationCode());
        user.setEnabled(false);

        // Save user to database
        User savedUser = userRepository.save(user);

        // Send activation email
        sendActivationEmail(user.getEmail(), user.getActivationCode());

        return ResponseEntity.ok("Registration successful");
    }

    private String generateActivationCode() {
        // Generate random UUID code
        return UUID.randomUUID().toString();
    }

    private void sendActivationEmail(String email, String activationCode) {
        String subject = "Activate your account at ElaBan";
        String text = "<html><body>"
                + "Please use the following code to activate your account for <b>" + email + "</b>:"
                + "<br/><h1>" + activationCode + "</h1>"
                + "</body></html>";

        text += "<br/> Click on the link below to activate your account: ";

        // URL updated to match the English endpoints we set earlier
        String url = "http://localhost:3000/activate/" + email + "/" + activationCode;

        text += ("<br/> <a href='" + url + "'>" + url + "</a>");

        // Note: Replace the sender email with your configured system email
        emailService.sendMessage("ngogiasamc3@gmail.com", email, subject, text);
    }

    public ResponseEntity<?> activateAccount(String email, String activationCode) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(new ErrorLog("User does not exist!"));
        }

        if (user.isEnabled()) {
            return ResponseEntity.badRequest().body(new ErrorLog("Account is already activated!"));
        }

        if (activationCode.equals(user.getActivationCode())) {
            user.setEnabled(true);
            userRepository.save(user);
            return ResponseEntity.ok("Account activated successfully!");
        } else {
            return ResponseEntity.badRequest().body(new ErrorLog("Incorrect activation code!"));
        }
    }

    // Gửi OTP reset mật khẩu
    public ResponseEntity<?> forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(new ErrorLog("Email không tồn tại!"));
        }

        // Tạo OTP 6 số
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        user.setResetOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10)); // hết hạn sau 10 phút
        userRepository.save(user);

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
        User user = userRepository.findByEmail(email).orElse(null);

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
        userRepository.save(user);

        return ResponseEntity.ok("Đặt lại mật khẩu thành công!");
    }
}
