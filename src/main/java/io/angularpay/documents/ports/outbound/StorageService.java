package io.angularpay.documents.ports.outbound;

import io.angularpay.documents.models.DownloadDocumentApiResponse;

import java.io.IOException;

public interface StorageService {
    void saveFile(String documentReference, String ownerReference, byte[] file) throws IOException;
    DownloadDocumentApiResponse loadFile(String documentReference, String ownerReference) throws IOException;
    void deleteFile(String documentReference, String ownerReference) throws IOException;
}
