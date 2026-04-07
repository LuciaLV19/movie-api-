package movies.controller;

import movies.models.Actor;
import movies.models.Movie;
import movies.repositories.ActorRepository;
import movies.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/actors")
public class ActorController {

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private MovieRepository movieRepository;

    // Get all actors
    @GetMapping
    public ResponseEntity<List<Actor>> getAllActors() {
        List<Actor> actors = actorRepository.findAll();
        return ResponseEntity.ok(actors);
    }

    // Get actor by ID
    @GetMapping("/{id}")
    public ResponseEntity<Actor> getActorById(@PathVariable final Long id) {
        Optional<Actor> actor = actorRepository.findById(id);
        return actor.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // All movies by actor
    @GetMapping("/{id}/movies")
    public ResponseEntity<List<Movie>> getMoviesByActor(@PathVariable final Long id) {
        return actorRepository.findById(id)
                .map(actor -> ResponseEntity.ok(actor.getMovies()))
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    // Create actor
    @PostMapping
    public ResponseEntity<Actor> createActor(@RequestBody final Actor actor) {
        Actor savedActor = actorRepository.save(actor);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedActor);
    }

    // Delete actor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable final Long id) {
        if (!actorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        actorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Update actor
    @PutMapping("/{id}")
    public ResponseEntity<Actor> updateActor(@PathVariable final Long id, @RequestBody final Actor updatedActor) {
        if (!actorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        updatedActor.setId(id);
        Actor savedActor = actorRepository.save(updatedActor);
        return ResponseEntity.ok(savedActor);
    }
}