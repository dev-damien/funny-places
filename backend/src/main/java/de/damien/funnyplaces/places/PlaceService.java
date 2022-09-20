package de.damien.funnyplaces.places;

import de.damien.funnyplaces.comments.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
        Optional<Place> placeOptional = placeRepository.findById(id);
        if (placeOptional.isEmpty()) {
            throw new NoSuchElementException();
        }
        return placeOptional.get();
    }

    public List<Comment> getAllComments(Long id) {
        Optional<Place> placeOptional = placeRepository.findById(id);
        if (placeOptional.isEmpty()) throw new NoSuchElementException();
        Place place = placeOptional.get();
        return place.getComments();
    }

    public Place updatePlace(Long id, Place placeNew) throws NoSuchElementException {
        Optional<Place> place = placeRepository.findById(id);
        if (place.isEmpty()) throw new NoSuchElementException();
        Place placeDB = place.get();
        placeDB.setTitle(placeNew.getTitle());
        placeDB.setDescription(placeNew.getDescription());
        placeRepository.save(placeDB);
        return place.get();
    }

    public Place deletePlace(Long id) throws NoSuchElementException {
        Optional<Place> placeOptional = placeRepository.findById(id);
        if (placeOptional.isEmpty()) throw new NoSuchElementException();
        Place place = placeOptional.get();
        placeRepository.deleteById(id);
        return place;
    }
}
