package ewm.server.mapper.place;

import ewm.server.dto.place.PlaceDto;
import ewm.server.model.place.Place;

public class PlaceMapper {
    public static Place placeDtoToPlace(PlaceDto placeDto) {

        Place place = new Place();

        place.setName(placeDto.getName());
        place.setLatitude(placeDto.getLatitude());
        place.setLongitude(placeDto.getLongitude());
        place.setRadius(placeDto.getRadius());

        return place;
    }

    public static PlaceDto placeToPlaceDto(Place place) {

        return PlaceDto
                .builder()
                .placeId(place.getPlaceId())
                .name(place.getName())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .radius(place.getRadius())
                .build();
    }
}