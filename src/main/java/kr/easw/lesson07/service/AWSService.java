package kr.easw.lesson07.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import kr.easw.lesson07.model.dto.AWSKeyDto;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;
@Service
public class AWSService {
    private static final String BUCKET_NAME = "easw-random-bucket-" + UUID.randomUUID();
    private AmazonS3 s3Client = null;

    public void initAWSAPI(AWSKeyDto awsKey) {
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsKey.getApiKey(), awsKey.getApiSecretKey())))
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();
        for (Bucket bucket : s3Client.listBuckets()) {
            if (bucket.getName().startsWith("easw-random-bucket-")) {
                s3Client.listObjects(bucket.getName())
                        .getObjectSummaries()
                        .forEach(it -> s3Client.deleteObject(bucket.getName(), it.getKey()));
                s3Client.deleteBucket(bucket.getName());
            }
        }
        s3Client.createBucket(BUCKET_NAME);
    }

    public boolean isInitialized() {
        return s3Client != null;
    }

    public List<String> getFileList() {
        return s3Client.listObjects(BUCKET_NAME).getObjectSummaries().stream().map(S3ObjectSummary::getKey).toList();
    }

    @SneakyThrows
    public void upload(MultipartFile file) {
        // 실습용 버킷에 파일을 업로드합니다.
        s3Client.putObject(BUCKET_NAME, file.getOriginalFilename(), new ByteArrayInputStream(file.getResource().getContentAsByteArray()), new ObjectMetadata());
    }

    @SneakyThrows
    public Resource downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(BUCKET_NAME, fileName);

        if (s3Object == null) {
            throw new IllegalArgumentException("다운로드할 파일이 존재하지 않습니다.");
        }

        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        return new InputStreamResource(inputStream);
    }
}
