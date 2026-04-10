package NgoGiaSam.Web_Elaban_be.service;


import NgoGiaSam.Web_Elaban_be.dao.RoleRespository;
import NgoGiaSam.Web_Elaban_be.dao.UserRespository;
import NgoGiaSam.Web_Elaban_be.enity.Role;
import NgoGiaSam.Web_Elaban_be.enity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRespository userRespository;

    @Autowired
    private RoleRespository roleRespository;

    @Autowired
    public  UserServiceImpl(UserRespository userRespository,RoleRespository roleRespository){
        this.userRespository=userRespository;
        this.roleRespository=roleRespository;
    }

    @Override
    public User findByUsername(String username) {
        // Lúc này 'User' đã được hiểu là NgoGiaSam.Web_Elaban_be.enity.User
        return userRespository.findByUsername(username).orElse(null);
    }

    @Override
    public  UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRespository.findByUsername(username).orElse(null);
        if(userEntity == null){
            throw new UsernameNotFoundException("Tai khoan khong ton tai!");
        }
        // 2. Sử dụng đường dẫn đầy đủ cho User của Security để không bị lẫn với Entity User
        return new org.springframework.security.core.userdetails.User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                rolesToAuthorities(userEntity.getRoles())
        );
    }

    private Collection<? extends GrantedAuthority> rolesToAuthorities(Collection<Role>  roles){
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

    }

}
