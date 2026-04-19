package NgoGiaSam.Web_Elaban_be.enity;

import jakarta.persistence.*;
import lombok.Data;

import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "voucher")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    private DiscountType discountType; // PERCENT | FIXED

    @Column(name = "discount_value")
    private double discountValue;

    @Column(name = "min_order_amount")
    private double minOrderAmount = 0;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "used_count")
    private int usedCount = 0;

    @Column(name = "expired_date")
    private LocalDateTime expiredDate;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() { createdDate = LocalDateTime.now(); }

    public enum DiscountType { PERCENT, FIXED }
}
