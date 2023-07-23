package ewm.server.mapper.event;

import ewm.client.StatsClient;
import ewm.dto.StatsResponseDto;
import ewm.server.dto.event.EventFullDto;
import ewm.server.dto.event.EventShortDto;
import ewm.server.dto.event.NewEventDto;
import ewm.server.mapper.category.CategoryMapper;
import ewm.server.mapper.user.UserMapper;
import ewm.server.model.event.Event;
import ewm.server.model.request.ParticipationRequest;
import ewm.server.model.request.RequestStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EventMapper {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Function<List<ParticipationRequest>, Integer> CALCULATE_CONFIRMED_REQUEST_FUNC = list ->
            list == null || list.isEmpty() ? 0 : (int) list.stream()
                    .filter(r -> r.getRequestStatus().equals(RequestStatus.CONFIRMED)).count();
    private static final BiFunction<Long, StatsClient, Integer> GET_VIEWS_OF_EVENT_FUNC = (id, statsClient) -> {
        StatsResponseDto stats = statsClient.getStats("2000-01-01 00:00:00",
                "2100-01-01 00:00:00",
                new String[]{String.format("/events/%d", id)}, "true").blockFirst();
        return stats == null ? 0 : stats.getHits().intValue();
    };

    public static Event mapDtoToModel(NewEventDto dto) {
        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setPaid(dto.getPaid() != null && dto.getPaid());
        event.setEventDate(LocalDateTime.parse(dto.getEventDate(), DATE_TIME_FORMAT));
        event.setParticipationLimit(dto.getParticipantLimit() == null ? 0 : dto.getParticipantLimit());
        event.setRequestModeration(dto.getRequestModeration() == null || dto.getRequestModeration());
        event.setTitle(dto.getTitle());
        return event;
    }

    public static EventFullDto mapModelToFullDto(Event event, StatsClient statsClient) {
        return EventFullDto.builder()
                .id(event.getEventId())
                .annotation(event.getAnnotation())
                .paid(event.getPaid())
                .category(CategoryMapper.mapModelToDto(event.getCategory()))
                .confirmedRequests(CALCULATE_CONFIRMED_REQUEST_FUNC.apply(event.getRequests()))
                .createdOn(event.getCreatedOn().format(DATE_TIME_FORMAT))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(DATE_TIME_FORMAT))
                .state(event.getEventStatus())
                .title(event.getTitle())
                .views(GET_VIEWS_OF_EVENT_FUNC.apply(event.getEventId(), statsClient))
                .initiator(UserMapper.mapModelToShortDto(event.getInitiator()))
                .location(LocationMapper.mapModelToDto(event.getLocation()))
                .participantLimit(event.getParticipationLimit())
                .publishedOn(event.getPublishedOn() == null ? "" : event.getPublishedOn().format(DATE_TIME_FORMAT))
                .requestModeration(event.getRequestModeration())
                .build();
    }

    public static EventShortDto mapModelToShortDto(Event event, StatsClient statsClient) {
        return EventShortDto.builder()
                .id(event.getEventId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapModelToDto(event.getCategory()))
                .confirmedRequests(CALCULATE_CONFIRMED_REQUEST_FUNC.apply(event.getRequests()))
                .eventDate(event.getEventDate().format(DATE_TIME_FORMAT))
                .initiator(UserMapper.mapModelToShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(GET_VIEWS_OF_EVENT_FUNC.apply(event.getEventId(), statsClient))
                .build();
    }
}