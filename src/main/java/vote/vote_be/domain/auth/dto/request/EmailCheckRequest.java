package vote.vote_be.domain.auth.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailCheckRequest {
    @NotBlank
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String value;
}
