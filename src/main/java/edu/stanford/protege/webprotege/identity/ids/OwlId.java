package edu.stanford.protege.webprotege.identity.ids;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = OwlId.IDS_COLLECTION)
public class OwlId {

    public final static String IDS_COLLECTION = "OwlIds";

    @Id
    private String id;
    private String value;

    public OwlId(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}