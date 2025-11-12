package vote.vote_be.domain.auth.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessTokenResponse {
    private String accessToken;

    public static AccessTokenResponse of(String accessToken) {
        AccessTokenResponse response = new AccessTokenResponse();
        response.accessToken = accessToken;
        return response;
    }
}
