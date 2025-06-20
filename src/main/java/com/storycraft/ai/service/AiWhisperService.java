package com.storycraft.ai.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class AiWhisperService {

    private final String apiKey;
    private final String whisperUrl;

    public AiWhisperService() {
        Dotenv dotenv = Dotenv.configure().load();
        this.apiKey = dotenv.get("OPENAI_API_KEY");
        this.whisperUrl = dotenv.get("OPENAI_WHISPER_URL");
    }

    public String transcribeAudio(File audioFile) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(apiKey);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(audioFile));
        body.add("model", "whisper-1");

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(whisperUrl, request, Map.class);

        Map<String, Object> responseBody = response.getBody();
        return responseBody != null ? (String) responseBody.get("text") : null;
    }

    public String transcribeAudio(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("audio", ".mp3");
        file.transferTo(tempFile);
        return transcribeAudio(tempFile); // 기존 File 기반 메서드 호출
    }
}
