package de.damien.funnyplaces.comments;

import de.damien.funnyplaces.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment addComment(Comment comment, String token) throws AuthenticationException {
        if (!AccountService.authenticateUser(comment.getWriter().getName(), token)) {
            throw new AuthenticationException("Invalid token");
        }
        return commentRepository.save(comment);
    }

    public Comment getComment(Long id, String token) throws NoSuchElementException, AuthenticationException {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isEmpty()) {
            throw new NoSuchElementException();
        }
        Comment comment = commentOptional.get();
        if (AccountService.getAccountByToken(token) == null) {
            throw new AuthenticationException("Invalid token");
        }
        return comment;
    }

    public Long updateComment(Long id, Comment commentNew, String token) throws NoSuchElementException, AuthenticationException {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isEmpty()) throw new NoSuchElementException();
        Comment commentDB = commentOptional.get();
        if (!AccountService.authenticateUser(commentDB.getWriter().getName(), token)) {
            throw new AuthenticationException("Invalid token");
        }
        commentDB.setText(commentNew.getText());
        return commentRepository.save(commentDB).getCommentId();
    }

    public Long deleteComment(Long id, String token) throws NoSuchElementException, AuthenticationException {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isEmpty()) throw new NoSuchElementException();
        Comment commentDB = commentOptional.get();
        if (!AccountService.authenticateUser(commentDB.getWriter().getName(), token)) {
            throw new AuthenticationException("Invalid token");
        }
        commentRepository.deleteById(id);
        return commentDB.getCommentId();
    }
}
