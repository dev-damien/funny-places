package de.damien.funnyplaces.comments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment addComment(Comment comment, String token) throws AuthenticationException {
        //TODO uncomment later
//        String userByToken = AccountService.getAccountByToken(token);
//        if (!AccountService.authenticateUser(comment.getWriter().getName(), token)) {
//            //token authentication failed
//            throw new AuthenticationException("");
//        }
        return commentRepository.save(comment);
    }

    public Comment getComment(Long id) throws NoSuchElementException {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isEmpty()) {
            throw new NoSuchElementException();
        }
        return commentOptional.get();
    }

    public Set<Comment> getAllCommentsByPlaceId(Long placeId) {
        return commentRepository.findByPlaceId(placeId);
    }
}
