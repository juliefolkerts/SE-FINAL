package finalproject.com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "t_users")
@Getter
@Setter
public class User extends BaseEntity implements UserDetails {

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password; // bcrypt hash

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "blocked", nullable = false)
    private boolean blocked = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    // spring s calls
    @Override
    public boolean isAccountNonLocked() {
        return !blocked;
    }

    @Override
    public boolean isEnabled() {
        return !blocked;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}




//package finalproject.com.example.demo.entity;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.ManyToMany;
//import jakarta.persistence.Table;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.time.LocalDateTime;
//import java.util.Collection;
//import java.util.List;
//
//@Entity
//@Table(name = "t_users")
//@Getter
//@Setter
//public class User extends BaseEntity implements UserDetails {
//
//    @Column(name = "email", unique = true, nullable = false)
//    private String email;
//
//    @Column(name = "password", nullable = false)
//    private String password; // BCrypt hash
//
//    @Column(name = "full_name", nullable = false)
//    private String fullName;
//
//    @Column(name = "blocked", nullable = false)
//    private boolean blocked = false;
//
//    @Column(name = "created_at", nullable = false)
//    private LocalDateTime createdAt;
//
//    @ManyToMany(fetch = FetchType.EAGER)
//    private List<Role> roles;
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return roles;
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return !blocked;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return !blocked;
//    }
//
//    @Override public boolean isAccountNonExpired() { return true; }
//    @Override public boolean isCredentialsNonExpired() { return true; }
//}
