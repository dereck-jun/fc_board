package com.fc_board.model.user;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestBody(
        @NotBlank String username,
        @NotBlank String password
) { }
