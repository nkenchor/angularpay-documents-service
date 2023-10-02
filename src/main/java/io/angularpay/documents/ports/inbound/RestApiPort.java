package io.angularpay.documents.ports.inbound;

import io.angularpay.documents.domain.UserDocument;
import io.angularpay.documents.models.GenericReferenceResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface RestApiPort {

    GenericReferenceResponse create(MultipartFile file, String filename, String fileDescription, Map<String, String> headers);
    void update(String documentReference, MultipartFile file, String filename, String fileDescription, Map<String, String> headers);
    UserDocument getByReference(String documentReference, Map<String, String> headers);
    ResponseEntity<Resource> downloadByReference(String documentReference, Map<String, String> headers);
    void deleteDocument(String documentReference, Map<String, String> headers);
    List<UserDocument> listDocuments(int page, Map<String, String> headers);
    List<UserDocument> getByOwnerReference(Map<String, String> headers);
}
