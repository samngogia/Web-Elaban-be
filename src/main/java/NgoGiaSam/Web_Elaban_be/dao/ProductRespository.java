package NgoGiaSam.Web_Elaban_be.dao;

import NgoGiaSam.Web_Elaban_be.enity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestResource(path = "products")
public interface ProductRespository extends JpaRepository<Product, Long> {

    // 0. MỚI THÊM: Lấy tất cả sản phẩm đang hiển thị (Dùng cho Trang Chủ)
    Page<Product> findByActiveTrue(Pageable pageable);

    // 1. Tìm theo tên sản phẩm VÀ đang hiển thị
    Page<Product> findByNameContainingAndActiveTrue(@RequestParam("name") String name, Pageable pageable);

    // 2. Tìm theo tên + danh mục VÀ đang hiển thị
    Page<Product> findByNameContainingAndCategories_IdAndActiveTrue(@RequestParam("name") String name, @RequestParam("id") Long id, Pageable pageable);

    // 3. Tìm theo ID danh mục VÀ đang hiển thị (Dùng @Query cho chắc chắn)
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId AND p.active = true")
    Page<Product> findByCategories_Id(@Param("categoryId") Long categoryId, Pageable pageable);

    // 4. Bộ lọc tổng hợp: Tìm theo tên, khoảng giá VÀ đang hiển thị
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
            "(:name IS NULL OR p.name LIKE %:name%) AND " +
            "(:minPrice IS NULL OR p.sellingPrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.sellingPrice <= :maxPrice)")
    Page<Product> findByFilter(
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable);

    // 5. Bộ lọc theo danh mục, khoảng giá VÀ đang hiển thị
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId AND p.active = true AND " +
            "(:minPrice IS NULL OR p.sellingPrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.sellingPrice <= :maxPrice)")
    Page<Product> findByCategoryAndPrice(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable);
}