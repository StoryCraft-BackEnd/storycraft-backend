package com.storycraft.event.controller;

import com.storycraft.global.response.ApiResponseDto;
import com.storycraft.event.dto.EventDetailResponseDto;
import com.storycraft.event.dto.EventListResponseDto;
import com.storycraft.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Event", description = "이벤트 API")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "진행중인 이벤트 목록 조회", description = "현재 진행중인 이벤트 목록을 조회")
    @GetMapping("/ongoing")
    public ResponseEntity<ApiResponseDto<List<EventListResponseDto>>> getOngoingEvents() {
        List<EventListResponseDto> events = eventService.getOngoingEvents();
        return ResponseEntity.ok(new ApiResponseDto<>(200, "진행중인 이벤트 목록 조회 성공", events));
    }

    @Operation(summary = "지난 이벤트 목록 조회", description = "종료된 이벤트 목록을 조회")
    @GetMapping("/past")
    public ResponseEntity<ApiResponseDto<List<EventListResponseDto>>> getPastEvents() {
        List<EventListResponseDto> events = eventService.getPastEvents();
        return ResponseEntity.ok(new ApiResponseDto<>(200, "지난 이벤트 목록 조회 성공", events));
    }

    @Operation(summary = "이벤트 상세 조회", description = "특정 이벤트의 상세 내용을 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<EventDetailResponseDto>> getEventById(@PathVariable Long id) {
        EventDetailResponseDto event = eventService.getEventById(id);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "이벤트 상세 조회 성공", event));
    }
} 