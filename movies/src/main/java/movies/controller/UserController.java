package movies.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import movies.dto.UserCreateDTO;
import movies.dto.UserDTO;
import movies.exception.ResourceNotFoundException;
import movies.mapper.UserMapper;
import movies.models.User;
import movies.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "API for managing users")
public class UserController {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserController(UserMapper userMapper,
                          UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Returns all registered users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {

        List<UserDTO> users = userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Returns a user by their ID")
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "User ID")
            @PathVariable final Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Registers a new user")
    public ResponseEntity<UserDTO> createUser(
            @Parameter(description = "User data")
            @Valid @RequestBody final UserCreateDTO dto) {

        User user = userMapper.toEntity(dto);
        User saved = userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "User ID")
            @PathVariable final Long id,
            @Valid @RequestBody final UserCreateDTO dto) {

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        existing.setUsername(dto.getUsername());
        existing.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existing.setPassword(dto.getPassword());
        }

        User updated = userRepository.save(existing);
        return ResponseEntity.ok(userMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by ID")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID")
            @PathVariable final Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }
}