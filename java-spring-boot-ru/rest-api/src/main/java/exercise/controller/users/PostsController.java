package exercise.controller.users;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

import exercise.model.Post;
import exercise.Data;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

// BEGIN
@RestController("/api")
public class PostsController {

    private List<Post> posts = Data.getPosts();

    @GetMapping("/users/{id}/posts")
    public Optional<Post> show(@PathVariable String id) {

        var post = posts.stream()
                .filter(p -> p.getUserId().equals(id))
                .findAll();
        return post;
    }

    @PostMapping("/users/{id}/posts")
    public ResponseEntity<Post> create(@RequestBody Post post) {
        posts.add(post);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(post.getUserId())
                .toUri();

        return ResponseEntity.created(location).body(post);
    }
}
// END
