package edu.stanford.protege.webprotege.identity.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import edu.stanford.protege.webprotege.identity.ids.OwlId;

import java.io.IOException;

public class OwlIdDeserializer extends StdDeserializer<OwlId> {

    public OwlIdDeserializer() {
        super(OwlId.class);
    }

    @Override
    public OwlId deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        // Extract the "_id" field
        JsonNode idNode = node.get("_id");
        if (idNode != null && !idNode.isNull()) {
            return new OwlId(idNode.asText());
        } else {
            return new OwlId(null);  // Or throw an exception if _id is required
        }
    }
}
