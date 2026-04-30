package movies.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import movies.dto.LoginRequestDTO;
import movies.dto.LoginResponseDTO;
import movies.dto.RegisterRequestDTO;
import movies.dto.RegisterResponseDTO;
import movies.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "API for user authentication and registration")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token")
    public ResponseEntity<LoginResponseDTO> login(
            @Parameter(description = "Login credentials")
            @Valid @RequestBody final LoginRequestDTO request) {

        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Registers a new user in the system")
    public ResponseEntity<RegisterResponseDTO> register(
            @Parameter(description = "Registration data")
            @Valid @RequestBody final RegisterRequestDTO request) {

        RegisterResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}