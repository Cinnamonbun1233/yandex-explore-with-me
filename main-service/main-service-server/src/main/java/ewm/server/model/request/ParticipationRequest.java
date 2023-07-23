package ewm.server.model.request;

import ewm.server.model.event.Event;
import ewm.server.model.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long requestId;
    @Column(name = "created")
    LocalDateTime created;
    @Enumerated(EnumType.STRING)
    RequestStatus requestStatus;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User requester;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    Event event;
}