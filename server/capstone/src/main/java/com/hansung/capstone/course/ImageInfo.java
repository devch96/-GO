package com.hansung.capstone.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageInfo{
    private String coordinate;
    private String placeName;
    private String placeLink;
}

