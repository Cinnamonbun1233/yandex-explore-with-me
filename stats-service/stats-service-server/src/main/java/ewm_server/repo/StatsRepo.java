package ewm_server.repo;

import ewm_server.model.StatsRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepo extends JpaRepository<StatsRecord, Long> {

    List<StatsRecord> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<StatsRecord> findAllByTimestampBetweenAndUriIn(LocalDateTime start, LocalDateTime end, String[] uris);
}