package movies.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import movies.dto.ActorDTO;
import movies.dto.MovieDTO;
import movies.exception.ResourceNotFoundException;
import movies.mapper.ActorMapper;
import movies.mapper.MovieMapper;
import movies.models.Actor;
import movies.repositories.ActorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/actors")
@Tag(name = "Actors", description = "API for managing actors and their related movies")
public class ActorController {

    private final ActorMapper actorMapper;
    private final MovieMapper movieMapper;
    private final ActorRepository actorRepository;

    public ActorController(ActorMapper actorMapper,
                           MovieMapper movieMapper,
                           ActorRepository actorRepository) {
        this.actorMapper = actorMapper;
        this.movieMapper = movieMapper;
        this.actorRepository = actorRepository;
    }

    @GetMapping
    @Operation(summary = "Get all actors", description = "Returns all actors in the system")
    public ResponseEntity<List<ActorDTO>> getAllActors() {

        List<ActorDTO> actors = actorRepository.findAll()
                .stream()
                .map(actorMapper::toDTO)
                .toList();

        return ResponseEntity.ok(actors);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get actor by ID", description = "Returns a specific actor by their ID")
    public ResponseEntity<ActorDTO> getActorById(
            @Parameter(description = "Actor ID")
            @PathVariable final Long id) {

        Actor actor = actorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Actor not found with id: " + id)
                );

        return ResponseEntity.ok(actorMapper.toDTO(actor));
    }

    @GetMapping("/{id}/movies")
    @Operation(summary = "Get movies by actor", description = "Returns all movies featuring a specific actor")
    public ResponseEntity<List<MovieDTO>> getMoviesByActor(
            @Parameter(description = "Actor ID")
            @PathVariable final Long id) {

        Actor actor = actorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Actor not found with id: " + id)
                );

        List<MovieDTO> movies = actor.getMovies()
                .stream()
                .map(movieMapper::toDTO)
                .toList();

        return ResponseEntity.ok(movies);
    }

    @PostMapping
    @Operation(summary = "Create actor", description = "Creates a new actor")
    public ResponseEntity<ActorDTO> createActor(
            @Valid @RequestBody final ActorDTO dto) {

        Actor actor = actorMapper.toEntity(dto);
        Actor saved = actorRepository.save(actor);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(actorMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update actor", description = "Updates an existing actor")
    public ResponseEntity<ActorDTO> updateActor(
            @Parameter(description = "Actor ID")
            @PathVariable final Long id,
            @Valid @RequestBody final ActorDTO dto) {

        Actor existing = actorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Actor not found with id: " + id)
                );

        existing.setName(dto.getName());
        existing.setBirthDate(dto.getBirthDate());
        existing.setNationality(dto.getNationality());

        Actor updated = actorRepository.save(existing);

        return ResponseEntity.ok(actorMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete actor", description = "Deletes an actor by ID")
    public ResponseEntity<Void> deleteActor(
            @Parameter(description = "Actor ID")
            @PathVariable final Long id) {

        Actor actor = actorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Actor not found with id: " + id)
                );

        actorRepository.delete(actor);

        return ResponseEntity.noContent().build();
    }
}