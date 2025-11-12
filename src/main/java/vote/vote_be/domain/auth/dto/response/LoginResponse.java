package vote.vote_be.domain.auth.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vote.vote_be.domain.user.entity.Part;
import vote.vote_be.domain.user.entity.Team;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginResponse {
    private Long userId;
    private String name;
    private Part part;
    private Team team;
    private String accessToken;

    public static LoginResponse of (Long userId, String name, Part part, Team team, String accessToken) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.userId = userId;
        loginResponse.name = name;
        loginResponse.part = part;
        loginResponse.team = team;
        loginResponse.accessToken = accessToken;

        return loginResponse;
    }
}
