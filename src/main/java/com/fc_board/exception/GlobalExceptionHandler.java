package com.fc_board.exception;

import com.fc_board.model.error.ClientErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientErrorException.class)
    public ResponseEntity<ClientErrorResponse> handleClientException(ClientErrorException e) {
        return new ResponseEntity<>(
                new ClientErrorResponse(e.getStatus(), e.getMessage()), e.getStatus()
        );
    }
}
