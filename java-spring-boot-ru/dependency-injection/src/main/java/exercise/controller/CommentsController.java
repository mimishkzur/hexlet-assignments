package exercise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.HttpStatus;

import java.util.List;

import exercise.model.Comment;
import exercise.repository.CommentRepository;
import exercise.exception.ResourceNotFoundException;

// BEGIN
@RestController
@RequestMapping("/comments")
public class CommentsController {

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping(path = "")
    private List<Comment> index() {
        return commentRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    private Comment show(@PathVariable Long id) {
        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with id " + id + " not found"));
        return comment;
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    private Comment create(@RequestBody Comment comment) {
        return commentRepository.save(comment);
    }

    @PutMapping(path = "/{id}")
    private Comment update(@PathVariable Long id, @RequestBody Comment commenti) {
        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with id " + id + " not found"));

        comment.setPostId(commenti.getPostId());
        comment.setBody(commenti.getBody());

        return commentRepository.save(comment);
    }

    @DeleteMapping(path = "/{id}")
    private void delete(@PathVariable Long id) {
        commentRepository.deleteById(id);
    }
}
// END
