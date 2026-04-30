package movies.controller;

import movies.dto.GenreDTO;
import movies.dto.MovieDTO;
import movies.mapper.GenreMapper;
import movies.mapper.MovieMapper;
import movies.models.Genre;
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
    private GenreMapper genreMapper;

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieRepository movieRepository;

    // Get all genres
    @GetMapping
    public ResponseEntity<List<GenreDTO>> getAllGenres() {
        List<GenreDTO> genres = genreRepository.findAll().stream()
                .map(genreMapper::toDTO)
                .toList();
        return ResponseEntity.ok(genres);
    }

    // Get genre by ID
    @GetMapping("/{id}")
    public ResponseEntity<GenreDTO> getGenreById(@PathVariable final Long id) {
        return genreRepository.findById(id)
                .map(genreMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all movies by genre
    @GetMapping("/{id}/movies")
    public ResponseEntity<List<MovieDTO>> getMoviesByGenre(@PathVariable final Long id) {
        return genreRepository.findById(id)
                .map(genre -> genre.getMovies().stream()
                        .map(movieMapper::toDTO)
                        .toList())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all genres by movie
    @GetMapping("/movies/{id}/genres")
    public ResponseEntity<List<String>> getAllGenresByMovie(@PathVariable final Long id) {
        return movieRepository.findById(id)
                .map(movie -> movie.getGenres().stream()
                        .map(Genre::getName)
                        .toList())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create genre
    @PostMapping
    public ResponseEntity<GenreDTO> createGenre(@RequestBody final GenreDTO dto) {
        Genre genre = genreMapper.toEntity(dto);
        Genre savedGenre = genreRepository.save(genre);
        return ResponseEntity.status(HttpStatus.CREATED).body(genreMapper.toDTO(savedGenre));
    }

    // Update genre
    @PutMapping("/{id}")
    public ResponseEntity<GenreDTO> updateGenre(@PathVariable final Long id, @RequestBody final GenreDTO dto) {
        return genreRepository.findById(id)
                .map(existing -> {
            existing.setName(dto.getName());
            return genreRepository.save(existing);
        })
                .map(genreMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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