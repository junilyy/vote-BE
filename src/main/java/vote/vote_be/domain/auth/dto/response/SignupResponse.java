package vote.vote_be.domain.auth.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupResponse {
    private String message;
    private String loginId;
    private String name;

    public static SignupResponse of(String message, String loginId, String name) {
        SignupResponse signupResponse = new SignupResponse();
        signupResponse.message = message;
        signupResponse.loginId = loginId;
        signupResponse.name = name;
        return signupResponse;
    }
}
