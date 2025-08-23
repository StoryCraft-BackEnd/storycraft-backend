package com.storycraft.reward.repository;

import com.storycraft.reward.entity.DailyMissionStatus;
import com.storycraft.profile.entity.ChildProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DailyMissionStatusRepository extends JpaRepository<DailyMissionStatus, Long> {
    List<DailyMissionStatus> findAllByChild(ChildProfile child);
    Optional<DailyMissionStatus> findByChildAndMissionCode(ChildProfile child, String missionCode);
    List<DailyMissionStatus> findAllByChildAndCreatedAtBetween(ChildProfile child, LocalDateTime from, LocalDateTime to);
    
    void deleteAllByChild(ChildProfile child);
} 