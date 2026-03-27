package NgoGiaSam.Web_Elaban_be.dao;
import org.springframework.data.domain.Pageable;
import NgoGiaSam.Web_Elaban_be.enity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "reviews")
public interface ReviewRespository extends JpaRepository<Review,Long> {
    Page<Review> findByProduct_Id(@Param("productId") Long productId, Pageable pageable);
}
