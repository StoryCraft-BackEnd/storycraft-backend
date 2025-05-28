package com.storycraft.auth.entity;

import com.storycraft.global.entity.BaseTimeEntity;
import com.storycraft.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "email_verifications")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerification extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 6)
    private String verificationCode;

    @Column(nullable = false)
    private Boolean isVerified;
}
