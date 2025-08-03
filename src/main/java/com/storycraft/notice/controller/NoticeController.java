package com.storycraft.notice.controller;

import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.notice.dto.NoticeResponseDto;
import com.storycraft.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@Tag(name = "Notice", description = "공지사항 API")
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 목록 조회", description = "활성화된 공지사항을 중요도 순으로 조회")
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<NoticeResponseDto>>> getNotices() {
        List<NoticeResponseDto> notices = noticeService.getActiveNotices();
        return ResponseEntity.ok(new ApiResponseDto<>(200, "공지사항 목록 조회 성공", notices));
    }

    @Operation(summary = "공지사항 상세 조회", description = "특정 공지사항의 상세 내용을 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<NoticeResponseDto>> getNoticeById(@PathVariable Long id) {
        NoticeResponseDto notice = noticeService.getNoticeById(id);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "공지사항 상세 조회 성공", notice));
    }
} 