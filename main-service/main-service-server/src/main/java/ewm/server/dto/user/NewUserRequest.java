package ewm.server.dto.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {
    @NotBlank
    @NotEmpty
    @NotNull
    @Length(min = 2, max = 250)
    String name;
    @Email
    @NotBlank
    @NotEmpty
    @NotNull
    @Length(min = 6, max = 254)
    String email;
}