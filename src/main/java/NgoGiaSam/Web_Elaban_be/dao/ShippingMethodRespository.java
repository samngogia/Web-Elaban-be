package NgoGiaSam.Web_Elaban_be.dao;

import NgoGiaSam.Web_Elaban_be.enity.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource (path = "shipping_method")
public interface ShippingMethodRespository extends JpaRepository<ShippingMethod,Integer> {
}
