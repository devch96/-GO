package com.hansung.capstone.user.email;

public interface EmailService {
    String sendSimpleMessage(String to, String code) throws Exception;

    Boolean checkCode(String email, String code) throws Exception;
}
