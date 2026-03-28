package NgoGiaSam.Web_Elaban_be.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/")
    public String test() {
        return "Kết nối thành công!";
    }
}
