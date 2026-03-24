package NgoGiaSam.Web_Elaban_be.service;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    public NgoGiaSam.Web_Elaban_be.enity.User findByUsername(String username);
}
