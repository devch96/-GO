package com.hansung.capstone.user.email;

import com.hansung.capstone.RedisService;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{


    private final JavaMailSender javaMailSender;

    private final RedisService redisService;


    private MimeMessage createMessage(String to, String code) throws Exception{
        System.out.println("보내는 대상 : " + to);
        System.out.println("인증 번호 : "+code);
        System.out.println(System.currentTimeMillis());
        MimeMessage message = javaMailSender.createMimeMessage();
        LocalDateTime expiredTime = LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusMinutes(5);
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.MEDIUM);

        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("자전GO 비밀번호 인증 이메일");

        String msgg="";
        msgg+= "<div style='margin:20px;'>";
        msgg+= "<h1> 안녕하세요 자전GO 입니다. </h1>";
        msgg+= "<br>";
        msgg+= "<p>아래 코드를 복사해 입력해주세요<p>";
        msgg+= "<br>";
        msgg+= "<p> 유효기간 : " + dtf.format(expiredTime) +"<p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>이메일 인증 코드입니다.</h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "CODE : <strong>";
        msgg+= code+"</strong><div><br/> ";
        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("ib103Friends@gmail.com","103Friends"));//보내는 사람

        return message;
    }

    public String createKey() {
        StringBuilder key = new StringBuilder();
        Random rnd = new Random(System.currentTimeMillis());

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3);

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    break;
            }
        }
        return key.toString();
    }

    @Override
    public String sendSimpleMessage(String to, String code) throws Exception {
        MimeMessage message = createMessage(to,code);
        this.redisService.setValuesWithTimeout("Email-Confirm:" + to, code, 300000); // 5분
        try{
            javaMailSender.send(message);
        }catch (MailException e){
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
        return code;
    }

    @Override
    public Boolean checkCode(String email, String code) throws Exception {
        if (this.redisService.getValues("Email-Confirm:" + email).equals(code)){
            this.redisService.deleteValues("Email-Confirm:" + email);
            return true;
        } else{
            return false;
        }
    }
}
