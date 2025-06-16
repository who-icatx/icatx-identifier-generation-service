package edu.stanford.protege.webprotege.identity.ids;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static edu.stanford.protege.webprotege.identity.ids.Seed.COLLECTION_NAME;

@Document(collection = COLLECTION_NAME)
public class Seed {

    public static final String COLLECTION_NAME = "seeds";
    @Id
    private String name;
    private long value;

    public Seed() {}

    public Seed(String name, long value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
