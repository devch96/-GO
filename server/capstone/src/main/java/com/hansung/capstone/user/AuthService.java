package com.hansung.capstone.user;

import com.hansung.capstone.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisService redisService;
    private final UserRepository userRepository;


    // 로그인: 인증 정보 저장 및 비어 토큰 발급
    @Transactional
    public UserDTO.SignInResponseDTO login(UserDTO.SignInRequestDTO req) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        TokenInfo tokenInfo = createToken("103Friends", req.getEmail(), getAuthorities(authentication));
        Optional<User> user = this.userRepository.findByEmail(req.getEmail());
        if(user.isPresent())
        {
            Long profileImageId;
            if(user.get().getProfileImage() != null){
                profileImageId = user.get().getProfileImage().getId();
            }else{
                profileImageId = -1L;
            }
            UserDTO.SignInResponseDTO res = UserDTO.SignInResponseDTO.builder()
                    .nickname(user.get().getNickname())
                    .birthday(user.get().getBirthday())
                    .check(true)
                    .userId(user.get().getId())
                    .email(user.get().getEmail())
                    .profileImageId(profileImageId)
                    .tokenInfo(tokenInfo)
                    .username(user.get().getUsername()).build();
            return res;
        }else{
            return null;
        }
    }

    // AT가 만료일자만 초과한 유효한 토큰인지 검사
    public boolean validate(String requestAccessTokenInHeader) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);
        return jwtTokenProvider.validateAccessTokenOnlyExpired(requestAccessToken); // true = 재발급
    }

//     토큰 재발급: validate 메서드가 true 반환할 때만 사용 -> AT, RT 재발급
    @Transactional
    public TokenInfo reissue(String requestAccessTokenInHeader, String requestRefreshToken) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);

        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);
        String email = this.jwtTokenProvider.getClaims(requestAccessToken).get("email").toString();

        String refreshTokenInRedis = redisService.getValues("RT:" + email);
        if (refreshTokenInRedis == null) { // Redis에 저장되어 있는 RT가 없을 경우
            return null; // -> 재로그인 요청
        }

        // 요청된 RT의 유효성 검사 & Redis에 저장되어 있는 RT와 같은지 비교
        if(!jwtTokenProvider.validateRefreshToken(requestRefreshToken)) {
            redisService.deleteValues("RT:" + email); // 탈취 가능성 -> 삭제
            return null; // -> 재로그인 요청
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String authorities = getAuthorities(authentication);

        // 토큰 재발급 및 Redis 업데이트
        redisService.deleteValues("RT:" + email); // 기존 RT 삭제
        TokenInfo tokenInfo = jwtTokenProvider.createToken(email, authorities);
        saveRefreshToken(email, tokenInfo.getRefreshToken());
        return tokenInfo;
    }

    // 토큰 발급
    @Transactional
    public TokenInfo createToken(String provider, String email, String authorities) {
        // RT가 이미 있을 경우
        if(redisService.getValues("RT:" + email) != null) {
            redisService.deleteValues("RT:" + email); // 삭제
        }

        // AT, RT 생성 및 Redis에 RT 저장
        TokenInfo tokenInfo = jwtTokenProvider.createToken(email, authorities);
        saveRefreshToken(email, tokenInfo.getRefreshToken());
        return tokenInfo;
    }

//     RT를 Redis에 저장
    @Transactional
    public void saveRefreshToken(String email, String refreshToken) {
        redisService.setValuesWithTimeout("RT:" + email, // key
                refreshToken, // value
                jwtTokenProvider.getTokenExpirationTime(refreshToken)); // timeout(milliseconds)
    }

    // 권한 이름 가져오기
    public String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    // "Bearer {AT}"에서 {AT} 추출
    public String resolveToken(String requestAccessTokenInHeader) {
        if (requestAccessTokenInHeader != null && requestAccessTokenInHeader.startsWith("Bearer ")) {
            return requestAccessTokenInHeader.substring(7);
        }
        return null;
    }

    // 로그아웃
    @Transactional
    public void logout(String requestAccessTokenInHeader) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);
        String email = this.jwtTokenProvider.getClaims(requestAccessToken).get("email").toString();

        // Redis에 저장되어 있는 RT 삭제
        String refreshTokenInRedis = redisService.getValues("RT:" + email);
        if (refreshTokenInRedis != null) {
            redisService.deleteValues("RT:" + email);
        }

        // Redis에 로그아웃 처리한 AT 저장
        long expiration = jwtTokenProvider.getTokenExpirationTime(requestAccessToken) - new Date().getTime();
        redisService.setValuesWithTimeout(requestAccessToken,
                "logout",
                expiration);
    }

    public boolean checkIdAndToken(Long userId){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userId.equals(userDetails.getUserId())){
            return true;
        } else {
            return false;
        }
    }
}