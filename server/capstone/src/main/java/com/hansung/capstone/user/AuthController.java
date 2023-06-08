package com.hansung.capstone.user;

import com.hansung.capstone.response.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final ResponseService responseService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestHeader("Authorization") String accessToken,
                                     @RequestHeader("Refresh-Token") String refreshToken){
        TokenInfo tokenInfo = this.authService.reissue(accessToken, refreshToken);
        if(tokenInfo != null){
            return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(tokenInfo), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(this.responseService.getFailureCommonResponse(), HttpStatus.UNAUTHORIZED);
        }
    }
}
