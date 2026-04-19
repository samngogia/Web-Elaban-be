package NgoGiaSam.Web_Elaban_be.dao;

import NgoGiaSam.Web_Elaban_be.enity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserIdOrderByIsDefaultDesc(Long userId);

    // Reset tất cả isDefault = false của một user
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    @Modifying
    @Transactional
    void resetDefaultAddresses(@Param("userId") Long userId);

    // Tìm địa chỉ mặc định của user
    Address findByUserIdAndIsDefaultTrue(Long userId);
}
