package NgoGiaSam.Web_Elaban_be.enity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "product") // Đổi tên bảng thành product
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 256, nullable = false)
    private String name;

    @Column(name = "brand", length = 256) // Hãng sản xuất/Thương hiệu
    private String brand;

    @Column(name = "material", length = 512) // Chất liệu (Gỗ sồi, Da, Nỉ...)
    private String material;

    @Column(name = "dimensions", length = 256) // Kích thước (Dài x Rộng x Cao)
    private String dimensions;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "list_price") // Giá niêm yết
    private double listPrice;

    @Column(name = "selling_price") // Giá bán thực tế
    private double sellingPrice;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "avg_rating") // Trung bình đánh giá
    private double avgRating;


    @Column(name = "color")
    private String color;

    ;
    // --- CÁC MỐI QUAN HỆ (RELATIONSHIPS) ---

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "product_category", // Bảng trung gian Sản phẩm - Thể loại
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductImage> images;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<WishList> wishlists;


}