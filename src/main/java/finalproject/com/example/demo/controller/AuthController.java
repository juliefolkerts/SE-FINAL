package finalproject.com.example.demo.controller;

import finalproject.com.example.demo.dto.auth.AuthResponse;
import finalproject.com.example.demo.dto.auth.LoginRequest;
import finalproject.com.example.demo.dto.auth.RegisterRequest;
import finalproject.com.example.demo.entity.User;
import finalproject.com.example.demo.security.jwt.JwtService;
import finalproject.com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        Boolean result = userService.register(req.getEmail(), req.getPassword(), req.getRepeatPassword(), req.getFullName());

        if (result == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is occupied!");
        }
        if (!result) {
            return ResponseEntity.badRequest().body("Passwords are mismatching!");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("User has successfully been registered!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        User user = (User) userService.loadUserByUsername(req.getEmail());
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
