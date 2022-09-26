package de.damien.funnyplaces.places;

import de.damien.funnyplaces.comments.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.AuthenticationException;
import java.util.List;
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
    public Place addPlace(@RequestBody Place place, @RequestHeader("token") String token) {
        try {
            return placeService.addPlace(place, token);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(path = "/{id}")
    public Place getPlace(@PathVariable("id") Long id, @RequestHeader("token") String token) {
        try {
            Place place = placeService.getPlace(id, token);
            return place;
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping
    public List<Place> getAllPlaces(@RequestHeader("token") String token) {
        try {
            return placeService.getAllPlaces(token);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PatchMapping(path = "/{id}")
    public Place updatePlace(@PathVariable("id") Long id, @RequestBody Place place, @RequestHeader("token") String token) {
        try {
            return placeService.updatePlace(id, place, token);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping(path = "/{id}")
    public Place deletePlace(@PathVariable("id") Long id, @RequestHeader("token") String token) {
        try {
            return placeService.deletePlace(id, token);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(path = "/{placeId}/comments")
    public List<Comment> getAllComments(@PathVariable("placeId") Long placeId, @RequestHeader("token") String token) {
        try {
            return placeService.getAllComments(placeId, token);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

}
