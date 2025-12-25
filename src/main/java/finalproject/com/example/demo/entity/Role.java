package finalproject.com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "t_roles")
@Getter
@Setter
public class Role extends BaseEntity implements GrantedAuthority {

    @Column(name = "role", unique = true, nullable = false)
    private String role; // ROLE_ADMIN, ROLE_USER, ROLE_SELLER

    @Override
    public String getAuthority() {
        return role;
    }
}
