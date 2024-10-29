package com.fc_board.controller;

import com.fc_board.model.entity.UserEntity;
import com.fc_board.model.post.Post;
import com.fc_board.model.post.PostPatchRequestBody;
import com.fc_board.model.post.PostRequestBody;
import com.fc_board.model.reply.Reply;
import com.fc_board.model.reply.ReplyPatchRequestBody;
import com.fc_board.model.reply.ReplyRequestBody;
import com.fc_board.service.PostService;
import com.fc_board.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/posts/{postId}/replies")
@RequiredArgsConstructor
public class ReplyController {

    private final PostService postService;
    private final ReplyService replyService;

    @GetMapping
    public ResponseEntity<List<Reply>> getRepliesByPostId(@PathVariable Long postId) {
        var replies = replyService.getRepliesByPostId(postId);
        return ResponseEntity.ok(replies);
    }

    @PostMapping
    public ResponseEntity<Reply> createReply(@PathVariable Long postId, @RequestBody ReplyRequestBody replyRequestBody, Authentication authentication) {
        var reply = replyService.createReply(postId, replyRequestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(reply);
    }

    @PatchMapping("/{replyId}")
    public ResponseEntity<Reply> updatePost(@PathVariable Long postId, @PathVariable Long replyId, @RequestBody ReplyPatchRequestBody replyPatchRequestBody, Authentication authentication) {
        var updateReply = replyService.updateReply(postId, replyId, replyPatchRequestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(updateReply);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<Reply> deletePost(@PathVariable Long postId, @PathVariable Long replyId, Authentication authentication) {
        replyService.deleteReply(postId, replyId, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.noContent().build();
    }

}
