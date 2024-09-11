package com.fc_board.controller;

import com.fc_board.model.Post;
import com.fc_board.model.PostPatchRequestBody;
import com.fc_board.model.PostRequestBody;
import com.fc_board.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<Post>> getPosts() {
        log.info("GET /api/v1/posts");
        var posts = postService.getPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostByPostId(@PathVariable Long postId) {
        log.info("GET /api/v1/posts/{}", postId);
        var post = postService.getPostByPostId(postId);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostRequestBody postRequestBody) {
        log.info("POST /api/v1/posts");
        var post = postService.createPost(postRequestBody);
        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody PostPatchRequestBody patchRequestBody) {
        log.info("PATCH /api/v1/posts/{}", postId);
        var updatePost = postService.updatePost(postId, patchRequestBody);
        return ResponseEntity.ok(updatePost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        log.info("DELETE /api/v1/posts/{}", postId);
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

}
