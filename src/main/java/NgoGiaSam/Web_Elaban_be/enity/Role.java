package NgoGiaSam.Web_Elaban_be.enity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id; // Chuyển sang Long để đồng bộ

    @Column(name = "name", length = 255, nullable = false)
    private String name; // Ví dụ: ROLE_ADMIN, ROLE_CUSTOMER, ROLE_STAFF

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    private List<User> users; // Đổi từ danhSachSachNguoiDung sang users
}
