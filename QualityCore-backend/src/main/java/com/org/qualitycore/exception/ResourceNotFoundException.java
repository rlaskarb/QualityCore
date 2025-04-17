package com.org.qualitycore.exception;

public class ResourceNotFoundException extends RuntimeException {

    // 기본 생성자
    public ResourceNotFoundException() {
        super("리소스를 찾을 수 없습니다.");
    }

    // 메시지를 전달할 수 있는 생성자
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // 예외를 발생시킬 때 원인(ex. 다른 예외)을 함께 전달할 수 있는 생성자
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
