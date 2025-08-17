package com.storycraft.speech.dto;

import com.storycraft.speech.enums.VoiceId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TtsCreateForm {

    @Schema(description = "읽을 동화 ID(선택). sectionId로부터 유추 가능", example = "1")
    private Long storyId;

    @NotNull
    @Schema(description = "읽을 단락(섹션) ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long sectionId;

    @NotNull
    @Schema(description = "Polly 성우(드롭다운)", implementation = VoiceId.class,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private VoiceId voiceId;

    @Schema(description = "재생 속도(0.5~1.5 권장)", example = "1.0", defaultValue = "1.0", minimum = "0.5", maximum = "1.5")
    @DecimalMin("0.5") @DecimalMax("1.5")
    private Float speechRate = 1.0f;
}
