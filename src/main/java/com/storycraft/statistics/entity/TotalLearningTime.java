package com.storycraft.statistics.entity;

import com.storycraft.global.entity.BaseTimeEntity;
import com.storycraft.profile.entity.ChildProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "total_learning_time")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalLearningTime extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false, unique = true)
    private ChildProfile child;

    @Column(name = "total_learning_time_minutes", nullable = false)
    private Long totalLearningTimeMinutes;

    @Column(name = "last_updated_at", nullable = false)
    private String lastUpdatedAt;

    // 학습 시간 업데이트 메서드
    public void updateLearningTime(Long totalLearningTimeMinutes, String lastUpdatedAt) {
        this.totalLearningTimeMinutes = totalLearningTimeMinutes;
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
