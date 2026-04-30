package movies.mapper;

import movies.dto.ActorDTO;
import movies.models.Actor;
import movies.models.Movie;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActorMapper {
    public ActorDTO toDTO (Actor actor){

        List<String> moviesList = actor.getMovies()!= null
                ?actor.getMovies().stream().map(Movie::getTitle).toList()
                : List.of();

        return new ActorDTO(
                actor.getId(),
                actor.getName(),
                actor.getBirthDate(),
                actor.getNationality(),
                moviesList
        );
    }

    public Actor toEntity(ActorDTO actorDTO) {
        Actor actor = new Actor();
        actor.setName(actorDTO.getName());
        actor.setBirthDate(actorDTO.getBirthDate());
        actor.setNationality(actorDTO.getNationality());
        return actor;
    }
}
