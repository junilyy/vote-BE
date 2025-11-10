package vote.vote_be.global.security.repository;

import org.springframework.data.repository.CrudRepository;
import vote.vote_be.global.security.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
