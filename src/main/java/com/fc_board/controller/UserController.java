package com.fc_board.controller;

import com.fc_board.model.user.User;
import com.fc_board.model.user.UserSignUpRequestBody;
import com.fc_board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> signUp(@RequestBody UserSignUpRequestBody request) {
        var user = userService.signUp(
                request.username(),
                request.password()
        );
        return ResponseEntity.ok(user);
    }

}
