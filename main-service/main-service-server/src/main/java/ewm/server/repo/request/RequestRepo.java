package ewm.server.repo.request;

import ewm.server.model.event.ParticipationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepo extends JpaRepository<ParticipationRequest, Long> {
}