package com.storycraft.global.util;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


@Component
public class S3Uploader {

    private S3Client s3Client;
    private String bucket;
    private String region;

    // S3Client와 bucket 정보 초기화
    @PostConstruct
    public void init() {
        Dotenv dotenv = Dotenv.configure().load();

        String accessKey = dotenv.get("AWS_ACCESS_KEY");
        String secretKey = dotenv.get("AWS_SECRET_KEY");
        this.region = dotenv.get("AWS_REGION");
        this.bucket = dotenv.get("AWS_BUCKET");

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }

    public String uploadBytes(byte[] bytes, String dirName, String fileName) {
        String key = dirName + "/" + UUID.randomUUID() + "-" + fileName;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(bytes));

        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }

    public String upload(File file, String dirName) throws IOException {
        String fileName = dirName + "/" + UUID.randomUUID() + "-" + file.getName();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                //.acl("public-read")
                .build();

        PutObjectResponse response = s3Client.putObject(request, file.toPath());

        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
    }

    // MultipartFile을 File로 변환 후 업로드
    public String uploadMultipart(MultipartFile multipartFile, String dirName) throws IOException {
        File file = convertMultipartFileToFile(multipartFile);
        String uploadUrl = upload(file, dirName);
        file.delete(); // 임시 파일 삭제
        return uploadUrl;
    }

    // MultipartFile → File
    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }
        return file;
    }
}

