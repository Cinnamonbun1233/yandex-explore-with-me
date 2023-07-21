package ewm.server.model.user;

import ewm.server.model.event.Event;
import ewm.server.model.request.ParticipationRequest;
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
import java.util.List;

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
    @OneToMany(
            targetEntity = Event.class,
            mappedBy = "initiator",
            fetch = FetchType.LAZY
    )
    List<Event> events;
    @OneToMany(
            targetEntity = ParticipationRequest.class,
            mappedBy = "requester",
            fetch = FetchType.LAZY
    )
    List<ParticipationRequest> requests;
}
