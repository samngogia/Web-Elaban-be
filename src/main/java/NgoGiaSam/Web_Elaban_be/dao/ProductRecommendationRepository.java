package NgoGiaSam.Web_Elaban_be.dao;

import NgoGiaSam.Web_Elaban_be.enity.ProductRecommendation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRecommendationRepository  extends JpaRepository<ProductRecommendation, Long> {
    // Apriori — sản phẩm thường mua cùng
    @Query("SELECT r FROM ProductRecommendation r " +
            "WHERE r.productId = :productId AND r.algorithm = 'apriori' " +
            "ORDER BY r.lift DESC")
    List<ProductRecommendation> findAprioriByProductId(
            @Param("productId") Long productId, Pageable pageable);

    // Content-based — sản phẩm tương tự
    @Query("SELECT r FROM ProductRecommendation r " +
            "WHERE r.productId = :productId AND r.algorithm = 'content_based' " +
            "ORDER BY r.confidence DESC")
    List<ProductRecommendation> findContentBasedByProductId(
            @Param("productId") Long productId, Pageable pageable);
}
