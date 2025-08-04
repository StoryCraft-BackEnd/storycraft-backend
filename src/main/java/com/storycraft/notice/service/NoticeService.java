package com.storycraft.notice.service;

import com.storycraft.global.exception.CustomException;
import com.storycraft.global.exception.ErrorCode;
import com.storycraft.notice.dto.NoticeResponseDto;
import com.storycraft.notice.entity.Notice;
import com.storycraft.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 활성화된 공지사항 목록 조회
     * 중요도 순으로 정렬 (HIGH -> NORMAL -> LOW)
     */
    public List<NoticeResponseDto> getActiveNotices() {
        List<Notice> notices = noticeRepository.findAllActiveNoticesOrderByImportance();
        return notices.stream()
                .map(NoticeResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 공지사항 상세 조회
     */
    public NoticeResponseDto getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));
        
        // 비활성화된 공지사항도 조회 가능하도록 함
        return NoticeResponseDto.from(notice);
    }
} 