package utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public class S3Utils {

    public static S3Client getS3Client() {
        return S3Client.create();
    }

    public static List<String[]> getFileFromS3Bucket(S3Client s3Client, String bucket, String key) throws Exception {
        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key).build());

        CSVReader reader = CsvUtils.getReader(new InputStreamReader(s3Object));
        return reader.readAll();
    }

    public static PutObjectResponse uploadFileToS3Bucket(S3Client s3Client, String bucket, String key,
            List<String[]> rows) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        try (CSVWriter writer = CsvUtils.getWriter(outputStreamWriter)) {
            writer.writeAll(rows);
            writer.flush();
            PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucket).key(key).build();
            return s3Client.putObject(objectRequest,
                    RequestBody.fromByteBuffer(ByteBuffer.wrap(outputStream.toByteArray())));
        }
    }
}
