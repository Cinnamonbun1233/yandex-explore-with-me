package ewm.server.repo.event;

import ewm.server.model.event.Event;
import ewm.server.model.event.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepo extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    Optional<Event> findByEventIdAndEventStatus(Long eventId, EventStatus eventStatus);

    @Query(value = "select * from events as e " +
                   "join locations as l on e.location_id = l.location_id " +
                   "where distance(l.lat, l.lon, :lat, :lon) < :radius", nativeQuery = true)
    List<Event> findEventsNearby(Double lon, Double lat, Double radius);
}