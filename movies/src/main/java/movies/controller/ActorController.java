package movies.controller;

import movies.dto.ActorDTO;
import movies.dto.MovieDTO;
import movies.mapper.ActorMapper;
import movies.mapper.MovieMapper;
import movies.models.Actor;
import movies.repositories.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/actors")
public class ActorController {
    @Autowired
    private ActorMapper actorMapper;

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private ActorRepository actorRepository;

    // Get all actors
    @GetMapping
    public ResponseEntity<List<ActorDTO>> getAllActors() {
        List<ActorDTO> actors = actorRepository.findAll().stream()
                .map(actorMapper::toDTO)
                .toList();
        return ResponseEntity.ok(actors);
    }

    // Get actor by ID
    @GetMapping("/{id}")
    public ResponseEntity<ActorDTO> getActorById(@PathVariable final Long id) {
        return actorRepository.findById(id)
                .map(actorMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // All movies by actor
    @GetMapping("/{id}/movies")
    public ResponseEntity<List<MovieDTO>> getMoviesByActor(@PathVariable final Long id) {
        return actorRepository.findById(id)
                .map(actor -> actor.getMovies().stream()
                        .map(movieMapper::toDTO)
                        .toList())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create actor
    @PostMapping
    public ResponseEntity<ActorDTO> createActor(@RequestBody ActorDTO dto) {
        Actor actor = actorMapper.toEntity(dto);
        Actor saved = actorRepository.save(actor);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(actorMapper.toDTO(saved));
    }

    // Update actor
    @PutMapping("/{id}")
    public ResponseEntity<ActorDTO> updateActor(@PathVariable Long id, @RequestBody ActorDTO dto) {
        return actorRepository.findById(id)
                .map(existing -> {
                    existing.setName(dto.getName());
                    existing.setBirthDate(dto.getBirthDate());
                    existing.setNationality(dto.getNationality());
                    return actorRepository.save(existing);
                })
                .map(actorMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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
}