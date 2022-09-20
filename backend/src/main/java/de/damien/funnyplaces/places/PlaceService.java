package de.damien.funnyplaces.places;

import de.damien.funnyplaces.comments.Comment;
import de.damien.funnyplaces.comments.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public PlaceService(PlaceRepository placeRepository, CommentRepository commentRepository) {
        this.placeRepository = placeRepository;
        this.commentRepository = commentRepository;
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

    @Transactional
    public Place deletePlace(Long id) throws NoSuchElementException {
        Optional<Place> placeOptional = placeRepository.findById(id);
        if (placeOptional.isEmpty()) throw new NoSuchElementException();
        Place place = placeOptional.get();
        deleteAllComments(place.getComments());
        placeRepository.deleteById(id);
        return place;
    }

    @Transactional
    private void deleteAllComments(List<Comment> comments) {
        for (Comment comment : comments) {
            commentRepository.deleteById(comment.getCommentId());
        }
    }
}
