package NgoGiaSam.Web_Elaban_be.dao;

import NgoGiaSam.Web_Elaban_be.enity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource (path = "product_image")
public interface ProductImageRespository extends JpaRepository<ProductImage,Integer> {
    // Tìm ảnh theo productId
    List<ProductImage> findByProduct_Id(@Param("productId") Long productId);
}
