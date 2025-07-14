package com.storycraft.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
public class SesConfig {

    @Value("${aws.ses.region}")
    private String region;

    @Value("${aws.access.key.id:}")
    private String accessKeyId;

    @Value("${aws.secret.access.key:}")
    private String secretAccessKey;

    @Bean
    public SesClient sesClient() {
        if (accessKeyId.isEmpty() || secretAccessKey.isEmpty()) {
            // IAM 역할이나 환경 변수를 통한 인증 (EC2, ECS 등에서 권장)
            return SesClient.builder()
                    .region(Region.of(region))
                    .build();
        } else {
            // 액세스 키를 통한 인증 (로컬 개발 환경)
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
            return SesClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                    .build();
        }
    }
} 