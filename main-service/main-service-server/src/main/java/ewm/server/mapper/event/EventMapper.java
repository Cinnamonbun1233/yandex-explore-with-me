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
    private static final Function<List<ParticipationRequest>, Integer> CALCULATE_CONFIRMED_REQUEST_FUNC =
            list -> list == null || list.isEmpty() ? 0 : (int) list
                    .stream()
                    .filter(
                            participationRequest -> participationRequest
                                    .getRequestStatus()
                                    .equals(RequestStatus.CONFIRMED)
                    )
                    .count();
    private static final BiFunction<Long, StatsClient, Integer> GET_VIEWS_OF_EVENT_FUNC =
            (id, statsClient) -> {
                StatsResponseDto statsResponseDto = statsClient
                        .getStats(
                                "2000-01-01 00:00:00",
                                "2100-01-01 00:00:00",
                                List.of(String.format("/events/%d", id)),
                                Boolean.valueOf("true")
                        )
                        .blockFirst();

                return statsResponseDto == null ? 0 : statsResponseDto.getHits().intValue();
            };

    public static Event newEventDtoToEvent(NewEventDto newEventDto) {

        Event event = new Event();

        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setPaid(newEventDto.getPaid() != null && newEventDto.getPaid());
        event.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), DATE_TIME_FORMAT));
        event.setParticipationLimit(newEventDto.getParticipantLimit() == null ? 0 : newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration() == null || newEventDto.getRequestModeration());
        event.setTitle(newEventDto.getTitle());

        return event;
    }

    public static EventFullDto eventToEventFullDto(Event event, StatsClient statsClient) {

        return EventFullDto
                .builder()
                .id(event.getEventId())
                .annotation(event.getAnnotation())
                .paid(event.getPaid())
                .category(CategoryMapper.categoryToCategoryDto(event.getCategory()))
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

    public static EventShortDto eventToEventShortDto(Event event, StatsClient statsClient) {

        return EventShortDto
                .builder()
                .id(event.getEventId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.categoryToCategoryDto(event.getCategory()))
                .confirmedRequests(CALCULATE_CONFIRMED_REQUEST_FUNC.apply(event.getRequests()))
                .eventDate(event.getEventDate().format(DATE_TIME_FORMAT))
                .initiator(UserMapper.mapModelToShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(GET_VIEWS_OF_EVENT_FUNC.apply(event.getEventId(), statsClient))
                .build();
    }
}