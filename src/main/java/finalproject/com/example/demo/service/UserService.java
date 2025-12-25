package finalproject.com.example.demo.service;

import finalproject.com.example.demo.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Boolean register(String email, String password, String repeatPassword, String fullName);

    Boolean changePassword(String oldPassword, String newPassword, String repeatNewPassword);

    User getCurrentUser();
}
