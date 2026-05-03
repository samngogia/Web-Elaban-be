package NgoGiaSam.Web_Elaban_be.dao;

import NgoGiaSam.Web_Elaban_be.enity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource (path = "role")
public interface RoleRespository extends JpaRepository<Role,Integer> {
    Optional<Role> findByName(String name);
}
