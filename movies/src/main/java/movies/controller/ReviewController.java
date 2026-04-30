package movies.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import movies.dto.ReviewDTO;
import movies.exception.ResourceNotFoundException;
import movies.mapper.ReviewMapper;
import movies.models.Movie;
import movies.models.Review;
import movies.repositories.MovieRepository;
import movies.repositories.ReviewRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "API for managing movie reviews")
public class ReviewController {

    private final ReviewMapper reviewMapper;
    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;

    public ReviewController(ReviewMapper reviewMapper,
                            ReviewRepository reviewRepository,
                            MovieRepository movieRepository) {
        this.reviewMapper = reviewMapper;
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
    }

    @GetMapping
    @Operation(summary = "Get all reviews", description = "Returns all reviews in the system")
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {

        List<ReviewDTO> reviews = reviewRepository.findAll()
                .stream()
                .map(reviewMapper::toDTO)
                .toList();

        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID", description = "Returns a specific review by its ID")
    public ResponseEntity<ReviewDTO> getReviewById(
            @Parameter(description = "Review ID")
            @PathVariable final Long id) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));

        return ResponseEntity.ok(reviewMapper.toDTO(review));
    }

    @GetMapping("/movie/{movieId}")
    @Operation(summary = "Get reviews by movie", description = "Returns all reviews of a specific movie")
    public ResponseEntity<List<ReviewDTO>> getReviewsByMovie(
            @Parameter(description = "Movie ID")
            @PathVariable final Long movieId) {

        List<ReviewDTO> reviews = reviewRepository.findByMovieId(movieId)
                .stream()
                .map(reviewMapper::toDTO)
                .toList();

        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    @Operation(summary = "Create review", description = "Creates a new review for a movie")
    public ResponseEntity<ReviewDTO> createReview(
            @Parameter(description = "Review data")
            @Valid @RequestBody final ReviewDTO dto) {

        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + dto.getMovieId()));

        Review review = new Review();
        review.setComment(dto.getComment());
        review.setRating(dto.getRating());
        review.setMovie(movie);

        Review saved = reviewRepository.save(review);
        movie.calculateRating(saved.getRating());
        movieRepository.save(movie);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update review", description = "Updates an existing review")
    public ResponseEntity<ReviewDTO> updateReview(
            @Parameter(description = "Review ID")
            @PathVariable final Long id,
            @Valid @RequestBody final ReviewDTO dto) {

        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));

        existing.setComment(dto.getComment());
        existing.setRating(dto.getRating());
        Review updated = reviewRepository.save(existing);

        return ResponseEntity.ok(reviewMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete review", description = "Deletes a review by ID")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "Review ID")
            @PathVariable final Long id) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));

        reviewRepository.delete(review);
        return ResponseEntity.noContent().build();
    }
}