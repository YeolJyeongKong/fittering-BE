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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

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

    public void moveS3ObjectToServerBucket(String storedFileName) throws IOException {
        S3Object o = amazonS3.getObject(new GetObjectRequest(crawlingBucket, storedFileName));
        S3ObjectInputStream objectInputStream = o.getObjectContent();
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
}
