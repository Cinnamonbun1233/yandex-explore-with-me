package ewm_server.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Column(name = "timestamp")
    LocalDateTime timestamp;
    @Column(name = "hits")
    Integer hits;
}