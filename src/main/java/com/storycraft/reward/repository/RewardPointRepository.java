package com.storycraft.reward.repository;

import com.storycraft.reward.entity.RewardPoint;
import com.storycraft.profile.entity.ChildProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RewardPointRepository extends JpaRepository<RewardPoint, Long> {
    @Query("SELECT COALESCE(SUM(r.points), 0) FROM RewardPoint r WHERE r.child = :child")
    int sumPointsByChild(ChildProfile child);

    List<RewardPoint> findAllByChildAndCreatedAtBetween(ChildProfile child, java.time.LocalDateTime from, java.time.LocalDateTime to);
    int countByChildAndRewardType(ChildProfile child, String rewardType);
    int countByChildAndRewardTypeAndCreatedAtBetween(ChildProfile child, String rewardType, java.time.LocalDateTime from, java.time.LocalDateTime to);
} 