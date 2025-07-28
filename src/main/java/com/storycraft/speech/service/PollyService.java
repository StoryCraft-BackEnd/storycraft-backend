package com.storycraft.speech.service;

import com.storycraft.global.util.S3Uploader;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class PollyService {

    private final S3Uploader s3Uploader;

    public String synthesizeTtsToS3(String text, String voiceId, float speechRate, String dirName) {
        String ssml = "<speak><prosody rate=\"" + (int) (speechRate * 100) + "%\">" + text + "</prosody></speak>";

        SynthesizeSpeechRequest request = SynthesizeSpeechRequest.builder()
                .textType(TextType.SSML)
                .text(ssml)
                .voiceId(voiceId)
                .outputFormat(OutputFormat.MP3)
                .build();

        Dotenv dotenv = Dotenv.configure().load();
        String accessKey = dotenv.get("AWS_ACCESS_KEY");
        String secretKey = dotenv.get("AWS_SECRET_KEY");
        String region = dotenv.get("AWS_REGION");

        try (PollyClient polly = PollyClient.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                ).build();
             ResponseInputStream<SynthesizeSpeechResponse> response = polly.synthesizeSpeech(request)) {

            File tempFile = File.createTempFile("tts-", ".mp3");
            Files.copy(response, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return s3Uploader.upload(tempFile, dirName);
        } catch (IOException | PollyException e) {
            throw new RuntimeException("TTS 생성 실패: " + e.getMessage(), e);
        }
    }
}

