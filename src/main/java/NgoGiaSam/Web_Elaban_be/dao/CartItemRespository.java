package NgoGiaSam.Web_Elaban_be.dao;


import NgoGiaSam.Web_Elaban_be.enity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "cart_item")
public interface CartItemRespository extends JpaRepository<CartItem,Long> {
    List<CartItem> findByCart_Id(Long cartId);

    Optional<CartItem> findByCart_IdAndProduct_Id(Long cartId, Long productId);

}

