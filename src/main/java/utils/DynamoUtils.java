package utils;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.Lists;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoUtils {

    private static final int BATCH_SIZE = 25;

    public static DynamoDbEnhancedClient getDynamoClient() {
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();
    }

    public static <T> Integer partitionedWrite(Class<T> itemType, List<T> items, DynamoDbEnhancedClient client,
            DynamoDbTable<T> table) {
        List<T> failedRequests = new ArrayList<>();
        List<List<T>> partitions = Lists.partition(items, BATCH_SIZE);

        partitions.forEach(partition -> {
            BatchWriteItemEnhancedRequest batchWriteItems = getBatchedWriteItems(itemType, partition, table);
            failedRequests.addAll(client.batchWriteItem(batchWriteItems).unprocessedPutItemsForTable(table));
        });

        return failedRequests.size();
    }

    private static <T> BatchWriteItemEnhancedRequest getBatchedWriteItems(Class<T> itemType, List<T> items,
            DynamoDbTable<T> table) {
        WriteBatch.Builder<T> writeBatchBuilder = WriteBatch.builder(itemType).mappedTableResource(table);
        items.forEach(writeBatchBuilder::addPutItem);

        return BatchWriteItemEnhancedRequest.builder().addWriteBatch(writeBatchBuilder.build()).build();
    }

}
