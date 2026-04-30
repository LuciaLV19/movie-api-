package movies.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import movies.dto.DirectorDTO;
import movies.dto.MovieDTO;
import movies.exception.ResourceNotFoundException;
import movies.mapper.DirectorMapper;
import movies.mapper.MovieMapper;
import movies.models.Director;
import movies.repositories.DirectorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/directors")
@Tag(name = "Directors", description = "API for managing directors and their movies")
public class DirectorController {

    private final DirectorMapper directorMapper;
    private final MovieMapper movieMapper;
    private final DirectorRepository directorRepository;

    public DirectorController(DirectorMapper directorMapper,
                              MovieMapper movieMapper,
                              DirectorRepository directorRepository) {
        this.directorMapper = directorMapper;
        this.movieMapper = movieMapper;
        this.directorRepository = directorRepository;
    }

    @GetMapping
    @Operation(summary = "Get all directors", description = "Returns all directors in the system")
    public ResponseEntity<List<DirectorDTO>> getAllDirectors() {

        List<DirectorDTO> directors = directorRepository.findAll()
                .stream()
                .map(directorMapper::toDTO)
                .toList();

        return ResponseEntity.ok(directors);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get director by ID", description = "Returns a specific director by their ID")
    public ResponseEntity<DirectorDTO> getDirectorById(
            @Parameter(description = "Director ID")
            @PathVariable final Long id) {

        Director director = directorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Director not found with id: " + id)
                );

        return ResponseEntity.ok(directorMapper.toDTO(director));
    }

    @GetMapping("/{id}/movies")
    @Operation(summary = "Get movies by director", description = "Returns all movies directed by a specific director")
    public ResponseEntity<List<MovieDTO>> getMoviesByDirector(
            @Parameter(description = "Director ID")
            @PathVariable final Long id) {

        Director director = directorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Director not found with id: " + id)
                );

        List<MovieDTO> movies = director.getMovies()
                .stream()
                .map(movieMapper::toDTO)
                .toList();

        return ResponseEntity.ok(movies);
    }

    @PostMapping
    @Operation(summary = "Create director", description = "Creates a new director")
    public ResponseEntity<DirectorDTO> createDirector(
            @Valid @RequestBody final DirectorDTO dto) {

        Director director = directorMapper.toEntity(dto);
        Director saved = directorRepository.save(director);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(directorMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update director", description = "Updates an existing director")
    public ResponseEntity<DirectorDTO> updateDirector(
            @Parameter(description = "Director ID")
            @PathVariable final Long id,
            @Valid @RequestBody final DirectorDTO dto) {

        Director existing = directorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Director not found with id: " + id)
                );

        existing.setName(dto.getName());
        existing.setBirthDate(dto.getBirthDate());
        existing.setNationality(dto.getNationality());

        Director updated = directorRepository.save(existing);

        return ResponseEntity.ok(directorMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete director", description = "Deletes a director by ID")
    public ResponseEntity<Void> deleteDirector(
            @Parameter(description = "Director ID")
            @PathVariable final Long id) {

        Director director = directorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Director not found with id: " + id)
                );

        director.getMovies().forEach(movie -> movie.setDirector(null));
        directorRepository.delete(director);

        return ResponseEntity.noContent().build();
    }
}