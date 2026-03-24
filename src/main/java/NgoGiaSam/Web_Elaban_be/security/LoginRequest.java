package NgoGiaSam.Web_Elaban_be.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor //tự tạo 1 constructor có đầy đủ tham số
@NoArgsConstructor  //tự tạo 1 constructor rỗng
public class LoginRequest {
    private String username;
    private String password;
}
