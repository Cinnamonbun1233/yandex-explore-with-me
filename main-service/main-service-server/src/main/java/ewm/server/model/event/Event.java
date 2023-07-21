package ewm.server.model.event;

import ewm.server.model.category.Category;
import ewm.server.model.request.ParticipationRequest;
import ewm.server.model.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "title")
    String title;
    @Column(name = "annotation")
    @NotBlank
    @NotEmpty
    @NotNull
    @Length(min = 20, max = 2000)
    String annotation;
    @Column(name = "description")
    @NotBlank
    @NotEmpty
    @NotNull
    @Length(min = 20, max = 7000)
    String description;
    @Column(name = "event_date")
    LocalDateTime eventDate;
    @Column(name = "created_on")
    LocalDateTime createdOn;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "paid")
    Boolean paid;
    @Column(name = "request_moderation")
    Boolean requestModeration;
    @Column(name = "participation_limit")
    Integer participationLimit;
    @Enumerated(EnumType.STRING)
    EventStatus eventStatus;
    @OneToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    Location location;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User initiator;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;
    @OneToMany(
            targetEntity = ParticipationRequest.class,
            mappedBy = "event",
            fetch = FetchType.LAZY
    )
    List<ParticipationRequest> requests;
}