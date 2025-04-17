package com.org.qualitycore.common;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PagingButton {

    private int currentPage; // 현재페이지
    private int startPage; // 시작 페이지
    private int endPage; // 마지막 페이지
}
