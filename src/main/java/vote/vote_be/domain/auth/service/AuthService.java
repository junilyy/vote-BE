package vote.vote_be.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vote.vote_be.domain.auth.dto.request.LoginRequest;
import vote.vote_be.domain.auth.dto.request.SignupRequest;
import vote.vote_be.domain.auth.dto.response.DuplicateCheckResponse;
import vote.vote_be.domain.auth.dto.response.LoginResponse;
import vote.vote_be.domain.auth.dto.response.SignupResponse;
import vote.vote_be.domain.auth.dto.response.TokenValidationResponse;
import vote.vote_be.domain.user.entity.*;
import vote.vote_be.domain.user.repository.UserRepository;
import vote.vote_be.global.apiPayload.code.SimpleMessageDTO;
import vote.vote_be.global.apiPayload.code.status.ErrorStatus;
import vote.vote_be.global.apiPayload.exception.GeneralException;
import vote.vote_be.global.redis.RedisService;
import vote.vote_be.global.security.dto.TokenResponse;
import vote.vote_be.global.security.entity.TokenStatus;
import vote.vote_be.global.security.jwt.TokenProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String RT_KEY_PREFIX = "refresh-token:";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    /* 회원가입 */
    @Transactional
    public SignupResponse signup(SignupRequest req) {

        User user = User.builder()
                .loginId(req.getLoginId())
                .password(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .part(req.getPart())
                .name(req.getName())
                .team(req.getTeam())
                .userRole(UserRole.USER)
                .build();

        userRepository.save(user);

        return SignupResponse.of("회원가입이 완료되었습니다.", user.getLoginId(), user.getName());
    }

    /* 로그인 */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        // LoginId 존재 여부 확인
        User user = userRepository.findByLoginId(req.getLoginId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_LOGIN_ID));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new GeneralException(ErrorStatus.LOGIN_FAIL);

        TokenResponse tokens = tokenProvider.createToken(user);

        // userId 기준으로 레디스에 refresh token 저장(TTL=refresh 남은 시간)
        long ttlSec = tokenProvider.getRemainingSeconds(tokens.getRefreshToken());
        redisService.setRefreshToken(RT_KEY_PREFIX + user.getId(), tokens.getRefreshToken(), ttlSec);

        return LoginResponse.of(user.getId(), user.getName(), user.getPart(), user.getTeam(), tokens.getAccessToken(), tokens.getRefreshToken());
    }

    @Transactional
    public SimpleMessageDTO logout(HttpServletRequest request) {
        String accessToken = tokenProvider.resolveToken(request);

        if (accessToken == null || accessToken.isBlank())
            throw new GeneralException(ErrorStatus.TOKEN_INVALID);

        String userId = tokenProvider.getUserIdFromToken(accessToken);
        redisService.deleteValue(RT_KEY_PREFIX + userId);

        return new SimpleMessageDTO("로그아웃이 완료되었습니다.");
    }


    @Transactional(readOnly = true)
    public TokenResponse newAccessToken(String refreshToken) { // 프론트에서 Authorization 헤더를 빼고 전송해줘야함.

        // refresh 토큰 검증
        TokenStatus tokenStatus = tokenProvider.validateToken(refreshToken);
        if(tokenStatus == TokenStatus.EXPIRED){
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_EXPIRED);
        }
        if(tokenStatus == TokenStatus.INVALID || tokenStatus == TokenStatus.MISSING){
            throw new GeneralException(ErrorStatus.TOKEN_INVALID);
        }

        // user 확인
        String userId = tokenProvider.getUserIdFromToken(refreshToken);

        // Redis의 refresh 토큰과 비교
        String key = RT_KEY_PREFIX + userId;
        String stored = redisService.getValue(key);
        if (stored.isEmpty()) {
            // 만료/로그아웃 등으로 없는 상태
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_EXPIRED);
        }
        if (!stored.equals(refreshToken)) {
            // 탈취 등으로 일치하지 않는 상태
            throw new GeneralException(ErrorStatus.TOKEN_INVALID);
        }

        // user 조회
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_USER));

        // AccessToken만 새로 발급
        String newAccess = tokenProvider.createAccessToken(user);

        return TokenResponse.of(newAccess, refreshToken);
    }

    /* 로그인 아이디 중복 체크 */
    @Transactional(readOnly = true)
    public DuplicateCheckResponse checkLoginId(String value) {
        boolean available = !userRepository.existsByLoginId(value);
        return DuplicateCheckResponse.of("loginId", value, available);
    }

    /* 이메일 중복 체크 */
    @Transactional(readOnly = true)
    public DuplicateCheckResponse checkEmail(String value) {
        boolean available = !userRepository.existsByEmail(value);
        return DuplicateCheckResponse.of("email", value, available);
    }

    // Access Token의 유효성 확인
    @Transactional(readOnly = true)
    public TokenValidationResponse isValidAccess(HttpServletRequest request) {
        // 유효성 검사는 JwtAuthenticationFilter에서 처리
        String accessToken = tokenProvider.resolveToken(request);
        TokenStatus status = tokenProvider.validateToken(accessToken);
        return TokenValidationResponse.of("Access Token이 유효합니다.", status);
    }
}
