package com.hansung.capstone.user;

import com.google.maps.model.LatLng;
import com.hansung.capstone.response.CommonResponse;
import com.hansung.capstone.response.ListResponse;
import com.hansung.capstone.response.ResponseService;
import com.hansung.capstone.response.SingleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final ResponseService responseService;

    private final AuthService authService;

    private final JwtTokenProvider jwtTokenProvider;

//    private final UserDetailServiceImpl userDetailService;

    @PostMapping("/signup")
    private ResponseEntity<SingleResponse> SignUp(@RequestBody @Valid UserDTO.SignUpRequestDTO req, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(this.responseService.getFailureSingleResponse(bindingResult.getAllErrors()), HttpStatus.BAD_REQUEST);
        }
        SingleResponse<UserDTO.SignUpResponseDTO> res = this.responseService.getSuccessSingleResponse(this.userService.SignUp(req));
        return new ResponseEntity<>(res,HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    private ResponseEntity<CommonResponse> SignIn(@RequestBody UserDTO.SignInRequestDTO req) {
        try {
            UserDTO.SignInResponseDTO res = authService.login(req);
            return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(res), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(this.responseService.getFailureCommonResponse(), HttpStatus.UNAUTHORIZED);
        }

    }

    @GetMapping("/email/duplicate-check")
    public ResponseEntity<SingleResponse> EmailDuplicateCheck(@RequestParam String email) {
        Boolean isCheck = userService.EmailDupCheck(email);
        if (isCheck) {
            UserDTO.DuplicateEmailCheckResponseDTO res = UserDTO.DuplicateEmailCheckResponseDTO.builder()
                    .email(email).build();
            return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(res), HttpStatus.OK);
        } else{
            return new ResponseEntity<>(this.responseService.getFailureSingleResponse(null), HttpStatus.OK);
        }


    }

    @GetMapping("/nickname/duplicate-check")
    public ResponseEntity<SingleResponse<UserDTO.DuplicateNicknameCheckResponseDTO>> NicknameDuplicateCheck(@RequestParam String nickname){
        Boolean isCheck = this.userService.NicknameDupCheck(nickname);
        if (isCheck){
            UserDTO.DuplicateNicknameCheckResponseDTO res = UserDTO.DuplicateNicknameCheckResponseDTO.builder()
                    .nickname(nickname).build();
            return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(res), HttpStatus.OK);
        } else
            return new ResponseEntity<>(this.responseService.getFailureSingleResponse(null), HttpStatus.OK);
    }

    @GetMapping("/findID")
    public ResponseEntity<ListResponse<String>> findID(@RequestParam String username, @RequestParam String birthday){
        List<String> email = this.userService.findEmail(username, birthday);
        return new ResponseEntity<>(this.responseService.getListResponse(email), HttpStatus.OK);
    }

    @PutMapping("/modifyPW")
    public ResponseEntity<SingleResponse<String>> modifyPW(@RequestBody UserDTO.ModifyPWRequestDTO req){
        this.userService.modifyPassword(req);
        return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(null),HttpStatus.OK);
    }

    @PutMapping("/modifyNick")
    public ResponseEntity<SingleResponse<String>> modifyNick(@RequestBody UserDTO.ModifyNickRequestDTO req){
        this.userService.modifyNickname(req);
        return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(req.getNickname()), HttpStatus.OK);
    }

    @GetMapping("/test")
    public String test(@RequestHeader(value = "Authorization") String token) {
        return this.jwtTokenProvider.getClaims(this.authService.resolveToken(token)).get("email").toString();

    }

    @PutMapping("/set-profile-image")
    public ResponseEntity<SingleResponse<UserDTO.ProfileImageResponseDTO>> setProfileImage(
            @RequestPart(value = "requestDTO") UserDTO.ProfileImageRequestDTO req,
            @RequestPart(value = "imageList", required = false) MultipartFile imageList) throws Exception {
        System.out.println("hi");
        return new ResponseEntity<>(this.responseService.getSuccessSingleResponse(this.userService.setProfileImage(req, imageList)), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(@RequestHeader("Authorization") String accessToken){
        try{
            this.authService.logout(accessToken);
            return new ResponseEntity<>(this.responseService.getSuccessCommonResponse(), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(this.responseService.getFailureCommonResponse(), HttpStatus.UNAUTHORIZED);
        }
    }
}