package movies.controller;

import movies.dto.DirectorDTO;
import movies.dto.MovieDTO;
import movies.mapper.DirectorMapper;
import movies.mapper.MovieMapper;
import movies.models.Director;
import movies.repositories.DirectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/directors")
public class DirectorController {

    @Autowired
    private DirectorMapper directorMapper;

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private DirectorRepository directorRepository;

    // Get all directors
    @GetMapping
    public ResponseEntity<List<DirectorDTO>> getAllDirectors() {
        List<DirectorDTO> directors = directorRepository.findAll().stream()
                .map(directorMapper::toDTO)
                .toList();
        return ResponseEntity.ok(directors);
    }

    // Get director by ID
    @GetMapping("/{id}")
    public ResponseEntity<DirectorDTO> getDirectorById(@PathVariable final Long id) {
        return directorRepository.findById(id)
                .map(directorMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all movies by director
    @GetMapping("/{id}/movies")
    public ResponseEntity<List<MovieDTO>> getMoviesByDirector(@PathVariable final Long id) {
        return directorRepository.findById(id)
                .map(director -> director.getMovies().stream()
                        .map(movieMapper::toDTO)
                        .toList())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create director
    @PostMapping
    public ResponseEntity<DirectorDTO> createDirector(@RequestBody DirectorDTO dto) {
        Director director = directorMapper.toEntity(dto);
        Director saved = directorRepository.save(director);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(directorMapper.toDTO(saved));
    }

    // Update director
    @PutMapping("/{id}")
    public ResponseEntity<DirectorDTO> updateDirector(
            @PathVariable Long id,
            @RequestBody DirectorDTO dto) {

        return directorRepository.findById(id)
                .map(existing -> {
                    existing.setName(dto.getName());
                    existing.setBirthDate(dto.getBirthDate());
                    existing.setNationality(dto.getNationality());
                    return directorRepository.save(existing);
                })
                .map(directorMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete director
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDirector(@PathVariable Long id) {
        if (!directorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Director director = directorRepository.findById(id).get();
        director.getMovies().forEach(movie -> movie.setDirector(null));
        directorRepository.delete(director);
        return ResponseEntity.noContent().build();
    }
}