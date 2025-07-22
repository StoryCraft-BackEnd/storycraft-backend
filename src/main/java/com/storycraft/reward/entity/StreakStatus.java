package com.storycraft.reward.entity;

import com.storycraft.profile.entity.ChildProfile;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "streak_status", uniqueConstraints = @UniqueConstraint(columnNames = {"child_id"}))
public class StreakStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildProfile child;

    @Column(nullable = false)
    private Integer currentStreak;

    @Column(nullable = false)
    private LocalDate lastLearnedDate;
} 