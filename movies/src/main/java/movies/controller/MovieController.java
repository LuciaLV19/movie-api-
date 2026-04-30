package movies.controller;

import movies.dto.ActorDTO;
import movies.dto.MovieDTO;
import movies.dto.ReviewDTO;
import movies.mapper.ActorMapper;
import movies.mapper.MovieMapper;
import movies.mapper.ReviewMapper;
import movies.models.Movie;
import movies.repositories.ActorRepository;
import movies.repositories.MovieRepository;
import movies.repositories.ReviewRepository;
import movies.services.MovieImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/movies")
public class MovieController {
    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private ActorMapper actorMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MovieImportService movieImportService;

    // Get all movies
    @GetMapping
    public ResponseEntity<List<MovieDTO>> getMovies(@RequestParam(required = false) String title) {
        List<Movie> movies = (title != null && !title.isEmpty())
                ? movieRepository.findByTitleContainingIgnoreCase(title)
                : movieRepository.findAll();

        List<MovieDTO> movieDtoList = movies.stream()
                .map(movieMapper::toDTO)
                .toList();

        return ResponseEntity.ok(movieDtoList);
    }

    // Get movie by ID
    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable final Long id) {
        return movieRepository.findById(id)
                .map(movieMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all actors by movie
    @GetMapping("/{id}/actors")
    public ResponseEntity<List<ActorDTO>> getActorsByMovie(@PathVariable Long id) {
        if (!movieRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        List<ActorDTO> actors = actorRepository.findByMoviesId(id).stream()
                .map(actorMapper::toDTO)
                .toList();
        return ResponseEntity.ok(actors);
    }

    // Get all reviews by movie
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewsByMovie(@PathVariable Long id) {
        if (!movieRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        List<ReviewDTO> reviews = reviewRepository.findByMovieId(id).stream()
                .map(reviewMapper::toDTO)
                .toList();
        return ResponseEntity.ok(reviews);
    }

    // Create movie
    @PostMapping
    public ResponseEntity<MovieDTO> createMovie(@RequestBody final MovieDTO dto) {
        Movie movie = movieMapper.toEntity(dto);
        Movie savedMovie = movieRepository.save(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(movieMapper.toDTO(savedMovie));
    }

    @PostMapping("/import/{title}")
    public ResponseEntity<MovieDTO> importMovie(@PathVariable String title) {
        // Asumiendo que importFromOMDb ahora devuelve un MovieDTO como hablamos
        MovieDTO imported = movieImportService.importFromOMDb(title);
        if(imported == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(imported);
    }

    // Update movie
    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable final Long id, @RequestBody MovieDTO dto) {
        return movieRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(dto.getTitle());
                    existing.setYear(dto.getYear());
                    existing.setDescription(dto.getDescription());
                    existing.setImage(dto.getImage());
                    return movieRepository.save(existing);
                })
                .map(movieMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete movie
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable final Long id) {
        if (!movieRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        movieRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Vote movie
    @PostMapping("/{id}/vote")
    public ResponseEntity<MovieDTO> voteMovie(@PathVariable final Long id, @RequestParam final double rating) {
        return movieRepository.findById(id)
                .map(movie -> {
                    double newRating = ((movie.getVotes() * movie.getRating()) + rating) / (movie.getVotes() + 1);
                    movie.setVotes(movie.getVotes() + 1);
                    movie.setRating(newRating);
                    return movieRepository.save(movie);
                })
                .map(movieMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}