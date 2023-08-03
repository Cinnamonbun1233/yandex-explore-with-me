package ewm.server.repo;

import ewm.dto.StatsResponseDto;
import ewm.server.model.StatsRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepo extends JpaRepository<StatsRecord, Long> {
    @Query("SELECT NEW ewm.dto.StatsResponseDto(r.app, r.uri, COUNT(r.uri)) " +
           "FROM StatsRecord AS r " +
           "WHERE r.timestamp BETWEEN :start AND :end " +
           "AND ((:uris) IS NULL OR r.uri IN (:uris)) " +
           "GROUP BY r.app, r.uri " +
           "ORDER BY COUNT(r.uri) DESC")
    List<StatsResponseDto> getStatsForDatesAndUris(@Param("start") LocalDateTime startPeriod,
                                                   @Param("end") LocalDateTime endPeriod,
                                                   @Param("uris") List<String> uris);

    @Query("SELECT NEW ewm.dto.StatsResponseDto(r.app, r.uri, COUNT(DISTINCT r.uri)) " +
           "FROM StatsRecord AS r " +
           "WHERE r.timestamp BETWEEN :start AND :end " +
           "AND ((:uris) IS NULL OR r.uri IN (:uris)) " +
           "GROUP BY r.app, r.uri " +
           "ORDER BY COUNT(DISTINCT r.uri) DESC")
    List<StatsResponseDto> getStatsForDatesAndUrisWithUniqueIp(@Param("start") LocalDateTime startPeriod,
                                                               @Param("end") LocalDateTime endPeriod,
                                                               @Param("uris") List<String> uris);
}