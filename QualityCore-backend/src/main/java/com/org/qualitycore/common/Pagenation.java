package com.org.qualitycore.common;

import org.springframework.data.domain.Page;

public class Pagenation {

    public static PagingButton getPagingInfo(Page page) {

        int currentPage = page.getNumber() + 1; // 시작페이지 0부터 시작하면 안되니 + 1

        // 페이지 버튼의 기본 갯수
        int defaultButtonCount = 1;

        // 시작페이지 계산
        int startPage = (int)(Math.ceil((double) currentPage / defaultButtonCount) - 1) * defaultButtonCount + 1;

        // 마지막페이지
        int endPage = startPage + defaultButtonCount - 1;

        if(page.getTotalPages() < endPage) {
            endPage = page.getTotalPages();
        } if(page.getTotalPages() == 0 && endPage == 0) {
            endPage = startPage;
        }

        return new PagingButton(currentPage, startPage, endPage);

    }
}
