package de.damien.funnyplaces.places;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/places")
public class PlaceController {

    private final PlaceService placeService;

    @Autowired
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @PostMapping
    public Place addPlace(@RequestBody Place place) {
        return placeService.addPlace(place);
    }

    @GetMapping(path = "/{id}")
    public Place getPlace(@PathVariable("id") Long id) {
        try {
            Place place = placeService.getPlace(id);
            return place;
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping(path = "/{id}")
    public Place updatePlace(@PathVariable("id") Long id) {
        //TODO
        return null;
    }

    @DeleteMapping(path = "/{id}")
    public Place deletePlace(@PathVariable("id") Long id) {
        //TODO
        return null;
    }

}
