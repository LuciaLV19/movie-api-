package movies.controller;

import movies.models.Genre;
import movies.models.Movie;
import movies.repositories.GenreRepository;
import movies.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/genres")
public class GenreController {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieRepository movieRepository;

    // Get all genres
    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        return ResponseEntity.ok(genres);
    }

    // Get genre by ID
    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable final Long id) {
        return genreRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all movies by genre
    @GetMapping("/{id}/movies")
    public ResponseEntity<List<Movie>> getMoviesByGenre(@PathVariable final Long id) {
        if(!genreRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        List<Movie> movies= movieRepository.findByGenresId(id);
        return ResponseEntity.ok(movies);
    }

    // Get all genres by movie
    @GetMapping("/movies/{id}/genres")
    public ResponseEntity<List<Genre>> getAllGenresByMovie(@PathVariable final Long id) {
        return movieRepository.findById(id)
                .map(movie -> ResponseEntity.ok(movie.getGenres()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create genre
    @PostMapping
    public ResponseEntity<Genre> createGenre(@RequestBody final Genre genre) {
        Genre savedGenre = genreRepository.save(genre);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGenre);
    }

    // Update genre
    @PutMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(@PathVariable final Long id,
                                             @RequestBody final Genre updatedGenre) {
        if (!genreRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        updatedGenre.setId(id);
        Genre savedGenre = genreRepository.save(updatedGenre);
        return ResponseEntity.ok(savedGenre);
    }

    // Delete genre
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable final Long id) {
        if (!genreRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        genreRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}