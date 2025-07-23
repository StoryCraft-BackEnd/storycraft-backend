package com.storycraft.reward.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StreakStatusResponseDto {
    private Long id;
    private Long childId;
    private Integer currentStreak;
    private LocalDate lastLearnedDate;
} 