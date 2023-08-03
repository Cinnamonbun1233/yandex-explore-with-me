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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(
        level = AccessLevel.PRIVATE,
        makeFinal = true
)
public class PlaceServiceImpl implements PlaceService {
    private final UserRepo userRepo;
    private final PlaceRepo placeRepo;
    private final EventRepo eventRepo;
    private final StatsClient statsClient;

    @Transactional
    @Override
    public PlaceDto createNewPlace(PlaceDto placeDto) {

        Place place = PlaceMapper.placeDtoToPlace(placeDto);

        return PlaceMapper.placeToPlaceDto(placeRepo.save(place));
    }

    @Transactional(readOnly = true)
    @Override
    public List<PlaceDto> getAllPlaces() {

        return placeRepo
                .findAll()
                .stream()
                .map(PlaceMapper::placeToPlaceDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<EventShortDto> getEventsNearbyPlace(Long placeId) {

        Place place = placeRepo.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(String.format("Place %d has not been added by admin", placeId)));

        return eventRepo
                .findEventsNearby(place.getLongitude(), place.getLatitude(), place.getRadius())
                .stream()
                .filter(event -> event.getEventStatus().equals(EventStatus.PUBLISHED))
                .map(event -> EventMapper.eventToEventShortDto(event, statsClient))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<EventShortDto> getEventsNearbyUsersLocation(Long userId, LocationDto locationDto, Long radius) {

        checkIfUserExists(userId);

        return eventRepo
                .findEventsNearby(locationDto.getLon(), locationDto.getLat(), radius.doubleValue())
                .stream()
                .filter(event -> event.getEventStatus().equals(EventStatus.PUBLISHED))
                .map(event -> EventMapper.eventToEventShortDto(event, statsClient))
                .collect(Collectors.toList());
    }

    private void checkIfUserExists(Long userId) {

        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException(String.format("User %d does not exist", userId));
        }
    }
}