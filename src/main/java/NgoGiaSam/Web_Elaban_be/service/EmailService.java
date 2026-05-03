package NgoGiaSam.Web_Elaban_be.service;

public interface EmailService {
    public void sendMessage(String from,String to, String subject, String text);
    void sendOrderConfirmationEmail(String to, Object order);
}
