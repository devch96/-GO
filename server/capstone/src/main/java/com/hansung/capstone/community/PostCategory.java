package com.hansung.capstone.community;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostCategory {
    FREE("FREE","자유 게시판"),
    COURSE("COURSE","코스 추천 게시판");

    private final String key;
    private final String title;
}
