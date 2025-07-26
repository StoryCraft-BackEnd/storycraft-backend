package com.storycraft.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.storycraft.ai.dto.AiQuizResponseDto;
import com.storycraft.ai.dto.StoryContentDto;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


@Service
public class AiGptService {

    private String apiKey;
    private String gptUrl;
    private String gptModel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiGptService(){
        Dotenv dotenv = Dotenv.configure().load();

        this.apiKey = dotenv.get("OPENAI_API_KEY");
        this.gptModel = dotenv.get("OPENAI_GPT_MODEL");
        this.gptUrl = dotenv.get("OPENAI_GPT_URL");
    }

    public StoryContentDto generateStoryContent(List<String> keyword) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);


        Map<String, Object> system = Map.of(
                "role","system",
                "content", "너는 유아를 위한 따뜻하고 창의적인 동화를 쓰는 작가야.");

        String keywordStr = String.join(", ", keyword);

        String prompt = """
                다음 JSON 형식으로 유아 영어 교육용 동화를 만들어줘.
                키워드: %s
                총 3~5개의 단락으로 구성하고, 한 단락안에는 5문장 정도로 이야기가 마무리 지어지도록 생성하고, 단락마다 줄바꿈(\\n\\n)으로 구분해줘.
                다음 형식의 JSON만 응답해줘. 설명이나 기타 문장 없이, 반드시 아래 형식 그대로 응답해:
                {
                  "title": "동화 제목",
                  "content": "영어로 된 단락1 내용.\\n\\n 영어로 된 단락2 내용.\\n\\n 영어로 된 단락3 내용."
                }
                """.formatted(keywordStr);

        Map<String, Object> user = Map.of("role", "user", "content", prompt);
        Map<String, Object> body = Map.of(
                "model", gptModel,
                "messages", List.of(system, user),
                "temperature", 0.8
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(gptUrl, request, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String rawJson = (String) message.get("content");

        try {
            // GPT가 ```json ... ``` 으로 감쌀 경우 제거
            rawJson = rawJson
                    .replaceAll("(?i)^```json", "")
                    .replaceAll("^```", "")
                    .replaceAll("```$", "")
                    .trim();

            Map<String, String> parsed = objectMapper.readValue(rawJson, Map.class);
            String title = parsed.getOrDefault("title", "동화 제목 없음").trim();
            String content = parsed.getOrDefault("content", "").trim();

            if (title.length() > 255) {
                title = title.substring(0, 255);
            }

            return new StoryContentDto(title, content, contentKr);

        } catch (Exception e) {
            throw new RuntimeException("GPT 응답 파싱 실패: " + e.getMessage());
        }
    }

    public List<AiQuizResponseDto> generateQuizFromContentAndKeywords(String content, List<String> keywords) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> system = Map.of(
                "role", "system",
                "content", "너는 유아를 위한 교육적인 퀴즈를 잘 만드는 AI야."
        );

        String keywordStr = String.join(", ", keywords);
        //TODO: 추후 총 10개의 퀴즈 생성으로 수정
        String prompt = """
                아래 동화 내용과 하이라이트된 단어를 중심으로 1개의 객관식 퀴즈를 만들어줘.
                반드시 JSON 형식으로만 응답해줘. 설명 문장 없이.
                
                동화 내용:
                %s
                
                하이라이트 키워드: %s
                
                예시:
                [
                    {
                        "question": "Which animal went on the Adventure?",
                        "options": {
                            "A": "Lion",
                            "B": "Cat",
                            "C": "Dog",
                            "D": "Rabbit"
                        },
                        "answer": "D"
                    }
                ]
                """.formatted(content, keywordStr);

        Map<String, Object> user = Map.of("role", "user", "content", prompt);
        Map<String, Object> body = Map.of(
                "model", gptModel,
                "messages", List.of(system, user),
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(gptUrl, request, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String rawJson = (String) message.get("content");

        try {
            // GPT가 ```json ... ``` 으로 감쌀 경우 제거
            rawJson = rawJson
                    .replaceAll("(?i)^```json", "")
                    .replaceAll("^```", "")
                    .replaceAll("```$", "")
                    .trim();
            return objectMapper.readValue(rawJson, new TypeReference<>() {
            });

        } catch (Exception e) {
            throw new RuntimeException("GPT 퀴즈 응답 파싱 실패: " + e.getMessage());
        }
    }
}
