package ewm.server.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "records")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class StatsRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "app")
    String app;
    @Column(name = "uri")
    String uri;
    @Column(name = "ip")
    String ip;
    @Column(name = "time_stamp")
    LocalDateTime timestamp;
}