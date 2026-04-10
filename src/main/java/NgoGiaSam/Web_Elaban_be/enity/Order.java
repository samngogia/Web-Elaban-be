package NgoGiaSam.Web_Elaban_be.enity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id; // Chuyển sang Long để đồng bộ

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "billing_address", length = 512) // Địa chỉ thanh toán
    private String billingAddress;

    @Column(name = "shipping_address", length = 512) // Địa chỉ giao hàng
    private String shippingAddress;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "payment_status", length = 255) // Trạng thái thanh toán (Paid/Unpaid)
    private String paymentStatus;

    @Column(name = "shipping_status", length = 255) // Trạng thái giao hàng (Shipping/Delivered)
    private String shippingStatus;

    @Column(name = "total_price_product") // Tổng tiền hàng (chưa gồm ship)
    private double totalPriceProduct;

    @Column(name = "shipping_fee") // Phí vận chuyển (nội thất thường có phí này cao)
    private double shippingFee;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    // --- MỐI QUAN HỆ ---

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "shipping_method_id", nullable = false)
    private ShippingMethod shippingMethod;



    @PrePersist
    public void prePersist() {
        if (createdDate == null) {
            createdDate = new Date();
        }
    }
}
