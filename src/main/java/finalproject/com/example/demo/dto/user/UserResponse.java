package finalproject.com.example.demo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private boolean blocked;
    private LocalDateTime createdAt;
    private List<String> roles;
}
