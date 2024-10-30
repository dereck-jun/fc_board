package com.fc_board.controller;

import com.fc_board.model.entity.UserEntity;
import com.fc_board.model.post.Post;
import com.fc_board.model.post.PostPatchRequestBody;
import com.fc_board.model.post.PostRequestBody;
import com.fc_board.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<Post>> getPosts(Authentication authentication) {
        log.info("GET /api/v1/posts");
        var posts = postService.getPosts((UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostByPostId(@PathVariable Long postId, Authentication authentication) {
        log.info("GET /api/v1/posts/{}", postId);
        var post = postService.getPostByPostId(postId, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostRequestBody postRequestBody, Authentication authentication) {
        log.info("POST /api/v1/posts");
        var post = postService.createPost(postRequestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody PostPatchRequestBody patchRequestBody, Authentication authentication) {
        log.info("PATCH /api/v1/posts/{}", postId);
        var updatePost = postService.updatePost(postId, patchRequestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(updatePost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, Authentication authentication) {
        log.info("DELETE /api/v1/posts/{}", postId);
        postService.deletePost(postId, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<Post> toggleLike(@PathVariable Long postId, Authentication authentication) {
        Post post = postService.toggleLike(postId, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(post);
    }

}
