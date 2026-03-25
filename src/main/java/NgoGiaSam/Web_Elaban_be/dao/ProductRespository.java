package NgoGiaSam.Web_Elaban_be.dao;

import NgoGiaSam.Web_Elaban_be.enity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;


@RepositoryRestResource(path = "products")
public interface ProductRespository extends JpaRepository<Product,Long> {


    // 1. Tìm theo tên sản phẩm (Giả định biến trong Product là 'name')
    Page<Product> findByNameContaining(@RequestParam("name") String name, Pageable pageable);

    // 2. Tìm theo ID danh mục (Dùng 'Categories_Id' vì trong Category biến là 'id')
    // Lưu ý: Trong Product bạn phải đặt tên List là 'categories'
    Page<Product> findByCategories_Id(@RequestParam("id") Long id, Pageable pageable);

    // 3. Tìm theo cả tên và mã danh mục
    Page<Product> findByNameContainingAndCategories_Id(@RequestParam("name") String name, @RequestParam("id") Long id, Pageable pageable);
}
