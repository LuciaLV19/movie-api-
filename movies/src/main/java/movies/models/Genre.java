package movies.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "genres")
public class Genre {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    private List<Movie> movies = new ArrayList<>();

    public Genre() {}

    public Genre (String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }

    public List<Movie> getMovies() {
        return movies;
    }
    public void setMovies(final List<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre)) return false;
        Genre genre = (Genre) o;
        return id != null && id.equals(genre.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
