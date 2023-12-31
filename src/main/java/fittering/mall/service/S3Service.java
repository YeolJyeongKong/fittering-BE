package fittering.mall.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.*;

@Service
@RequiredArgsConstructor
public class S3Service {

    private static final String IMAGE_CONTENT_TYPE = "image/*";

    @Value("${cloud.aws.s3.bucket.crawling}")
    private String crawlingBucket;
    @Value("${cloud.aws.s3.bucket.server}")
    private String serverBucket;
    @Value("${cloud.aws.s3.bucket.body}")
    private String bodyBucket;
    @Value("${cloud.aws.s3.bucket.silhouette}")
    private String silhouetteBucket;

    private final AmazonS3 amazonS3;

    public String saveObject(MultipartFile file, Long userId, String bucket) throws IOException {
        if (bucket.equals("crawling")) {
            return saveObjectWithBucket(file, userId, crawlingBucket);
        }
        if (bucket.equals("server")) {
            return saveObjectWithBucket(file, userId, serverBucket);
        }
        if (bucket.equals("body")) {
            return saveObjectWithBucket(file, userId, bodyBucket);
        }
        if (bucket.equals("silhouette")) {
            return saveObjectWithBucket(file, userId, silhouetteBucket);
        }
        return null;
    }

    private String saveObjectWithBucket(MultipartFile file, Long userId, String bucket) throws IOException {
        String fileName = userId + "_" + LocalDateTime.now();
        byte[] fileBytes = file.getBytes();
        InputStream fileInputStream = new ByteArrayInputStream(fileBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(IMAGE_CONTENT_TYPE);
        metadata.setContentLength(fileBytes.length);
        amazonS3.putObject(bucket, fileName, fileInputStream, metadata);
        return fileName;
    }
}
