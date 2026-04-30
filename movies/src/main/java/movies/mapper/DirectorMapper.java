package movies.mapper;

import movies.dto.DirectorDTO;
import movies.models.Actor;
import movies.models.Director;
import movies.models.Movie;
import org.springframework.stereotype.Component;

import javax.swing.text.html.parser.Entity;
import java.util.List;

@Component
public class DirectorMapper {
    public DirectorDTO toDTO (Director director){
        List<String> listOfMovies = director.getMovies() != null
                ? director.getMovies().stream().map(Movie::getTitle).toList()
                : List.of();

        return new DirectorDTO(
                director.getId(),
                director.getName(),
                director.getBirthDate(),
                director.getNationality(),
                listOfMovies
        );
    }

    public Director toEntity(DirectorDTO directorDTO){
        Director director = new Director();
        director.setName(directorDTO.getName());
        director.setBirthDate(directorDTO.getBirthDate());
        director.setNationality(directorDTO.getNationality());
        return director;
    }
}
