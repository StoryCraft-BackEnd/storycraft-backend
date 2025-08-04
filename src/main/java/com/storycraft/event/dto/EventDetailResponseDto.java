package com.storycraft.event.dto;

import com.storycraft.event.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailResponseDto {

    private Long id;
    private String title;
    private String description;
    private String eventPeriod;
    private String participantCount;
    private String reward;
    private LocalDateTime createdAt;

    public static EventDetailResponseDto from(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String period = event.getStartDate().format(formatter) + " ~ " + event.getEndDate().format(formatter);
        
        return EventDetailResponseDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventPeriod(period)
                .participantCount(event.getParticipantCount())
                .reward(event.getReward())
                .createdAt(event.getCreatedAt())
                .build();
    }
} 