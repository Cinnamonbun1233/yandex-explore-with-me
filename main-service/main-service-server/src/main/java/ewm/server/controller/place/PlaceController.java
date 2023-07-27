package ewm.server.controller.place;

import ewm.server.service.place.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

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
}