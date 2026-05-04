package NgoGiaSam.Web_Elaban_be.dao;

import NgoGiaSam.Web_Elaban_be.dto.ProductSalesDTO;
import NgoGiaSam.Web_Elaban_be.enity.OrderDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Date;
import java.util.List;


@RepositoryRestResource (path = "order_detail")
public interface OrderDetailRespository extends JpaRepository<OrderDetail,Long> {
    @Query("SELECT new NgoGiaSam.Web_Elaban_be.dto.ProductSalesDTO(" +
            "od.product.id, od.product.name, SUM(od.quantity), od.product.quantity) " +
            "FROM OrderDetail od " +
            "JOIN od.order o " +
            "WHERE o.createdDate >= :startDate AND o.paymentStatus = 'PAID' " +
            "GROUP BY od.product.id, od.product.name, od.product.quantity " +
            "ORDER BY SUM(od.quantity) DESC")
    List<ProductSalesDTO> findTopSellingProducts(@Param("startDate") Date startDate, Pageable pageable);
}
