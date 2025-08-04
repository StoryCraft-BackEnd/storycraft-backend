package com.storycraft.event.dto;

import com.storycraft.event.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventListResponseDto {

    private Long id;
    private String title;
    private String summary;
    private String eventPeriod;
    private boolean isOngoing;
    private String participantCount;

    public static EventListResponseDto from(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String period = event.getStartDate().format(formatter) + " ~ " + event.getEndDate().format(formatter);
        
        return EventListResponseDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .summary(event.getSummary())
                .eventPeriod(period)
                .isOngoing(event.isOngoing())
                .participantCount(event.getParticipantCount())
                .build();
    }
} 