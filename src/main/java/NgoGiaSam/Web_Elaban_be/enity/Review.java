package NgoGiaSam.Web_Elaban_be.enity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "review")

public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id; // Đồng bộ kiểu Long

    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // Nội dung đánh giá (VD: Sofa rất êm, màu đúng như ảnh)

    @Column(name = "rating")
    private double rating; // Số sao (1.0 - 5.0)

    @Column(name = "created_date")
    private Date createdDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Đổi từ Sach sang Product

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Đổi từ NguoiDung sang User
}
