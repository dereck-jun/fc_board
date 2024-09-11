package com.fc_board.service;

import com.fc_board.model.Post;
import com.fc_board.model.PostPatchRequestBody;
import com.fc_board.model.PostRequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    public static final List<Post> posts = new ArrayList<>();
    static {
        posts.add(new Post(1L, "Post 1", ZonedDateTime.now()));
        posts.add(new Post(2L, "Post 2", ZonedDateTime.now()));
        posts.add(new Post(3L, "Post 3", ZonedDateTime.now()));
        posts.add(new Post(4L, "Post 4", ZonedDateTime.now()));
    }

    public List<Post> getPosts() {
        return posts;
    }

    public Optional<Post> getPostByPostId(Long postId) {
        return posts.stream().filter(post -> postId.equals(post.getPostId())).findFirst();
    }

    public Post createPost(PostRequestBody postRequestBody) {
        var newPostId = posts.stream().mapToLong(Post::getPostId).max().orElse(0L) + 1;

        var newPost = new Post(newPostId, postRequestBody.body(), ZonedDateTime.now());
        posts.add(newPost);

        return newPost;
    }

    public Post updatePost(Long postId, PostPatchRequestBody patchRequestBody) {
        Optional<Post> optionalPost = posts.stream().filter(post -> postId.equals(post.getPostId())).findFirst();

        if (optionalPost.isPresent()) {
            Post updatePost = optionalPost.get();
            updatePost.setBody(patchRequestBody.body());
            return updatePost;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
    }

    public void deletePost(Long postId) {
        Optional<Post> optionalPost = posts.stream().filter(post -> postId.equals(post.getPostId())).findFirst();

        if (optionalPost.isPresent()) {
            posts.remove(optionalPost.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
    }
}
