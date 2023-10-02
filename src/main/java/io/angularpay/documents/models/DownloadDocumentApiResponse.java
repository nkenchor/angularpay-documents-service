package io.angularpay.documents.models;

import io.angularpay.documents.domain.UserDocument;
import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.InputStreamResource;

@Data
@Builder(toBuilder = true)
public class DownloadDocumentApiResponse {

    private UserDocument document;
    private InputStreamResource resource;
    private long contentLength;
}
