package ewm.server.service.place;

import ewm.server.dto.event.EventShortDto;
import ewm.server.dto.event.LocationDto;
import ewm.server.dto.place.PlaceDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceServiceImpl implements PlaceService {
    @Override
    public PlaceDto addPlace(PlaceDto placeDto) {
        return null;
    }

    @Override
    public List<PlaceDto> getAllPlaces() {
        return null;
    }

    @Override
    public List<EventShortDto> getEventsNearbyPlace(Long placeId) {
        return null;
    }

    @Override
    public List<EventShortDto> getEventsNearbyUsersLocation(Long userId, LocationDto usersLocation) {
        return null;
    }
}