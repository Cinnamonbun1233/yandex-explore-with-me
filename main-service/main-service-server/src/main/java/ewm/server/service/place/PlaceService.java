package ewm.server.service.place;

import ewm.server.dto.event.EventShortDto;
import ewm.server.dto.event.LocationDto;
import ewm.server.dto.place.PlaceDto;

import java.util.List;

public interface PlaceService {
    PlaceDto addPlace(PlaceDto placeDto);

    List<PlaceDto> getAllPlaces();

    List<EventShortDto> getEventsNearbyPlace(Long placeId);

    List<EventShortDto> getEventsNearbyUsersLocation(Long userId, LocationDto usersLocation);
}