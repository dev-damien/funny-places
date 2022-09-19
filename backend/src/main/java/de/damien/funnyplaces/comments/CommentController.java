package de.damien.funnyplaces.comments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.AuthenticationException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentRepository) {
        this.commentService = commentRepository;
    }

    @PostMapping(path = "/comments")
    public Long addComment(@RequestBody Comment comment, String token) {
        try {
            return commentService.addComment(comment, token).getCommentId();
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(path = "/comments/{id}")
    public Comment getComment(@PathVariable("id") Long id) {
        try {
            return commentService.getComment(id);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

}
