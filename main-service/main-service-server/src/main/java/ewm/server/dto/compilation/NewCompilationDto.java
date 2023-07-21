package ewm.server.dto.compilation;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    List<Long> events;
    Boolean pinned;
    @NotEmpty
    @NotBlank
    @NotNull
    @Length(max = 50)
    String title;
}