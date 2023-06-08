package com.hansung.capstone.user;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class test {
    public static void main(String[] args) {
        LocalDateTime dateTime = LocalDateTime.now(); // 현재 날짜와 시간

        // 시간을 뺄 LocalDateTime 객체
        LocalDateTime result = dateTime.minusHours(2).minusMinutes(30);

        // 결과로 얻은 LocalDateTime 객체의 시간을 00:00:00.000000으로 설정
        LocalTime midnight = LocalTime.of(0, 0, 0, 0);
        LocalDateTime finalDateTime = result.with(midnight);

        System.out.println(finalDateTime);
    }
}
