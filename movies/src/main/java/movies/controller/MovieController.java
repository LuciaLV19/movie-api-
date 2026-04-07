package movies.controller;

import movies.models.Actor;
import movies.models.Movie;
import movies.models.Review;
import movies.repositories.ActorRepository;
import movies.repositories.MovieRepository;
import movies.repositories.ReviewRepository;
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
    private MovieRepository movieRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    // Get all movies
    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        return ResponseEntity.ok(movies);
    }

    // Get movie by ID
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable final Long id) {
        return movieRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all actors by movie
    @GetMapping("/{id}/actors")
    public ResponseEntity<List<Actor>> getActorsByMovie(@PathVariable Long id) {
        if (!movieRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        List<Actor> actors = actorRepository.findByMoviesId(id);
        return ResponseEntity.ok(actors);
    }

    // Get all reviews by movie
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<Review>> getReviewsByMovie(@PathVariable Long id) {
        if (!movieRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        List<Review> reviews = reviewRepository.findByMovieId(id);
        return ResponseEntity.ok(reviews);
    }

    // Create movie
    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody final Movie movie) {
        Movie savedMovie = movieRepository.save(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMovie);
    }

    // Update movie
    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable final Long id,
                                             @RequestBody final Movie updatedMovie) {
        if (!movieRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        updatedMovie.setId(id);
        Movie savedMovie = movieRepository.save(updatedMovie);
        return ResponseEntity.ok(savedMovie);
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
    public ResponseEntity<Movie> voteMovie(@PathVariable final Long id,
                                           @RequestParam final double rating) {
        return movieRepository.findById(id)
                .map(movie -> {
                    double newRating = ((movie.getVotes() * movie.getRating()) + rating) / (movie.getVotes() + 1);
                    movie.setVotes(movie.getVotes() + 1);
                    movie.setRating(newRating);
                    Movie savedMovie = movieRepository.save(movie);
                    return ResponseEntity.ok(savedMovie);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}