package movies.controller;

import movies.dto.UserCreateDTO;
import movies.dto.UserDTO;
import movies.mapper.UserMapper;
import movies.models.User;
import movies.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .toList());
    }

    // Get user by id
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create user (register)
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO dto) {
        User user = userMapper.toEntity(dto);
        User saved = userRepository.save(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userMapper.toDTO(saved));
    }
    // Update user account
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserCreateDTO dto) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setUsername(dto.getUsername());
                    existing.setEmail(dto.getEmail());
                    if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                        existing.setPassword(dto.getPassword());
                    }
                    return userRepository.save(existing);
                })
                .map(userMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}