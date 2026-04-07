package movies.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "directors")
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 50)
    private String nationality;

    @OneToMany(mappedBy = "director")
    @JsonIgnore
    private List<Movie> movies = new ArrayList<>();

    public Director() {}

    public Director(String name, LocalDate birthDate, String nationality) {
        this.name = name;
        this.birthDate = birthDate;
        this.nationality = nationality;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(final LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getNationality() {
        return nationality;
    }
    public void setNationality(final String nationality) {
        this.nationality = nationality;
    }


    public List<Movie> getMovies() {
        return movies;
    }
    public void setMovies(final List<Movie> movies) {
        this.movies = movies;
    }


    @Override
    public String toString() {
        return "Director{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", nationality='" + nationality + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Director)) return false;
        Director director = (Director) o;
        return id != null && id.equals(director.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
