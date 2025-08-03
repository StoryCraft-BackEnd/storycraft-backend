package com.storycraft.event.repository;

import com.storycraft.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * 진행중인 이벤트 조회 (활성화된 것만)
     * 시작일 <= 오늘 <= 종료일
     */
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.startDate <= :today AND e.endDate >= :today ORDER BY e.createdAt DESC")
    List<Event> findOngoingEvents(LocalDate today);

    /**
     * 지난 이벤트 조회 (활성화된 것만)
     * 종료일 < 오늘
     */
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.endDate < :today ORDER BY e.endDate DESC")
    List<Event> findPastEvents(LocalDate today);
} 