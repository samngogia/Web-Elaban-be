package NgoGiaSam.Web_Elaban_be.dao;

import NgoGiaSam.Web_Elaban_be.enity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource (path = "order")
public interface OrderRespository extends JpaRepository<Order,Long> {
    // Đếm tổng đơn hàng
    long count();

    // Tổng doanh thu
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.paymentStatus = 'PAID'")
    Double getTotalRevenue();

    // Đơn hàng theo trạng thái
    List<Order> findByPaymentStatus(String paymentStatus);
    List<Order> findByShippingStatus(String shippingStatus);
    List<Order> findByUser_Id(Long userId);
    // Đơn hàng gần đây
    List<Order> findTop5ByOrderByCreatedDateDesc();

    // Doanh thu theo tháng
    @Query("SELECT MONTH(o.createdDate), SUM(o.totalAmount) FROM Order o " +
            "WHERE YEAR(o.createdDate) = :year AND o.paymentStatus = 'PAID' " +
            "GROUP BY MONTH(o.createdDate) ORDER BY MONTH(o.createdDate)")
    List<Object[]> getRevenueByMonth(@Param("year") int year);


    List<Order> findByUser_IdOrderByCreatedDateDesc(Long userId);
}
