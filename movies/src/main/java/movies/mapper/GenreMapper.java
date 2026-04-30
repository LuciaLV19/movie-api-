package movies.mapper;

import movies.dto.GenreDTO;
import movies.models.Genre;
import movies.models.Movie;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class GenreMapper {
    public GenreDTO toDTO(Genre genre){
        List<String> listOfMovies = genre.getMovies() != null
                ? genre.getMovies().stream().map(Movie::getTitle).toList()
                : List.of();

        return new GenreDTO(
                genre.getId(),
                genre.getName(),
                listOfMovies
        );
    }

    public Genre toEntity(GenreDTO genreDTO){
        Genre genre = new Genre();
        genre.setName(genreDTO.getName());
        return genre;
    }
}
