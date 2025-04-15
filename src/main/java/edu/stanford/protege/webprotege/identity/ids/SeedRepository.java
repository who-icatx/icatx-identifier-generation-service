package edu.stanford.protege.webprotege.identity.ids;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeedRepository extends MongoRepository<Seed, String> {
}
