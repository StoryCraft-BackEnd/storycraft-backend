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

    public byte[] generateImage(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "prompt", prompt,
                "n", 1,
                "size", "512x512",
                "response_format", "b64_json"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(dalleUrl, request, Map.class);

        List<Map<String, String>> data = (List<Map<String, String>>) response.getBody().get("data");
        String base64Image = data.get(0).get("b64_json");

        return Base64.getDecoder().decode(base64Image);
    }
}
