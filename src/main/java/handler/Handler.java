package handler;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import utils.CsvUtils;

// Handler value: handler.Handler
public class Handler implements RequestHandler<S3Event, String> {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String handleRequest(S3Event s3event, Context context) {
        try {
            LambdaLogger logger = context.getLogger();
            logger.log("Received Event: "+ gson.toJson(s3event));
            S3EventNotification.S3EventNotificationRecord record = s3event.getRecords().get(0);

            String srcBucket = record.getS3().getBucket().getName();

            // Object key may have spaces or unicode non-ASCII characters.
            String srcKey = record.getS3().getObject().getUrlDecodedKey();

            String dstBucket = srcBucket;
            String dstKey = srcKey.replace("inbound", "outbound");

            // Download the image from S3 into a stream
            logger.log("S3Client Creation Started");
            S3Client s3Client = S3Client.create();
            logger.log("S3Client Created");
            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(GetObjectRequest.builder().bucket(srcBucket).key(srcKey).build());
            logger.log("Received File");
            InputStreamReader objectData = new InputStreamReader(s3Object);
            
            CSVReader reader = CsvUtils.getReader(objectData);
            List<String[]> rows = reader.readAll();
            logger.log("File converted to rows");

            logger.log("Started Writing");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            
            try(CSVWriter writer = CsvUtils.getWriter(outputStreamWriter)) {
                writer.writeAll(rows);
                writer.flush();
                logger.log("Write Complete");
                logger.log("File Upload Started:" +dstBucket+"-"+dstKey);
                PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(dstBucket).key(dstKey).build();
                s3Client.putObject(objectRequest, RequestBody.fromByteBuffer(ByteBuffer.wrap(outputStream.toByteArray())));                
            }

            logger.log("File Uploaded:" +dstBucket+"-"+dstKey);
            return "Ok";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
