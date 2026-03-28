package NgoGiaSam.Web_Elaban_be.enity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL,orphanRemoval = true) //cascade = CascadeType.ALL khi thao tác trên cart thì các cartitem cũng bị ảnh hưởng theo ,  orphanRemoval = true khi xóa 1 item khỏi list thì Hibermate sẽ tự xóa item đó khỏi DB
    private List<CartItem> cartItems;


}
