package movies.mapper;

import movies.dto.MovieDTO;
import movies.models.Actor;
import movies.models.Director;
import movies.models.Genre;
import movies.models.Movie;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MovieMapper {
        public MovieDTO toDTO(Movie movie) {
            List<String> listOfActors = movie.getActors() != null
                    ? movie.getActors().stream().map(Actor::getName).collect(Collectors.toList())
                    : List.of();

            List<String> listOfGenres = movie.getGenres() !=null
                    ? movie.getGenres().stream().map(Genre::getName).collect(Collectors.toList())
                    : List.of();

            int numberOfActors = listOfActors.size();
            String directorName = Optional.ofNullable(movie.getDirector())
                    .map(Director::getName)
                    .orElse(null);

            return new MovieDTO(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getYear(),
                    directorName,
                    listOfActors,
                    numberOfActors,
                    listOfGenres,
                    movie.getImage(),
                    movie.getDescription()
            );
        }

    public Movie toEntity(MovieDTO movieDTO) {
        Movie movie = new Movie();
        movie.setTitle(movieDTO.getTitle());
        movie.setYear(movieDTO.getYear());
        movie.setImage(movieDTO.getImage());
        movie.setDescription(movieDTO.getDescription());
        return movie;
    }
}

