package ewm_server.repo;

import ewm_server.model.StatsRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatsRepo extends JpaRepository<StatsRecord, Long> {

}