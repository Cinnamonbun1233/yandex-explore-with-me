package ewm.server.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users", schema = "public",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "name")
    @NotBlank
    @NotEmpty
    @NotNull
    @Length(min = 2, max = 250)
    private String name;
    @Column(name = "email")
    @Email
    @NotBlank
    @NotEmpty
    @NotNull
    @Length(min = 6, max = 254)
    String email;
}