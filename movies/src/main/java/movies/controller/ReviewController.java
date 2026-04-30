package movies.controller;

import movies.dto.ReviewDTO;
import movies.mapper.MovieMapper;
import movies.mapper.ReviewMapper;
import movies.models.Movie;
import movies.models.Review;
import movies.repositories.MovieRepository;
import movies.repositories.ReviewRepository;
import movies.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getAllReviews (){
        List<ReviewDTO> reviews = reviewRepository.findAll().stream()
                .map(reviewMapper::toDTO)
                .toList();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable final Long id) {
        return reviewRepository.findById(id)
                .map(reviewMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByMovie(@PathVariable Long movieId) {
        List<ReviewDTO> reviews = reviewRepository.findByMovieId(movieId).stream()
                .map(reviewMapper::toDTO)
                .toList();
        return ResponseEntity.ok(reviews);
    }

    // Create review
    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO dto) {
        Review review = new Review();
        review.setComment(dto.getComment());
        review.setRating(dto.getRating());
        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        review.setMovie(movie);
        Review saved = reviewRepository.save(review);
        // actualizar rating
        movie.calculateRating(saved.getRating());
        movieRepository.save(movie);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewMapper.toDTO(saved));
    }

    // Update review
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id, @RequestBody ReviewDTO dto) {
        return reviewRepository.findById(id)
                .map(existing -> {
                    existing.setComment(dto.getComment());
                    existing.setRating(dto.getRating());
                    return reviewRepository.save(existing);
                })
                .map(reviewMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete review
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        if (!reviewRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reviewRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

