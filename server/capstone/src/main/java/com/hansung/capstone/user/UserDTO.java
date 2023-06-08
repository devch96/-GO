package com.hansung.capstone.user;

import com.google.maps.model.LatLng;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

public class UserDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignUpRequestDTO {
        @Email
        @NotBlank
        private String email;
        @NotBlank
        @Size(min = 6, max = 14)
        private String password;
        @NotBlank
        private String nickname;
        @NotBlank
        private String username;
        @Size(min = 8, max = 8)
        private String birthday;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SignUpResponseDTO {
        private Long id;
        private String nickname;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignInRequestDTO {

        private String email;

        private String password;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SignInResponseDTO {
        private boolean check;

        private String email;

        private Long userId;

        private String username;

        private String birthday;

        private String nickname;

        private Long profileImageId;

        private TokenInfo tokenInfo;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DuplicateEmailCheckResponseDTO{

        private String email;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DuplicateNicknameCheckResponseDTO{
        private String nickname;
    }
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FindIdRequestDTO {
        private String username;
        private String birthday;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ModifyPWRequestDTO {
        private String email;
        private String password;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ModifyNickRequestDTO {
        private String email;
        private String nickname;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PostUserResponseDTO{
        private Long id;
        private String nickname;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CommentUserResponseDTO{
        private Long id;
        private String nickname;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfileImageRequestDTO{
        private Long userId;

        private Long profileImageId;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ProfileImageResponseDTO{
        private Long id;

        private String email;

        private String username;

        private String birthday;

        private String nickname;

        private Long profileImageId;
    }

}
