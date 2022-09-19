package de.damien.funnyplaces.places;

import de.damien.funnyplaces.comments.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    @Autowired
    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public Place addPlace(Place place) {
        return placeRepository.save(place);
    }


    public Place getPlace(Long id) throws NoSuchElementException {
        Optional<Place> placeOptional = placeRepository.findPlaceById(id);
        if (placeOptional.isEmpty()) {
            throw new NoSuchElementException();
        }
        return placeOptional.get();
    }

    public Set<Comment> getAllComments(Long id) {
        return placeRepository.findPlaceById(id).get().getComments();
    }
}
