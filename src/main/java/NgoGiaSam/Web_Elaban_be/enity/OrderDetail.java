package NgoGiaSam.Web_Elaban_be.enity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "order_detail")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Đổi từ int sang Long

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "price")
    private double price; // Giá tại thời điểm mua (để lưu vết dù sau này SP có tăng giá)

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Đổi từ Sach sang Product

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // Đổi từ DonHang sang Order
}
