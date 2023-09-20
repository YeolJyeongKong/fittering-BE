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

    private final AmazonS3 amazonS3;
    private static final String IMAGE_CONTENT_TYPE = "image/*";

    @Value("${cloud.aws.s3.bucket.crawling}")
    private String crawlingBucket;
    @Value("${cloud.aws.s3.bucket.server}")
    private String serverBucket;
    @Value("${cloud.aws.s3.bucket.body}")
    private String bodyBucket;
    @Value("${cloud.aws.s3.bucket.silhouette}")
    private String silhouetteBucket;

    public void moveObject(String storedFileName, String bucket) throws IOException {
        if (bucket.equals("crawling")) {
            moveObjectWithBucket(storedFileName, crawlingBucket);
        }
        if (bucket.equals("server")) {
            moveObjectWithBucket(storedFileName, serverBucket);
        }
        if (bucket.equals("body")) {
            moveObjectWithBucket(storedFileName, bodyBucket);
        }
        if (bucket.equals("silhouette")) {
            moveObjectWithBucket(storedFileName, silhouetteBucket);
        }
    }

    public void moveObjectWithBucket(String storedFileName, String bucket) throws IOException {
        S3Object savedObject = amazonS3.getObject(new GetObjectRequest(crawlingBucket, storedFileName));
        S3ObjectInputStream objectInputStream = savedObject.getObjectContent();
        byte[] fileBytes = IOUtils.toByteArray(objectInputStream);
        String fileName = URLEncoder.encode(storedFileName, UTF_8)
                .replaceAll("\\+", "%20")
                .replaceAll("%3A", ":")
                .replaceAll("%5F", "_");

        InputStream fileInputStream = new ByteArrayInputStream(fileBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(IMAGE_CONTENT_TYPE);
        metadata.setContentLength(fileBytes.length);
        amazonS3.putObject(serverBucket, fileName, fileInputStream, metadata);
    }

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

    public String saveObjectWithBucket(MultipartFile file, Long userId, String bucket) throws IOException {
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
