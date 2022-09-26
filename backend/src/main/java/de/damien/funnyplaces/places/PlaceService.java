package de.damien.funnyplaces.places;

import de.damien.funnyplaces.accounts.AccountService;
import de.damien.funnyplaces.comments.Comment;
import de.damien.funnyplaces.comments.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
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

    public Place addPlace(Place place, String token) throws AuthenticationException {
        if (!AccountService.authenticateUser(place.getCreator().getName(), token)) {
            throw new AuthenticationException("Invalid token");
        }
        return placeRepository.save(place);
    }


    public Place getPlace(Long id, String token) throws NoSuchElementException, AuthenticationException {
        Optional<Place> placeOptional = placeRepository.findById(id);
        if (placeOptional.isEmpty()) {
            throw new NoSuchElementException();
        }
        Place place = placeOptional.get();
        if (AccountService.getAccountByToken(token) == null) {
            throw new AuthenticationException("Invalid token");
        }
        return place;
    }

    public List<Comment> getAllComments(Long id, String token) throws AuthenticationException {
        Optional<Place> placeOptional = placeRepository.findById(id);
        if (placeOptional.isEmpty()) throw new NoSuchElementException();
        Place place = placeOptional.get();
        if (AccountService.getAccountByToken(token) == null) {
            throw new AuthenticationException("Invalid token");
        }
        return place.getComments();
    }

    public Place updatePlace(Long id, Place placeNew, String token) throws NoSuchElementException, AuthenticationException {
        Optional<Place> place = placeRepository.findById(id);
        if (place.isEmpty()) throw new NoSuchElementException();
        Place placeDB = place.get();
        if (!AccountService.authenticateUser(placeDB.getCreator().getName(), token)) {
            throw new AuthenticationException("Invalid token");
        }
        if (placeNew.getTitle() != null && !placeNew.getTitle().isBlank())
            placeDB.setTitle(placeNew.getTitle());
        if (placeNew.getDescription() != null && !placeNew.getDescription().isBlank())
            placeDB.setDescription(placeNew.getDescription());
        placeRepository.save(placeDB);
        return place.get();
    }

    @Transactional
    public Place deletePlace(Long id, String token) throws NoSuchElementException, AuthenticationException {
        Optional<Place> placeOptional = placeRepository.findById(id);
        if (placeOptional.isEmpty()) throw new NoSuchElementException();
        Place place = placeOptional.get();
        if (!AccountService.authenticateUser(place.getCreator().getName(), token)) {
            throw new AuthenticationException("Invalid token");
        }
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

    @Transactional
    public List<Place> getAllPlaces(String token) throws AuthenticationException {
        if (AccountService.getAccountByToken(token) == null) {
            throw new AuthenticationException("Invalid token");
        }
        return placeRepository.findAll();
    }
}
