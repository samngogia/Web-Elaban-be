package NgoGiaSam.Web_Elaban_be.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

// Nhớ import Entity/DTO Order của bạn vào đây
// import NgoGiaSam.Web_Elaban_be.entity.Order;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    // 1. Tiêm thêm công cụ render HTML của Thymeleaf
    @Autowired
    private TemplateEngine templateEngine;

    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    // Hàm gửi text/OTP cũ của bạn (Giữ nguyên không đổi)
    @Override
    public void sendMessage(String from, String to, String subject, String text) {
        MimeMessage message = emailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text,true); // Tham số true này chính là chìa khóa để hiển thị HTML!
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        emailSender.send(message);
    }

    // 2. Thêm hàm mới chuyên để gửi mail xác nhận đơn hàng
    @Override
    public void sendOrderConfirmationEmail(String to, Object order) { // Đổi Object thành class Order thực tế của bạn
        // Nạp dữ liệu vào Context
        Context context = new Context();
        context.setVariable("order", order);

        // Chuyển đổi file "order-confirmation.html" thành chuỗi String HTML
        String htmlContent = templateEngine.process("order-confirmation", context);

        // Gọi lại chính hàm sendMessage có sẵn ở trên để gửi đi!
        String subject = "Xác nhận đơn hàng từ ElaBan";
        String from = "no-reply@elaban.com"; // Thay bằng email hệ thống của bạn

        sendMessage(from, to, subject, htmlContent);
    }
}