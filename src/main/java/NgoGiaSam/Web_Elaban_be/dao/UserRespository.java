package NgoGiaSam.Web_Elaban_be.dao;

import NgoGiaSam.Web_Elaban_be.enity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(path = "users")
public interface UserRespository extends JpaRepository<User,Integer> {
    public   boolean existsByUsername(String username);

    public   boolean existsByEmail(String email);

    public User findByUsername(String username);

    public User findByEmail(String email);
}
