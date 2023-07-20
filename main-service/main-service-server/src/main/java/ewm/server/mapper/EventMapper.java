package ewm.server.mapper;

import ewm.server.dto.EventFullDto;
import ewm.server.dto.NewEventDto;
import ewm.server.model.event.Event;

import java.time.format.DateTimeFormatter;

public class EventMapper {
    private static final DateTimeFormatter REQUEST_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event mapDtoToModel(NewEventDto dto) {
        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setPaid(dto.getPaid());
        event.setParticipationLimit(dto.getParticipationLimit());
        event.setRequestModeration(dto.getRequestModeration());
        event.setTitle(dto.getTitle());
        return event;
    }

    public static EventFullDto mapModelToDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .paid(event.getPaid())
                .category(CategoryMapper.mapModelToDto(event.getCategory()))
                //TODO: add logic
                .confirmedRequests(0)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .state(event.getEventStatus())
                .title(event.getTitle())
                //TODO: get from stats
                .views(0)
                .initiator(UserMapper.mapModelToDto(event.getInitiator()))
                .location(LocationMapper.mapModelToDto(event.getLocation()))
                .participationLimit(event.getParticipationLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .build();
    }
}