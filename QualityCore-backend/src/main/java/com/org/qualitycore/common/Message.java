package com.org.qualitycore.common;

import lombok.*;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Message {

    private int code; // 상태코드
    private String message; // 메시지
    private Map<String, Object> result; // 상태값



}
