package finalproject.com.example.demo.config;

import finalproject.com.example.demo.entity.Role;
import finalproject.com.example.demo.entity.User;
import finalproject.com.example.demo.repository.RoleRepository;
import finalproject.com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initRolesAndAdmin(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            createRoleIfMissing(roleRepository, "ROLE_ADMIN");
            createRoleIfMissing(roleRepository, "ROLE_USER");
            createRoleIfMissing(roleRepository, "ROLE_SELLER");

            String adminEmail = "admin@store.com";
            if (userRepository.findByEmail(adminEmail) == null) {
                Role adminRole = roleRepository.findByRole("ROLE_ADMIN");

                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setFullName("Admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRoles(List.of(adminRole));
                admin.setBlocked(false);
                admin.setCreatedAt(LocalDateTime.now());

                userRepository.save(admin);

                System.out.println("=== Seeded admin ===");
                System.out.println("email: admin@store.com");
                System.out.println("password: admin123");
            }
        };
    }

    private void createRoleIfMissing(RoleRepository repo, String roleName) {
        if (repo.findByRole(roleName) == null) {
            Role r = new Role();
            r.setRole(roleName);
            repo.save(r);
        }
    }
}
