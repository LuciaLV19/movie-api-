package movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ActorDTO {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private String nationality;
    private List<String> movies;
}
