package finalproject.com.example.demo.controller;

import finalproject.com.example.demo.dto.user.ChangePasswordRequest;
import finalproject.com.example.demo.dto.user.UpdateProfileRequest;
import finalproject.com.example.demo.dto.user.UserResponse;
import finalproject.com.example.demo.entity.User;
import finalproject.com.example.demo.repository.UserRepository;
import finalproject.com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> me() {
        User u = userService.getCurrentUser();
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(toResponse(u));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest req) {
        User current = userService.getCurrentUser();
        if (current == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

        User byEmail = userRepository.findByEmail(req.getEmail());
        if (byEmail != null && !byEmail.getId().equals(current.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is occupied!");
        }

        current.setEmail(req.getEmail());
        current.setFullName(req.getFullName());
        userRepository.save(current);

        return ResponseEntity.ok(toResponse(current));
    }

    @PutMapping("/me/password")
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        Boolean result = userService.changePassword(req.getOldPassword(), req.getNewPassword(), req.getRepeatNewPassword());

        if (result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password is mismatching!");
        }
        if (!result) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New passwords are mismatching!");
        }
        return ResponseEntity.ok("Password has been changed successfully!");
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(
                u.getId(),
                u.getEmail(),
                u.getFullName(),
                u.isBlocked(),
                u.getCreatedAt(),
                u.getRoles().stream().map(r -> r.getRole()).collect(Collectors.toList())
        );
    }
}
