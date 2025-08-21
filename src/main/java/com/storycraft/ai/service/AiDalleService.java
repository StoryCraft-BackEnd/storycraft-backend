package com.storycraft.ai.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class AiDalleService {

    private final String apiKey;
    private final String dalleUrl;

    public AiDalleService() {
        Dotenv dotenv = Dotenv.configure().load();
        this.apiKey = dotenv.get("OPENAI_API_KEY");
        this.dalleUrl = dotenv.get("OPENAI_DALLE_URL");
    }

    public byte[] generateImagePublic(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", "dall-e-3",
                "prompt", prompt,
                "n", 1,
                "size", "1024x1024",
                "response_format", "b64_json"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(dalleUrl, request, Map.class);

        List<Map<String, String>> data = (List<Map<String, String>>) response.getBody().get("data");
        String base64Image = data.get(0).get("b64_json");

        return Base64.getDecoder().decode(base64Image);
    }

    public String getIllustrationPrompt(String paragraphText) {
        return String.join("\n",
                "다음 영어 문장을 장면 설명으로 사용해서 어린이 동화 삽화를 그려줘: \"" + paragraphText + "\"",
                "",
                "  첫 번째로 생성되는 이미지에서 정의된 주인공의 외형(얼굴 형태, 눈 크기, 색, 비율)을 기준으로",
                "  이후 모든 이미지에서도 동일하게 유지해줘.",
                "",
                "스타일은 어린이 동화책 삽화 느낌으로,",
                "- 따뜻한 수채화/과슈 느낌, 부드러운 종이 질감",
                "- 파스텔 톤 색감, 부드러운 경계",
                "- 단순하고 깔끔한 선, 둥글고 귀여운 비율",
                "- 아늑하고 친근한 분위기",
                "- 너무 세밀하지 않고 단순화된 배경",
                "- 전체적으로 일관된 동화책 그림체",
                "",
                "구도:",
                "- 아이들이 보기 편한 중간 샷(정면 또는 3/4 시점)",
                "- 주인공 캐릭터가 화면의 시선 중심에 잘 보이도록",
                "- 산만한 배경/과도한 디테일은 피하기",
                "",
                "절대 포함하지 마:",
                "- 글자, 문자, 숫자, 간판, 로고, 워터마크",
                "- 포토리얼리즘/3D/거친 질감/강한 조명 효과",
                "- 캡션 또는 UI 오버레이"
        );
    }

    public byte[] generateImage(String paragraphText) {
        String prompt = getIllustrationPrompt(paragraphText);
        return generateImagePublic(prompt);
    }

}
