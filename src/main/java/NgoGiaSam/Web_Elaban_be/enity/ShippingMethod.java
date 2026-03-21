package NgoGiaSam.Web_Elaban_be.enity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Entity
@Data
@Table(name = "shipping_method")
public class ShippingMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_method_id")
    private Long id; // Chuyển sang Long để đồng bộ toàn hệ thống

    @Column(name = "name", length = 255, nullable = false)
    private String name; // Ví dụ: Giao hàng tiêu chuẩn, Xe tải nội thất, Lắp đặt tận nhà

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // Mô tả: Giao trong 24h, hỗ trợ bưng bê lên lầu...

    @Column(name = "shipping_fee")
    private double shippingFee;

    @OneToMany(mappedBy = "shippingMethod", fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Order> orders; // Đổi từ danhSachDonHang sang orders
}
