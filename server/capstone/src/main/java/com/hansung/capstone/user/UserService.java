package com.hansung.capstone.user;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO.SignUpResponseDTO SignUp(UserDTO.SignUpRequestDTO req);

//    TokenInfo SignIn(UserDTO.SignInRequestDTO req);

    List<String> findEmail(String username, String birthday);

    Boolean EmailDupCheck(String email);

    Boolean NicknameDupCheck(String nickname);

    Optional<User> modifyPassword(UserDTO.ModifyPWRequestDTO req);

    Optional<User> modifyNickname(UserDTO.ModifyNickRequestDTO req);

    UserDTO.ProfileImageResponseDTO setProfileImage(UserDTO.ProfileImageRequestDTO req, MultipartFile image) throws Exception;

}

