package com.backend.backend.domain.service.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3Mover {
    private final S3Client s3;
    
    @Value("${cloud.aws.s3.bucket}") 
    String bucket;

    public String copy(String srcKey, String destKey) {
        s3.copyObject(b -> b.sourceBucket(bucket).sourceKey(srcKey)
                .destinationBucket(bucket).destinationKey(destKey));
        return destKey;
    }

    public List<String> copyBatch(List<String> srcKeys, java.util.function.Function<String,String> toDest) {
        if (srcKeys == null) return List.of();
        List<String> dest = new ArrayList<>();
        for (String src : srcKeys) {
            String d = toDest.apply(src);
            copy(src, d);
            dest.add(d);
        }
        return dest;
    }

    public void deleteBatch(List<String> keys) {
        if (keys == null || keys.isEmpty()) return;
        for (String k : keys) s3.deleteObject(b -> b.bucket(bucket).key(k));
    }

    public void deletePrefix(String prefix) {
        try {
            String continuationToken = null;
            do {
                // prefix配下のオブジェクトをリストアップ
                ListObjectsV2Request.Builder listReqBuilder = ListObjectsV2Request.builder()
                        .bucket(bucket)
                        .prefix(prefix);
                if (continuationToken != null) {
                    listReqBuilder.continuationToken(continuationToken);
                }

                ListObjectsV2Response listResp = s3.listObjectsV2(listReqBuilder.build());

                // オブジェクトがあればまとめて削除
                List<ObjectIdentifier> keysToDelete = new ArrayList<>();
                for (S3Object s3Object : listResp.contents()) {
                    keysToDelete.add(ObjectIdentifier.builder().key(s3Object.key()).build());
                }

                if (!keysToDelete.isEmpty()) {
                    DeleteObjectsRequest delReq = DeleteObjectsRequest.builder()
                            .bucket(bucket)
                            .delete(Delete.builder().objects(keysToDelete).build())
                            .build();
                    s3.deleteObjects(delReq);
                }

                continuationToken = listResp.nextContinuationToken();
            } while (continuationToken != null);

        } catch (S3Exception e) {
            System.err.println("S3削除エラー: " + e.awsErrorDetails().errorMessage());
        }
    }
}
