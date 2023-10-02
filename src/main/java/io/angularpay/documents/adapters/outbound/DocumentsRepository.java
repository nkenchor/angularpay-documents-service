package io.angularpay.documents.adapters.outbound;

import io.angularpay.documents.domain.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentsRepository extends MongoRepository<UserDocument, String> {

    Optional<UserDocument> findByReference(String reference);
    List<UserDocument> findByOwnerReference(String ownerReference);
}
