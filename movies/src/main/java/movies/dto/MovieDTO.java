package movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MovieDTO {
    private Long id;
    private String title;
    private Integer year;
    private String name;
    private List<String> listOfActors;
    private Integer numberOfActors;
    private List<String> listOfGenres;
    private String image;
    private String description;
}
