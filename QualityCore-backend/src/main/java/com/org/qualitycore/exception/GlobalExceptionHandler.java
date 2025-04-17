package com.org.qualitycore.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

//    // 404 오류
//    @ExceptionHandler(ResourceNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND) // 404 상태 코드
//    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
//        headers.set("Charset", "UTF-8");
//
//        return new ResponseEntity<>("요청한 리소스를 찾을 수 없습니다.", headers, HttpStatus.NOT_FOUND);
//    }
//
//    // 500 오류
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500 상태 코드
//    public ResponseEntity<String> handleInternalServerError(Exception ex) {
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
//        headers.set("Charset", "UTF-8");
//
//        // 예외가 발생한 경우 500 내부 서버 오류를 처리
//        return new ResponseEntity<>("서버 내부에서 오류가 발생했습니다.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
