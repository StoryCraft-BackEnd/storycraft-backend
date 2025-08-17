package com.storycraft.speech.dto;

import com.storycraft.speech.enums.VoiceId;
import com.storycraft.story.entity.Story;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Schema(description = "TTS 음성 생성 요청 DTO")
public class TtsCreateRequestDto {

    @Schema(description = "읽을 동화 ID", example = "1")
    private Long storyId;

    @Schema(description = "읽을 단락 ID", example = "1")
    private Long sectionId;

    @Schema(description="Polly 성우(드롭다운 표시)", implementation = VoiceId.class)
    private VoiceId voiceId;

    @Schema(description="재생 속도(0.5~1.5 권장)", example="0.9", minimum="0.5", maximum="1.5", defaultValue="1.0")
    @DecimalMin("0.5") @DecimalMax("1.5")
    private float speechRate = 1.0f;
}
