package movies.services;

import movies.dto.MovieDTO;
import movies.exception.ImportMovieException;
import movies.mapper.MovieMapper;
import movies.models.Actor;
import movies.models.Director;
import movies.models.Movie;
import movies.repositories.ActorRepository;
import movies.repositories.DirectorRepository;
import movies.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class MovieImportService {

    @Value("${omdb.api.key}")
    private String apiKey;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MovieMapper movieMapper;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private DirectorRepository directorRepository;
    @Autowired
    private ActorRepository actorRepository;


    public MovieDTO importFromOMDb(String title) {
        String url = "http://www.omdbapi.com/?t=" + title + "&apikey=" + apiKey;
        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) restTemplate.getForObject(url, Map.class);

        if (response == null || !"True".equalsIgnoreCase((String) response.get("Response"))) {
            throw new ImportMovieException("Movie not found in OMDb: " + title);
        }
            // 1. Director
            String directorName = (String) response.get("Director");
            Director director = directorRepository.findByName(directorName)
                    .orElseGet(() -> directorRepository.save(new Director(directorName)));

            // 2. Movie
            Movie movie = new Movie();
            movie.setTitle((String) response.get("Title"));
            movie.setDescription((String) response.get("Plot"));
            movie.setYear(Integer.parseInt(((String) response.get("Year")).substring(0, 4)));
            movie.setImage((String) response.get("Poster"));
            movie.setDirector(director);
            movie.setVotes(0);
            movie.setRating(0.0);

            // 3. Actors
            String actorsRaw = (String) response.get("Actors");

            if (actorsRaw != null) {
                String[] actorsList = actorsRaw.split(", ");
                Set<Actor> actorsSet = new HashSet<>();

                for (String actorName : actorsList) {
                    Actor actor = actorRepository.findByName(actorName)
                            .orElseGet(() -> actorRepository.save(new Actor(actorName)));
                    actorsSet.add(actor);
                }

                movie.setActors(actorsSet);
            }
            Movie movieSaved = movieRepository.save(movie);
            return movieMapper.toDTO(movieSaved);
    }
}
