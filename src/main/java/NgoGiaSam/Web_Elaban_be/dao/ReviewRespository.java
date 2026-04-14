package NgoGiaSam.Web_Elaban_be.dao;
import org.springframework.data.domain.Pageable;
import NgoGiaSam.Web_Elaban_be.enity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(path = "reviews")
public interface ReviewRespository extends JpaRepository<Review,Long> {
    @RestResource(path = "findByProduct_IdAndApprovedTrueOrderByCreatedDateDesc")
    List<Review> findByProduct_IdAndApprovedTrueOrderByCreatedDateDesc(
            @Param("productId") Long productId);

    @RestResource(exported = false)
    List<Review> findByProduct_Id(Long productId);

    @RestResource(exported = false)
    List<Review> findByApprovedFalse();

    @RestResource(exported = false)
    List<Review> findByProduct_IdAndApprovedTrue(Long productId);

    @RestResource(exported = false)
    @Query("SELECT r FROM Review r ORDER BY r.createdDate DESC")
    List<Review> findAllByOrderByCreatedDateDesc();
}
