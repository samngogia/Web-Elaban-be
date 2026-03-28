package NgoGiaSam.Web_Elaban_be.enity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name ="cart_item")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long quantity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cart_id", nullable = false)
    private Cart cart;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id", nullable = false)
    private Product product;

}
