package finalproject.com.example.demo.controller;

import finalproject.com.example.demo.dto.user.UserResponse;
import finalproject.com.example.demo.entity.Role;
import finalproject.com.example.demo.entity.User;
import finalproject.com.example.demo.repository.RoleRepository;
import finalproject.com.example.demo.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static class AdminCreateUserRequest {
        @Email @NotBlank public String email;
        @NotBlank @Size(min = 6, max = 100) public String password;
        @NotBlank @Size(min = 2, max = 200) public String fullName;
        @NotBlank public String role; // ROLE_USER / ROLE_SELLER / ROLE_ADMIN
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody AdminCreateUserRequest req) {
        if (userRepository.findByEmail(req.email) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is occupied!");
        }

        Role r = roleRepository.findByRole(req.role);
        if (r == null) {
            return ResponseEntity.badRequest().body("Role not found: " + req.role);
        }

        User u = new User();
        u.setEmail(req.email);
        u.setPassword(passwordEncoder.encode(req.password));
        u.setFullName(req.fullName);
        u.setRoles(List.of(r));
        u.setBlocked(false);
        u.setCreatedAt(LocalDateTime.now());

        userRepository.save(u);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(u));
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<?> block(@PathVariable Long id) {
        User u = userRepository.findById(id).orElse(null);
        if (u == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        u.setBlocked(true);
        userRepository.save(u);
        return ResponseEntity.ok("User blocked");
    }

    @PatchMapping("/{id}/unblock")
    public ResponseEntity<?> unblock(@PathVariable Long id) {
        User u = userRepository.findById(id).orElse(null);
        if (u == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        u.setBlocked(false);
        userRepository.save(u);
        return ResponseEntity.ok("User unblocked");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted");
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(
                u.getId(),
                u.getEmail(),
                u.getFullName(),
                u.isBlocked(),
                u.getCreatedAt(),
                u.getRoles().stream().map(Role::getRole).collect(Collectors.toList())
        );
    }
}
