package com.storycraft.statistics.repository;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.statistics.entity.TotalLearningTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TotalLearningTimeRepository extends JpaRepository<TotalLearningTime, Long> {

    /**
     * 특정 자녀의 총 학습 시간 기록을 조회합니다.
     */
    Optional<TotalLearningTime> findByChild(ChildProfile child);

    /**
     * 특정 자녀의 총 학습 시간 기록이 존재하는지 확인합니다.
     */
    boolean existsByChild(ChildProfile child);
}
