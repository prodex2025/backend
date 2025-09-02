package com.backend.backend.domain.service.s3;

import com.backend.backend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PresignService {

    private final S3Presigner presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // PUT用
    public Map<String, String> createPutUrl(String key, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PresignedPutObjectRequest presigned =
                presigner.presignPutObject(r -> r.signatureDuration(Duration.ofMinutes(10))
                        .putObjectRequest(putObjectRequest));

        return Map.of("url", presigned.url().toString(), "key", key, "contentType", contentType);
    }

    // GET用
    public String createGetUrl(String key, Duration ttl) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        PresignedGetObjectRequest presigned =
                presigner.presignGetObject(r -> r.signatureDuration(ttl)
                        .getObjectRequest(getObjectRequest));

        return presigned.url().toString();
    }
}

