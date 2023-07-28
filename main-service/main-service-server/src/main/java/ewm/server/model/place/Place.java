package ewm.server.model.place;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "places", schema = "public")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    Long placeId;
    @Column(name = "name")
    String name;
    @Column(name = "lat")
    Double latitude;
    @Column(name = "lon")
    Double longitude;
    @Column(name = "radius")
    Double radius;
}