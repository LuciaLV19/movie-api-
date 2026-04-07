package movies.controller;

import movies.models.Director;
import movies.models.Movie;
import movies.repositories.DirectorRepository;
import movies.repositories.MovieRepository;
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
    private DirectorRepository directorRepository;

    @Autowired
    private MovieRepository movieRepository;

    // Get all directors
    @GetMapping
    public ResponseEntity<List<Director>> getAllDirectors() {
        List<Director> directors = directorRepository.findAll();
        return ResponseEntity.ok(directors);
    }

    // Get director by ID
    @GetMapping("/{id}")
    public ResponseEntity<Director> getDirectorById(@PathVariable final Long id) {
        return directorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all movies by director
    @GetMapping("/{id}/movies")
    public ResponseEntity<List<Movie>> getMoviesByDirector(@PathVariable final Long id) {
        if(!directorRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        List<Movie> movies = movieRepository.findByDirectorId(id);
        return ResponseEntity.ok(movies);
    }

    // Create director
    @PostMapping
    public ResponseEntity<Director> createDirector(@RequestBody final Director director) {
        Director savedDirector = directorRepository.save(director);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDirector);
    }

    // Update director
    @PutMapping("/{id}")
    public ResponseEntity<Director> updateDirector(@PathVariable final Long id,
                                                   @RequestBody final Director updatedDirector) {
        if (!directorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        updatedDirector.setId(id);
        Director savedDirector = directorRepository.save(updatedDirector);
        return ResponseEntity.ok(savedDirector);
    }

    // Delete director
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDirector(@PathVariable final Long id) {
        if (!directorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        directorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}