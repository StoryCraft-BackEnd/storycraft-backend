package com.storycraft.reward.entity;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reward_points")
public class RewardPoint extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildProfile child;

    @Column(nullable = false, length = 50)
    private String rewardType;

    @Column(nullable = false, length = 50)
    private String context;

    @Column(nullable = false)
    private Integer points;

    // awarded_at은 createdAt으로 대체
} 