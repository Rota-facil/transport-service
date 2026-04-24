package com.rota.facil.transport_service.persistence.entities;

import com.rota.facil.transport_service.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Entity
@Table(name = "users_tb")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @Column(name = "user_id")
    private UUID id;

    @Column(name = "prefecture_id")
    private UUID prefectureId;

    private String name;

    private String email;

    @Builder.Default
    private Double score = 5.0;

    @Enumerated(EnumType.STRING)
    private Role role;

    public void update(UserEntity userEntity) {
        if (userEntity.getPrefectureId() != null) this.prefectureId = userEntity.getPrefectureId();
        if (userEntity.getName() != null) this.name = userEntity.getName();
        if (userEntity.getEmail() != null) this.email = userEntity.getEmail();
        if (userEntity.getScore() != null) this.score = userEntity.getScore();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstitutionEntity that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
