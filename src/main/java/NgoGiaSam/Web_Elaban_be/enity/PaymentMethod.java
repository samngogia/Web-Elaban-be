package NgoGiaSam.Web_Elaban_be.enity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "payment_method")
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_method_id")
    private Long id; // Chuyển sang Long cho đồng bộ hệ thống

    @Column(name = "name", length = 256, nullable = false)
    private String name; // Ví dụ: Chuyển khoản, MoMo, Trả góp 0%, COD

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "payment_fee")
    private double paymentFee; // Chi phí phụ thu khi dùng phương thức này

    @OneToMany(mappedBy = "paymentMethod", fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Order> orders; // Đổi từ danhSachDonHang sang orders
}
