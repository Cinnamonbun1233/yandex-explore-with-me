package ewm.server.controller.place;

import ewm.server.dto.event.EventShortDto;
import ewm.server.dto.event.LocationDto;
import ewm.server.dto.place.PlaceDto;
import ewm.server.service.place.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class PlaceController {
    private static final String ADMIN_PLACES_PATH = "/admin/places";
    private static final String PUBLIC_PLACE_PATH = "/places/{placeId}";
    private static final String PRIVATE_PLACE_PATH = "/users/{userId}/places";

    private final PlaceService placeService;

    @Autowired
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @PostMapping(value = ADMIN_PLACES_PATH)
    public ResponseEntity<PlaceDto> addPlace(@Valid @RequestBody PlaceDto placeDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(placeService.createNewPlace(placeDto));
    }

    @GetMapping(ADMIN_PLACES_PATH)
    public ResponseEntity<List<PlaceDto>> getAllPlaces() {
        return ResponseEntity.ok().body(placeService.getAllPlaces());
    }

    @GetMapping(PUBLIC_PLACE_PATH)
    public ResponseEntity<List<EventShortDto>> getEventsNearbyPlace(@PathVariable("placeId") Long placeId) {
        return ResponseEntity.ok().body(placeService.getEventsNearbyPlace(placeId));
    }

    @GetMapping(PRIVATE_PLACE_PATH)
    public ResponseEntity<List<EventShortDto>> getEventsNearbyUsersLocation(@PathVariable("userId") Long userId,
                                                                            @Valid @RequestBody LocationDto usersLocation,
                                                                            @RequestParam(name = "radius", required = false, defaultValue = "1") Long radius) {
        return ResponseEntity.ok().body(placeService.getEventsNearbyUsersLocation(userId, usersLocation, radius));
    }
}