package vote.vote_be.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import vote.vote_be.global.entity.BaseEntity;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "login_id", length = 20, nullable = false, unique = true)   // VARCHAR(20)
    private String loginId;

    @Column(name = "password", length = 255, nullable = false)                   // VARCHAR(255)
    private String password;

    @Column(name = "email", length = 50, nullable = false, unique = true)                         // VARCHAR(50)
    private String email;

    @Column(name = "name", length = 20, nullable = false)                       // VARCHAR(20)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "part", nullable = false)
    private Part part;

    @Enumerated(EnumType.STRING)
    @Column(name = "team", nullable = false)
    private Team team;

    // 일단 UserRole 도입.
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "user_role", nullable = false)
    private UserRole userRole = UserRole.USER;
}
