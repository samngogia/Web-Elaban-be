package NgoGiaSam.Web_Elaban_be.dao;
import org.springframework.data.domain.Pageable;
import NgoGiaSam.Web_Elaban_be.enity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "reviews")
public interface ReviewRespository extends JpaRepository<Review,Long> {

    Page<Review> findByProduct_Id(@Param("productId") Long productId, Pageable pageable);

    List<Review> findByProduct_Id(@Param("productId") Long productId);

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId ORDER BY r.createdDate DESC")
    List<Review> findByProduct_IdOrderByCreatedDateDesc(@Param("productId") Long productId);



    List<Review> findByApproved(boolean approved);

    List<Review> findByHidden(boolean hidden);

    @Query("SELECT r FROM Review r ORDER BY r.createdDate DESC")
    List<Review> getAllForAdmin();
}
