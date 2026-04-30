package movies.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import movies.dto.ActorDTO;
import movies.dto.MovieDTO;
import movies.dto.ReviewDTO;
import movies.exception.ResourceNotFoundException;
import movies.mapper.ActorMapper;
import movies.mapper.MovieMapper;
import movies.mapper.ReviewMapper;
import movies.models.Movie;
import movies.repositories.MovieRepository;
import movies.repositories.ReviewRepository;
import movies.services.MovieImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/movies")
@Tag(name = "Movies", description = "API for managing movies")
public class MovieController {

    private final MovieMapper movieMapper;
    private final ActorMapper actorMapper;
    private final ReviewMapper reviewMapper;
    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;
    private final MovieImportService movieImportService;

    public MovieController(MovieMapper movieMapper,
                           ActorMapper actorMapper,
                           ReviewMapper reviewMapper,
                           MovieRepository movieRepository,
                           ReviewRepository reviewRepository,
                           MovieImportService movieImportService) {

        this.movieMapper = movieMapper;
        this.actorMapper = actorMapper;
        this.reviewMapper = reviewMapper;
        this.movieRepository = movieRepository;
        this.reviewRepository = reviewRepository;
        this.movieImportService = movieImportService;
    }

    @GetMapping
    @Operation(summary = "Get all movies", description = "Returns all movies, optionally filtered by title")
    public ResponseEntity<List<MovieDTO>> getMovies(
            @Parameter(description = "Optional title filter")
            @RequestParam(required = false) String title) {

        List<Movie> movies = (title != null && !title.isBlank())
                ? movieRepository.findByTitleContainingIgnoreCase(title)
                : movieRepository.findAll();

        return ResponseEntity.ok(
                movies.stream()
                        .map(movieMapper::toDTO)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get movie by ID", description = "Returns a specific movie by its ID")
    public ResponseEntity<MovieDTO> getMovieById(
            @Parameter(description = "Movie ID")
            @PathVariable final Long id) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Movie not found with id: " + id
                ));

        return ResponseEntity.ok(movieMapper.toDTO(movie));
    }

    @GetMapping("/{id}/actors")
    @Operation(summary = "Get actors by movie", description = "Returns all actors in a specific movie")
    public ResponseEntity<List<ActorDTO>> getActorsByMovie(
            @Parameter(description = "Movie ID")
            @PathVariable final Long id) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Movie not found with id: " + id
                ));

        return ResponseEntity.ok(
                movie.getActors()
                        .stream()
                        .map(actorMapper::toDTO)
                        .toList()
        );
    }

    @GetMapping("/{id}/reviews")
    @Operation(summary = "Get reviews by movie", description = "Returns all reviews for a specific movie")
    public ResponseEntity<List<ReviewDTO>> getReviewsByMovie(
            @Parameter(description = "Movie ID")
            @PathVariable final Long id) {

        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movie not found with id: " + id);
        }

        return ResponseEntity.ok(
                reviewRepository.findByMovieId(id)
                        .stream()
                        .map(reviewMapper::toDTO)
                        .toList()
        );
    }

    @PostMapping
    @Operation(summary = "Create movie", description = "Creates a new movie")
    public ResponseEntity<MovieDTO> createMovie(@Valid @RequestBody final MovieDTO dto) {

        Movie movie = movieMapper.toEntity(dto);
        Movie saved = movieRepository.save(movie);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(movieMapper.toDTO(saved));
    }

    @PostMapping("/import/{title}")
    @Operation(summary = "Import movie", description = "Imports a movie from OMDb database")
    public ResponseEntity<MovieDTO> importMovie(
            @Parameter(description = "Movie title")
            @PathVariable final String title) {

        MovieDTO imported = movieImportService.importFromOMDb(title);

        if (imported == null) {
            throw new ResourceNotFoundException(
                    "Movie not found in OMDb with title: " + title
            );
        }

        return ResponseEntity.ok(imported);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update movie", description = "Updates an existing movie")
    public ResponseEntity<MovieDTO> updateMovie(
            @Parameter(description = "Movie ID")
            @PathVariable final Long id,
            @Valid @RequestBody final MovieDTO dto) {

        Movie existing = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Movie not found with id: " + id
                ));

        existing.setTitle(dto.getTitle());
        existing.setYear(dto.getYear());
        existing.setDescription(dto.getDescription());
        existing.setImage(dto.getImage());

        Movie saved = movieRepository.save(existing);

        return ResponseEntity.ok(movieMapper.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete movie", description = "Deletes a movie by ID")
    public ResponseEntity<Void> deleteMovie(
            @Parameter(description = "Movie ID")
            @PathVariable final Long id) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Movie not found with id: " + id
                ));

        movieRepository.delete(movie);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/vote")
    @Operation(summary = "Vote on movie", description = "Updates the rating of a movie")
    public ResponseEntity<MovieDTO> voteMovie(
            @Parameter(description = "Movie ID")
            @PathVariable final Long id,
            @Parameter(description = "Rating value")
            @RequestParam final double rating) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Movie not found with id: " + id
                ));

        double newRating = ((movie.getVotes() * movie.getRating()) + rating)
                / (movie.getVotes() + 1);

        movie.setVotes(movie.getVotes() + 1);
        movie.setRating(newRating);

        Movie saved = movieRepository.save(movie);

        return ResponseEntity.ok(movieMapper.toDTO(saved));
    }
}