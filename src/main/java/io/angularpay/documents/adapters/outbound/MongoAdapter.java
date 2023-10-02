package io.angularpay.documents.adapters.outbound;

import io.angularpay.documents.domain.UserDocument;
import io.angularpay.documents.ports.outbound.PersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MongoAdapter implements PersistencePort {

    private final DocumentsRepository documentsRepository;

    @Override
    public UserDocument createDocuments(UserDocument request) {
        request.setCreatedOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        request.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return documentsRepository.save(request);
    }

    @Override
    public UserDocument updateDocument(UserDocument request) {
        request.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return documentsRepository.save(request);
    }

    @Override
    public Optional<UserDocument> findDocumentByReference(String reference) {
        return documentsRepository.findByReference(reference);
    }

    @Override
    public void deleteDocument(UserDocument userDocument) {
        documentsRepository.delete(userDocument);
    }

    @Override
    public List<UserDocument> listDocuments(Pageable pageable) {
        return documentsRepository.findAll();
    }

    @Override
    public List<UserDocument> findDocumentByOwnerReference(String ownerReference) {
        return documentsRepository.findByOwnerReference(ownerReference);
    }
}
