package com.storycraft.reward.repository;

import com.storycraft.reward.entity.StreakStatus;
import com.storycraft.profile.entity.ChildProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StreakStatusRepository extends JpaRepository<StreakStatus, Long> {
    Optional<StreakStatus> findByChild(ChildProfile child);
} 