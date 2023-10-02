package io.angularpay.documents.ports.outbound;

import io.angularpay.documents.domain.UserDocument;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PersistencePort {
    UserDocument createDocuments(UserDocument userDocument);
    UserDocument updateDocument(UserDocument userDocument);
    Optional<UserDocument> findDocumentByReference(String reference);
    List<UserDocument> listDocuments(Pageable pageable);
    void deleteDocument(UserDocument userDocument);
    List<UserDocument> findDocumentByOwnerReference(String ownerReference);
}
