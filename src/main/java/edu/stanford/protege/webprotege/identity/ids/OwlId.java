package edu.stanford.protege.webprotege.identity.ids;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = OwlId.IDS_COLLECTION)
public class OwlId {

    public final static String IDS_COLLECTION = "OwlIds";

    @Id
    @JsonProperty("_id")
    private String value;

    public OwlId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}