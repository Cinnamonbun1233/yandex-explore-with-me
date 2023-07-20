package ewm.server.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotNull
    @NotEmpty
    @NotBlank
    @Length(min = 20, max = 2000)
    String annotation;
    Integer category;
    @NotNull
    @NotEmpty
    @NotBlank
    @Length(min = 20, max = 7000)
    String description;
    @NotNull
    @NotEmpty
    @NotBlank
    @Length(min = 3, max = 120)
    String title;
    String eventDate;
    LocationDto location;
    Boolean paid;
    Integer participationLimit;
    Boolean requestModeration;
}