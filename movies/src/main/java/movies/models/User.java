package movies.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import movies.repositories.ReviewRepository;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Review> reviews = new HashSet<>();

    public User() {}

        public User(Long id, String username, String email, String password, Set<Review> reviews) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.reviews = reviews;
    }
}