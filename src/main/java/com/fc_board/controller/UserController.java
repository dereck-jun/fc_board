package com.fc_board.controller;

import com.fc_board.model.entity.UserEntity;
import com.fc_board.model.post.Post;
import com.fc_board.model.user.*;
import com.fc_board.service.PostService;
import com.fc_board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;

    @PostMapping
    public ResponseEntity<User> signUp(@Valid @RequestBody UserSignUpRequestBody request) {
        var user = userService.signUp(
                request.username(),
                request.password()
        );
        return ResponseEntity.ok(user);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<UserAuthenticationResponse> authenticate(@Valid @RequestBody UserLoginRequestBody request) {
        var response = userService.authenticate(
                request.username(),
                request.password()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers(@RequestParam(required = false) String query) {
        var users = userService.getUsers(query);
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username, @RequestBody UserPatchRequestBody requestBody, Authentication authentication) {
        var user = userService.updateUser(username, requestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> updateUser(@PathVariable String username) {
        var user = userService.getUser(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{username}/posts")
    public ResponseEntity<List<Post>> getPostsByUsername(@PathVariable String username) {
        var posts = postService.getPostByUsername(username);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{username}/follows")
    public ResponseEntity<User> follow(@PathVariable String username, Authentication authentication) {
        User user = userService.follow(username, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{username}/follows")
    public ResponseEntity<User> unFollow(@PathVariable String username, Authentication authentication) {
        User user = userService.unFollow(username, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(user);
    }

}
