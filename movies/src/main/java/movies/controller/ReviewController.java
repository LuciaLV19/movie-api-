package movies.controller;

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
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews (){
        List<Review> reviews = reviewRepository.findAll();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable final Long id) {
        return reviewRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Review>> getReviewsByMovie(@PathVariable Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reviewRepository.findByMovieId(movieId));
    }

    // Create review
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        if (review.getMovie() == null || !movieRepository.existsById(review.getMovie().getId())) {
            return ResponseEntity.badRequest().build();
        }
        if (review.getUser() == null || !userRepository.existsById(review.getUser().getId())) {
            return ResponseEntity.badRequest().build();
        }

        Review savedReview = reviewRepository.save(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
    }

    // Update review
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable final Long id, @RequestBody final Review updatedReview) {
        return reviewRepository.findById(id)
                .map(existingReview -> {
                    existingReview.setComment(updatedReview.getComment());
                    existingReview.setRating(updatedReview.getRating());

                    Review savedReview = reviewRepository.save(existingReview);
                    return ResponseEntity.ok(savedReview);
                })
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

