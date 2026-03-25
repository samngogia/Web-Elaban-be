package NgoGiaSam.Web_Elaban_be.enity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "product_image")

public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Chuyển sang Long để đồng bộ toàn hệ thống

    @Column(name = "name", length = 256)
    private String name;

    @Column(name = "is_thumbnail") // Thay cho 'icon' để xác định ảnh đại diện sản phẩm
    private boolean isThumbnail;

    @Column(name = "imageUrl", length = 512) // Đường dẫn ảnh (thường dùng URL từ Cloudinary/S3)
    private String imageUrl;

    @Lob
    @Column(name = "data", columnDefinition = "LONGTEXT")
    private String data; // Dùng nếu bạn lưu ảnh dạng Base64 (tuy nhiên nên ưu tiên dùng URL)

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Đổi từ Sach sang Product
}

