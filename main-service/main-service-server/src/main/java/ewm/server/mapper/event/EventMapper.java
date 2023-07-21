package ewm.server.mapper.event;

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
import java.util.function.Function;

public class EventMapper {
    private static final DateTimeFormatter REQUEST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Function<List<ParticipationRequest>, Integer> calculateConfirmedRequestFunc = list -> {
        if(list == null || list.isEmpty()) {
            return 0;
        } else {
            return (int) list.stream().filter(r -> r.getRequestStatus().equals(RequestStatus.CONFIRMED)).count();
        }
    };
    private static final Function<LocalDateTime, String> serializePublishOnFunc = time -> {
        if(time == null) {
            return "";
        } else {
            return time.format(REQUEST_TIME_FORMAT);
        }
    };

    public static Event mapDtoToModel(NewEventDto dto) {
        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setPaid(dto.getPaid() == null ? false : dto.getPaid());
        event.setEventDate(LocalDateTime.parse(dto.getEventDate(), REQUEST_TIME_FORMAT));
        event.setParticipationLimit(dto.getParticipantLimit() == null ? 0 : dto.getParticipantLimit());
        event.setRequestModeration(dto.getRequestModeration() == null ? true : dto.getRequestModeration());
        event.setTitle(dto.getTitle());
        return event;
    }

    public static EventFullDto mapModelToFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .paid(event.getPaid())
                .category(CategoryMapper.mapModelToDto(event.getCategory()))
                .confirmedRequests(calculateConfirmedRequestFunc.apply(event.getRequests()))
                .createdOn(event.getCreatedOn().format(REQUEST_TIME_FORMAT))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(REQUEST_TIME_FORMAT))
                .state(event.getEventStatus())
                .title(event.getTitle())
                //TODO: get from stats
                .views(0)
                .initiator(UserMapper.mapModelToShortDto(event.getInitiator()))
                .location(LocationMapper.mapModelToDto(event.getLocation()))
                .participantLimit(event.getParticipationLimit())
                .publishedOn(serializePublishOnFunc.apply(event.getPublishedOn()))
                .requestModeration(event.getRequestModeration())
                .build();
    }

    public static EventShortDto mapModelToShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapModelToDto(event.getCategory()))
                .confirmedRequests(calculateConfirmedRequestFunc.apply(event.getRequests()))
                .eventDate(event.getEventDate().format(REQUEST_TIME_FORMAT))
                .initiator(UserMapper.mapModelToShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                //TODO: get from stats
                .views(0)
                .build();
    }
}