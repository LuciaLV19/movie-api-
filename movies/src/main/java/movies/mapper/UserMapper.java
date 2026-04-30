package movies.mapper;

import movies.dto.ReviewDTO;
import movies.dto.UserCreateDTO;
import movies.dto.UserDTO;
import movies.models.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public UserDTO toDTO (User user){
        List<ReviewDTO> listOfReviews = user.getReviews() != null
                ? user.getReviews().stream()
                    .map(review -> new ReviewDTO(
                        review.getId(),
                        review.getComment(),
                        review.getRating(),
                        review.getMovie() != null ? review.getMovie().getId() : null
                )).toList()
                : List.of();

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                listOfReviews
        );
    }

    public User toEntity(UserCreateDTO userCreateDTO){
        User user = new User();
        user.setUsername(userCreateDTO.getUsername());
        user.setEmail(userCreateDTO.getEmail());
        user.setPassword(userCreateDTO.getPassword());
        return user;
    }
}
