package NgoGiaSam.Web_Elaban_be.dao;

import NgoGiaSam.Web_Elaban_be.enity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;


@RepositoryRestResource(path = "users")
public interface UserRespository extends JpaRepository<User,Long> {
    public   boolean existsByUsername(String username);

    public   boolean existsByEmail(String email);

    public Optional<User> findByUsername(String username);

    public Optional<User> findByEmail(String email);
}
