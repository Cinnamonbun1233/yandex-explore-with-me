package ewm.server.mapper;

import ewm.server.dto.LocationDto;
import ewm.server.model.event.Location;

public class LocationMapper {
    public static Location mapDtoToModel(LocationDto dto) {
        Location location = new Location();
        location.setLat(dto.getLat());
        location.setLon(dto.getLon());
        return location;
    }

    public static LocationDto mapModelToDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}