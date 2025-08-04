package com.storycraft.notice.repository;

import com.storycraft.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    /**
     * 활성화된 공지사항을 중요도 순으로 조회
     * HIGH -> NORMAL -> LOW 순서
     */
    @Query("SELECT n FROM Notice n WHERE n.isActive = true ORDER BY " +
           "CASE n.importance " +
           "WHEN 'HIGH' THEN 1 " +
           "WHEN 'NORMAL' THEN 2 " +
           "WHEN 'LOW' THEN 3 " +
           "END, n.createdAt DESC")
    List<Notice> findAllActiveNoticesOrderByImportance();
} 