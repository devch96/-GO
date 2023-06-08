package com.hansung.capstone.user.email;

import com.hansung.capstone.response.CommonResponse;
import com.hansung.capstone.response.ResponseService;
import com.hansung.capstone.user.AuthService;
import com.hansung.capstone.user.TokenInfo;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailServiceImpl emailService;

    private final AuthService authService;

    private final ResponseService responseService;


    @PostMapping("/send")
    public ResponseEntity<CommonResponse> emailSend(@RequestParam String email) throws Exception {
        String code = emailService.createKey();
        emailService.sendSimpleMessage(email,code);
        return new ResponseEntity<>(this.responseService.getSuccessCommonResponse(), HttpStatus.OK);
    }

    @PostMapping("/confirm")
    public ResponseEntity emailConfirm(@RequestParam String email, @RequestParam String code) throws Exception {
        if(emailService.checkCode(email,code)){
            TokenInfo tokenInfo = this.authService.createToken("103Friends", email, null);
            return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(tokenInfo), HttpStatus.OK);
        } else{
            return new ResponseEntity<>(this.responseService.getFailureCommonResponse(), HttpStatus.UNAUTHORIZED);
        }
    }
}
