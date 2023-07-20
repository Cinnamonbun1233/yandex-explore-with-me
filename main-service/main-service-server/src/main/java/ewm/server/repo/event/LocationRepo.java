package ewm.server.repo.event;

import ewm.server.model.event.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepo extends JpaRepository<Location, Long> {
    Optional<Location> findByLonAndLat(Double lon, Double lat);
}