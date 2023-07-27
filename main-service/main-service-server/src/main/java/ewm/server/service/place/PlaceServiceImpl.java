package ewm.server.service.place;

import ewm.client.StatsClient;
import ewm.server.dto.event.EventShortDto;
import ewm.server.dto.event.LocationDto;
import ewm.server.dto.place.PlaceDto;
import ewm.server.exception.place.PlaceNotFoundException;
import ewm.server.exception.user.UserNotFoundException;
import ewm.server.mapper.event.EventMapper;
import ewm.server.mapper.place.PlaceMapper;
import ewm.server.model.event.EventStatus;
import ewm.server.model.place.Place;
import ewm.server.repo.event.EventRepo;
import ewm.server.repo.place.PlaceRepo;
import ewm.server.repo.user.UserRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlaceServiceImpl implements PlaceService {
    UserRepo userRepo;
    PlaceRepo placeRepo;
    EventRepo eventRepo;
    StatsClient statsClient;

    @Override
    public PlaceDto addPlace(PlaceDto placeDto) {
        Place toBeAdded = PlaceMapper.mapDtoToModel(placeDto);
        return PlaceMapper.mapModelToDto(placeRepo.save(toBeAdded));
    }

    @Override
    public List<PlaceDto> getAllPlaces() {
        return placeRepo.findAll().stream().map(PlaceMapper::mapModelToDto).collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsNearbyPlace(Long placeId) {
        Place placeToBeSearched = placeRepo.findById(placeId).orElseThrow(() -> {
            throw new PlaceNotFoundException(String.format("Place %d has not been added by admin", placeId));
        });
        return eventRepo.findEventsNearby(placeToBeSearched.getLon(), placeToBeSearched.getLat(),
                        placeToBeSearched.getRadius())
                .stream()
                .filter(e -> e.getEventStatus().equals(EventStatus.PUBLISHED))
                .map(e -> EventMapper.mapModelToShortDto(e, statsClient))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsNearbyUsersLocation(Long userId, LocationDto usersLocation, Long radius) {
        checkIfUserExists(userId);
        return eventRepo.findEventsNearby(usersLocation.getLon(), usersLocation.getLat(), radius.doubleValue())
                .stream()
                .filter(e -> e.getEventStatus().equals(EventStatus.PUBLISHED))
                .map(e -> EventMapper.mapModelToShortDto(e, statsClient))
                .collect(Collectors.toList());
    }

    private void checkIfUserExists(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException(String.format("User %d does not exist", userId));
        }
    }
}