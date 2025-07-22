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
@Table(name = "daily_mission_status", uniqueConstraints = @UniqueConstraint(columnNames = {"child_id", "mission_code"}))
public class DailyMissionStatus extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildProfile child;

    @Column(nullable = false, length = 50)
    private String missionCode;

    @Column(nullable = false)
    private Integer progressCount;

    @Column(nullable = false)
    private Boolean completed;

    // last_updated_at은 updatedAt으로 대체
} 