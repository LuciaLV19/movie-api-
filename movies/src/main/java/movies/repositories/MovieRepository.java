package movies.repositories;

import movies.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByActorsId (Long actorId);
    List<Movie> findByDirectorId(Long directorId);
    List<Movie> findByGenresId(Long genreId);

    List<Movie> findByTitleContainingIgnoreCase (String title);
}
