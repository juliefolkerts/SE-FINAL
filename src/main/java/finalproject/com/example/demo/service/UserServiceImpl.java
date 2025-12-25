package finalproject.com.example.demo.service;

import finalproject.com.example.demo.entity.Role;
import finalproject.com.example.demo.entity.User;
import finalproject.com.example.demo.repository.RoleRepository;
import finalproject.com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (Objects.nonNull(user)) {
            return user;
        }
        throw new UsernameNotFoundException("User Not Found");
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                return (User) principal;
            }
        }
        return null;
    }

    @Override
    public Boolean register(String email, String password, String repeatPassword, String fullName) {
        User existing = userRepository.findByEmail(email);

        // lecture-style return values:
        // null  -> email exists
        // false -> password mismatch
        // true  -> success
        if (existing != null) {
            return null;
        }

        if (!password.equals(repeatPassword)) {
            return false;
        }

        Role userRole = roleRepository.findByRole("ROLE_USER");
        if (userRole == null) {
            throw new IllegalStateException("ROLE_USER not found. Roles must be seeded first.");
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setFullName(fullName);
        newUser.setRoles(List.of(userRole));
        newUser.setBlocked(false);
        newUser.setCreatedAt(LocalDateTime.now());

        userRepository.save(newUser);
        return true;
    }

    @Override
    public Boolean changePassword(String oldPassword, String newPassword, String repeatNewPassword) {
        User currentUser = getCurrentUser();

        // lecture-style:
        // null -> unauthorized OR old password mismatch
        // false -> new passwords mismatch
        // true -> success
        if (currentUser == null) {
            return null;
        }

        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            return null;
        }

        if (!newPassword.equals(repeatNewPassword)) {
            return false;
        }

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);

        return true;
    }
}
