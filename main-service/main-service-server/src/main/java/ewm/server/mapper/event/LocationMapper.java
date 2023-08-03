package ewm.server.mapper.event;

import ewm.server.dto.event.LocationDto;
import ewm.server.model.event.Location;

public class LocationMapper {
    public static Location locationDtoToLocation(LocationDto locationDto) {

        Location location = new Location();

        location.setLat(locationDto.getLat());
        location.setLon(locationDto.getLon());

        return location;
    }

    public static LocationDto locationToLocationDto(Location location) {

        return LocationDto
                .builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}