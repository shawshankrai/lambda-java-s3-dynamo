package handler;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;

import utils.CsvUtils;

// Handler value: handler.Handler
public class Handler implements RequestHandler<S3Event, String> {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String handleRequest(S3Event s3event, Context context) {
        try {
            LambdaLogger logger = context.getLogger();
            logger.log("EVENT: "+ gson.toJson(s3event));
            S3EventNotification.S3EventNotificationRecord record = s3event.getRecords().get(0);

            String srcBucket = record.getS3().getBucket().getName();

            // Object key may have spaces or unicode non-ASCII characters.
            String srcKey = record.getS3().getObject().getUrlDecodedKey();

            String dstBucket = srcBucket;
            String dstKey = srcKey.replace("inbound", "outbound");

            // Download the image from S3 into a stream
            AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(
              srcBucket, srcKey));
            
            InputStream objectData = s3Object.getObjectContent();
            
            CSVReader reader = CsvUtils.getReader(objectData);
            List<String[]> rows = reader.readAll();

            rows.stream()
                .flatMap(Arrays::stream)
                .collect(Collectors.toList())
                .forEach(element -> logger.log("Elements: " + element));

            logger.log("Dest Key and Bucket:" + dstKey + ", " + dstBucket);

            return "Ok";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
