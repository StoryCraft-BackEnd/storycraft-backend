package com.storycraft.event.service;

import com.storycraft.global.exception.CustomException;
import com.storycraft.global.exception.ErrorCode;
import com.storycraft.event.dto.EventDetailResponseDto;
import com.storycraft.event.dto.EventListResponseDto;
import com.storycraft.event.entity.Event;
import com.storycraft.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    /**
     * 진행중인 이벤트 목록 조회
     */
    public List<EventListResponseDto> getOngoingEvents() {
        LocalDate today = LocalDate.now();
        List<Event> events = eventRepository.findOngoingEvents(today);
        return events.stream()
                .map(EventListResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 지난 이벤트 목록 조회
     */
    public List<EventListResponseDto> getPastEvents() {
        LocalDate today = LocalDate.now();
        List<Event> events = eventRepository.findPastEvents(today);
        return events.stream()
                .map(EventListResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 이벤트 상세 조회
     */
    public EventDetailResponseDto getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        return EventDetailResponseDto.from(event);
    }
} 