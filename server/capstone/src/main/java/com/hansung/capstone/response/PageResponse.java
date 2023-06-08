package com.hansung.capstone.response;

import lombok.Getter;

@Getter
public class PageResponse<T> extends CommonResponse {
    int totalPage;
    T data;

}
