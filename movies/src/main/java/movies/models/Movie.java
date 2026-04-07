package movies.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 1000)
    private String description;

    private int year;

    private int votes;

    private double rating;

    @Column(length = 255)
    private String image;

    @ManyToMany
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private List<Actor> actors = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "director_id")
    private Director director;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    public Movie() {}

    public Movie(String title, String description, int year, int votes, double rating, String image) {
        this.title = title;
        this.description = description;
        this.year = year;
        this.votes = votes;
        this.rating = rating;
        this.image = image;
    }

    public Long getId() { return id; }
    public void setId(final Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(final String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(final String description) { this.description = description; }

    public int getYear() { return year; }
    public void setYear(final int year) { this.year = year; }

    public int getVotes() { return votes; }
    public void setVotes(final int votes) { this.votes = votes; }

    public double getRating() { return rating; }
    public void setRating(final double rating) { this.rating = rating; }

    public String getImage() { return image; }
    public void setImage(final String image) { this.image = image; }

    public List<Actor> getActors() { return actors; }
    public void setActors(final List<Actor> actors) { this.actors = actors; }

    public List<Genre> getGenres() { return genres; }
    public void setGenres(final List<Genre> genres) { this.genres = genres; }

    public Director getDirector() { return director; }
    public void setDirector(final Director director) { this.director = director; }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", year=" + year +
                ", votes=" + votes +
                ", rating=" + rating +
                ", image='" + image + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return id != null && id.equals(movie.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}