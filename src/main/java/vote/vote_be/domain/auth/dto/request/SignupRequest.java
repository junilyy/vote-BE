package vote.vote_be.domain.auth.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vote.vote_be.domain.user.entity.Part;
import vote.vote_be.domain.user.entity.Team;

@Getter
@NoArgsConstructor
public class SignupRequest {
    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

    @Email @NotBlank
    private String email;

    @NotNull
    private Part part;   // FRONTEND / BACKEND

    @NotBlank
    private String name;

    @NotNull
    private Team team;   // MODELLY / DIGGINDIE / CATCHUP / MENUAL / STORIX
}