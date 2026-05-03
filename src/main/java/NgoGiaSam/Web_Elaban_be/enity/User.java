package NgoGiaSam.Web_Elaban_be.enity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "users") // 'user' thường là từ khóa dè dặt trong SQL, nên dùng 'users'
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id; // Chuyển sang Long để đồng bộ

    @Column(name = "first_name", length = 255)
    private String firstName; // Tương ứng 'ten'

    @Column(name = "last_name", length = 255)
    private String lastName; // Tương ứng 'hoDem'

    @Column(name = "username", length = 255, unique = true, nullable = false)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "gender", length = 50)
    private String gender;

    @Column(name = "email", length = 255, unique = true)
    private String email;

    @Column(name = "billing_address", length = 512)
    private String billingAddress;

    @Column(name = "shipping_address", length = 512)
    private String shippingAddress;

    @Column(name = "is_enabled")
    private boolean isEnabled; // Tương ứng 'daKichHoat'

    @Column(name = "activation_code")
    private String activationCode;


    //quên mk trả về OTP
    @Column(name = "reset_otp")
    private String resetOtp;

    @Column(name = "otp_expiry")
    private LocalDateTime otpExpiry;

    @Lob
    @Column(name = "avatar", columnDefinition = "LONGTEXT")
    private String avatar;

    // --- CÁC MỐI QUAN HỆ ---

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<WishList> wishlists;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", billingAddress='" + billingAddress + '\'' +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", isEnabled=" + isEnabled +
                ", activationCode='" + activationCode + '\'' +
                ", avatar='" + avatar + '\'' +
                ", roles=" + roles +
                ", reviews=" + reviews +
                ", wishlists=" + wishlists +
                ", orders=" + orders +
                '}';
    }

}
