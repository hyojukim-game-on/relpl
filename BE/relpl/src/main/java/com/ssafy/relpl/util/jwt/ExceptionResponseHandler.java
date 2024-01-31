package com.ssafy.relpl.util.jwt;

import com.ssafy.relpl.util.exception.BaseException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class ExceptionResponseHandler {

    // 아래 내용 모두 추가
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<String> handleSignatureException() {
        log.info("ExceptionResponseHandler : handleSignatureException!!!!!");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 유효하지 않습니다.");
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<String> handleMalformedJwtException() {
        log.info("ExceptionResponseHandler : handleMalformedJwtException!!!!!");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("올바르지 않은 토큰입니다.");
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredJwtException() {
        log.info("ExceptionResponseHandler : handleExpiredJwtException!!!!!");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 만료되었습니다. 다시 로그인해주세요.");
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<String> handleBaseException(BaseException ex) {
        return ResponseEntity.status(HttpStatus.GONE).body(ex.getMessage());
    }
}