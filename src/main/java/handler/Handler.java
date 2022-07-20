package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

            return "Ok";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
