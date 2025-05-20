package com.storycraft.auth.entity;

import com.storycraft.user.entity.User;
import com.storycraft.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auth_tokens")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthToken extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String refreshToken;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
