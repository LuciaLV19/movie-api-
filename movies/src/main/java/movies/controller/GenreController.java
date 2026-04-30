package movies.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import movies.dto.GenreDTO;
import movies.dto.MovieDTO;
import movies.exception.ResourceNotFoundException;
import movies.mapper.GenreMapper;
import movies.mapper.MovieMapper;
import movies.models.Genre;
import movies.repositories.GenreRepository;
import movies.repositories.MovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/genres")
@Tag(name = "Genres", description = "API for managing genres and their relationships with movies")
public class GenreController {

    private final GenreMapper genreMapper;
    private final MovieMapper movieMapper;
    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;

    public GenreController(GenreMapper genreMapper,
                           MovieMapper movieMapper,
                           GenreRepository genreRepository,
                           MovieRepository movieRepository) {
        this.genreMapper = genreMapper;
        this.movieMapper = movieMapper;
        this.genreRepository = genreRepository;
        this.movieRepository = movieRepository;
    }

    @GetMapping
    @Operation(summary = "Get all genres", description = "Returns all genres in the system")
    public ResponseEntity<List<GenreDTO>> getAllGenres() {

        List<GenreDTO> genres = genreRepository.findAll()
                .stream()
                .map(genreMapper::toDTO)
                .toList();

        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get genre by ID", description = "Returns a specific genre by its ID")
    public ResponseEntity<GenreDTO> getGenreById(
            @Parameter(description = "Genre ID")
            @PathVariable final Long id) {

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Genre not found with id: " + id)
                );

        return ResponseEntity.ok(genreMapper.toDTO(genre));
    }

    @GetMapping("/{id}/movies")
    @Operation(summary = "Get movies by genre", description = "Returns all movies in a specific genre")
    public ResponseEntity<List<MovieDTO>> getMoviesByGenre(
            @Parameter(description = "Genre ID")
            @PathVariable final Long id) {

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Genre not found with id: " + id)
                );

        List<MovieDTO> movies = genre.getMovies()
                .stream()
                .map(movieMapper::toDTO)
                .toList();

        return ResponseEntity.ok(movies);
    }

    @GetMapping("/movies/{id}/genres")
    @Operation(summary = "Get genres by movie", description = "Returns all genres for a specific movie")
    public ResponseEntity<List<String>> getGenresByMovie(
            @Parameter(description = "Movie ID")
            @PathVariable final Long id) {

        return movieRepository.findById(id)
                .map(movie -> movie.getGenres()
                        .stream()
                        .map(Genre::getName)
                        .toList())
                .map(ResponseEntity::ok)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Movie not found with id: " + id)
                );
    }

    @PostMapping
    @Operation(summary = "Create genre", description = "Creates a new genre")
    public ResponseEntity<GenreDTO> createGenre(
            @Valid @RequestBody final GenreDTO dto) {

        Genre genre = genreMapper.toEntity(dto);
        Genre saved = genreRepository.save(genre);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(genreMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update genre", description = "Updates an existing genre")
    public ResponseEntity<GenreDTO> updateGenre(
            @Parameter(description = "Genre ID")
            @PathVariable final Long id,
            @Valid @RequestBody final GenreDTO dto) {

        Genre existing = genreRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Genre not found with id: " + id)
                );

        existing.setName(dto.getName());

        Genre updated = genreRepository.save(existing);

        return ResponseEntity.ok(genreMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete genre", description = "Deletes a genre by ID")
    public ResponseEntity<Void> deleteGenre(
            @Parameter(description = "Genre ID")
            @PathVariable final Long id) {

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Genre not found with id: " + id)
                );

        genreRepository.delete(genre);

        return ResponseEntity.noContent().build();
    }
}