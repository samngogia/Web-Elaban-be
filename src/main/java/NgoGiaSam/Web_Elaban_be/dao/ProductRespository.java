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
public interface ProductRespository extends JpaRepository<Product,Long> {


    // 1. Tìm theo tên sản phẩm (Giả định biến trong Product là 'name')
    Page<Product> findByNameContaining(@RequestParam("name") String name, Pageable pageable);


    Page<Product> findByNameContainingAndCategories_Id(@RequestParam("name") String name, @RequestParam("id") Long id, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId")
    Page<Product> findByCategories_Id(@Param("categoryId") Long categoryId, Pageable pageable);

}
