package ewm.server.service.event;

import ewm.server.dto.event.EventFullDto;
import ewm.server.dto.event.LocationDto;
import ewm.server.dto.event.NewEventDto;
import ewm.server.exception.CategoryNotFoundException;
import ewm.server.exception.UserNotFoundException;
import ewm.server.mapper.EventMapper;
import ewm.server.mapper.LocationMapper;
import ewm.server.model.category.Category;
import ewm.server.model.event.Event;
import ewm.server.model.event.EventStatus;
import ewm.server.model.event.Location;
import ewm.server.model.user.User;
import ewm.server.repo.category.CategoryRepo;
import ewm.server.repo.event.EventRepo;
import ewm.server.repo.event.LocationRepo;
import ewm.server.repo.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final UserRepo userRepo;
    private final CategoryRepo categoryRepo;
    private final EventRepo eventRepo;
    private final LocationRepo locationRepo;

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        Event newEvent = EventMapper.mapDtoToModel(newEventDto);
        newEvent.setCategory(getCategory(newEventDto.getCategory()));
        newEvent.setLocation(saveLocation(newEventDto.getLocation()));
        newEvent.setInitiator(getInitiator(userId));
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setEventStatus(EventStatus.PENDING);
        return EventMapper.mapModelToDto(eventRepo.save(newEvent));
    }

    private Location saveLocation(LocationDto locationDto) {
        return locationRepo.save(LocationMapper.mapDtoToModel(locationDto));
    }


    private Category getCategory(Integer categoryId) {
        return categoryRepo.findById(categoryId.longValue()).orElseThrow(() -> {
            throw new CategoryNotFoundException("Category does not exist");
        });
    }

    private User getInitiator(Long userId) {
        return userRepo.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("User does not exist");
        });
    }
}