package com.ac.su;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
    // @PreAuthorize("hasRole('CLUB_PRESIDENT')") 조건에 위배할시
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseMessage> handleAccessDeniedException(AccessDeniedException ex) {
        ResponseMessage responseMessage = new ResponseMessage("동아리 회장만 접근 가능");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
    }
}

