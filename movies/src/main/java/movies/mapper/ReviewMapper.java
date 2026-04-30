package movies.mapper;

import movies.dto.ReviewDTO;
import movies.models.Review;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewMapper {
    public ReviewDTO toDTO (Review review){
        Long movieId = review.getMovie() != null
                ? review.getMovie().getId()
                : null;
        return new ReviewDTO(
                review.getId(),
                review.getComment(),
                review.getRating(),
                movieId
        );
    }

    public Review toEntity(ReviewDTO reviewDTO){
        Review review = new Review();
        review.setComment(reviewDTO.getComment());
        review.setRating(reviewDTO.getRating());
        return review;
    }
}
