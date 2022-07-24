package handler;

import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.Row;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import utils.DynamoUtils;
import utils.S3Utils;

// Handler value: handler.Handler
public class Handler implements RequestHandler<S3Event, String> {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String handleRequest(S3Event s3event, Context context) {
        try {
            LambdaLogger logger = context.getLogger();
            logger.log("Received Event: " + gson.toJson(s3event));
            S3EventNotification.S3EventNotificationRecord record = s3event.getRecords().get(0);

            String srcBucket = record.getS3().getBucket().getName();

            // Object key may have spaces or unicode non-ASCII characters.
            String srcKey = record.getS3().getObject().getUrlDecodedKey();

            String dstBucket = srcBucket;
            String dstKey = srcKey.replace("inbound", "outbound");

            // Download the file from S3 into a stream
            logger.log("S3Client Creation Started");
            S3Client s3Client = S3Utils.getS3Client();
            logger.log("S3Client Created");

            List<String[]> rows = S3Utils.getFileFromS3Bucket(s3Client, srcBucket, srcKey);
            logger.log("File received File and converted to rows");

            // Upload the file from S3 into a stream
            logger.log("Started Writing");
            PutObjectResponse uploadResponse = S3Utils.uploadFileToS3Bucket(s3Client, dstBucket, dstKey, rows);
            logger.log("File Uploaded:" + dstBucket + "-" + dstKey);

            logger.log("Rows transformed to objects");
            List<Row> dbRecords = rows.stream().skip(1).map(row -> new Row(row[0], row[1], row[2], row[3]))
                    .collect(Collectors.toList());

            // Write transformed rows to dynamo db
            logger.log("DynamoDbEnhancedClient Creation Started");
            DynamoDbEnhancedClient enhancedClient = DynamoUtils.getDynamoClient();
            logger.log("DynamoDbEnhancedClient Created");
            DynamoDbTable<Row> mappedTable = enhancedClient.table("Row", TableSchema.fromBean(Row.class));

            logger.log("Started Batch Write");
            Integer pendingRecords = DynamoUtils.partitionedWrite(Row.class, dbRecords, enhancedClient, mappedTable);
            logger.log("Total pending records after batched write: " + pendingRecords);

            return uploadResponse.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
