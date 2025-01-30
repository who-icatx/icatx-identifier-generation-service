package edu.stanford.protege.webprotege.identity.ids;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.webprotege.common.ProjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static edu.stanford.protege.webprotege.identity.ids.OwlId.IDS_COLLECTION;
import static java.util.stream.Collectors.toList;

@Repository
public class IdentificationRepository {


    @Value("${icatx.versioning.savebatchsize}")
    private int batchSize;

    private final MongoTemplate mongoTemplate;

    private final ObjectMapper objectMapper;

    public IdentificationRepository(MongoTemplate mongoTemplate, ObjectMapper objectMapper) {
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
    }

    public List<String> getExistingIds() {
        return StreamSupport.stream(mongoTemplate.getCollection(IDS_COLLECTION)
                                .find().spliterator(),
                        false
                )
                .map(doc -> objectMapper.convertValue(doc, OwlId.class))
                .map(OwlId::getValue)
                .collect(toList());
    }
    // Method to save the list in pages of 1000 elements
    public void saveListInPages(List<String> idsToBeSaved) {
        // Split the list into chunks of 1000
        List<List<String>> chunks = splitListIntoChunks(idsToBeSaved);

        for (List<String> chunk : chunks) {
            // Convert each chunk into StringDocument objects
            List<OwlId> documents = new ArrayList<>();
            for (String value : chunk) {
                documents.add(new OwlId(value));
            }

            // Perform bulk insert for the current chunk
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, OwlId.class);
            bulkOps.insert(documents);
            bulkOps.execute();
        }
    }

    // Utility method to split a list into chunks
    private List<List<String>> splitListIntoChunks(List<String> list) {
        List<List<String>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(list.size(), i + batchSize);
            chunks.add(list.subList(i, end));
        }
        return chunks;
    }
}
