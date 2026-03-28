package NgoGiaSam.Web_Elaban_be.dao;

import NgoGiaSam.Web_Elaban_be.enity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.Optional;


@RepositoryRestResource (path = "cart")
public interface CartRespository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUser_Id(Long userId);
}


