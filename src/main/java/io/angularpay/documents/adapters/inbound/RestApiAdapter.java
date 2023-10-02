package io.angularpay.documents.adapters.inbound;

import io.angularpay.documents.configurations.AngularPayConfiguration;
import io.angularpay.documents.domain.UserDocument;
import io.angularpay.documents.domain.commands.*;
import io.angularpay.documents.models.*;
import io.angularpay.documents.ports.inbound.RestApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

import static io.angularpay.documents.helpers.Helper.fromHeaders;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class RestApiAdapter implements RestApiPort {

    private final CreateDocumentCommand createDocumentCommand;
    private final UpdateDocumentCommand updateDocumentCommand;
    private final GetDocumentByReferenceCommand getDocumentByReferenceCommand;
    private final DownloadDocumentByReferenceCommand downloadDocumentByReferenceCommand;
    private final DeleteDocumentCommand deleteDocumentCommand;
    private final GetDocumentListCommand getDocumentListCommand;
    private final GetUserDocumentsCommand getUserDocumentsCommand;

    private final AngularPayConfiguration configuration;

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public GenericReferenceResponse create(
            @RequestParam("file") MultipartFile file,
            @RequestParam("filename") String filename,
            @RequestParam("file_description") String fileDescription,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericDocumentApiModel genericDocumentApiModel = GenericDocumentApiModel.builder()
                .file(file)
                .filename(filename)
                .fileDescription(fileDescription)
                .build();
        CreateDocumentCommandRequest createDocumentCommandRequest = CreateDocumentCommandRequest.builder()
                .genericDocumentApiModel(genericDocumentApiModel)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.createDocumentCommand.execute(createDocumentCommandRequest);
    }

    @PutMapping("/{documentReference}")
    @Override
    public void update(
            @PathVariable String documentReference,
            @RequestParam("file") MultipartFile file,
            @RequestParam("filename") String filename,
            @RequestParam("file_description") String fileDescription,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericDocumentApiModel genericDocumentApiModel = GenericDocumentApiModel.builder()
                .file(file)
                .filename(filename)
                .fileDescription(fileDescription)
                .build();
        UpdateDocumentCommandRequest updateDocumentCommandRequest = UpdateDocumentCommandRequest.builder()
                .reference(documentReference)
                .genericDocumentApiModel(genericDocumentApiModel)
                .authenticatedUser(authenticatedUser)
                .build();
        this.updateDocumentCommand.execute(updateDocumentCommandRequest);
    }

    @GetMapping("/{documentReference}")
    @ResponseBody
    @Override
    public UserDocument getByReference(
            @PathVariable String documentReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericDocumentReferenceCommandRequest genericDocumentReferenceCommandRequest = GenericDocumentReferenceCommandRequest.builder()
                .reference(documentReference)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.getDocumentByReferenceCommand.execute(genericDocumentReferenceCommandRequest);
    }

    @GetMapping("/{documentReference}/download")
    @ResponseBody
    @Override
    public ResponseEntity<Resource> downloadByReference(
            @PathVariable String documentReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericDocumentReferenceCommandRequest genericDocumentReferenceCommandRequest = GenericDocumentReferenceCommandRequest.builder()
                .reference(documentReference)
                .authenticatedUser(authenticatedUser)
                .build();
        DownloadDocumentApiResponse response = this.downloadDocumentByReferenceCommand.execute(genericDocumentReferenceCommandRequest);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + response.getDocument().getFilename());
        responseHeaders.add("Cache-Control", "no-cache, no-store, must-revalidate");
        responseHeaders.add("Pragma", "no-cache");
        responseHeaders.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .contentLength(response.getContentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(response.getResource());
    }

    @DeleteMapping("/{documentReference}")
    @Override
    public void deleteDocument(
            @PathVariable String documentReference,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericDocumentReferenceCommandRequest genericDocumentReferenceCommandRequest = GenericDocumentReferenceCommandRequest.builder()
                .reference(documentReference)
                .authenticatedUser(authenticatedUser)
                .build();
        this.deleteDocumentCommand.execute(genericDocumentReferenceCommandRequest);
    }

    @GetMapping("/list/page/{page}")
    @ResponseBody
    @Override
    public List<UserDocument> listDocuments(
            @PathVariable int page,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericDocumentListCommandRequest genericDocumentListCommandRequest = GenericDocumentListCommandRequest.builder()
                .paging(Paging.builder().size(this.configuration.getPageSize()).index(page).build())
                .authenticatedUser(authenticatedUser)
                .build();
        return this.getDocumentListCommand.execute(genericDocumentListCommandRequest);
    }

    @GetMapping
    @ResponseBody
    @Override
    public List<UserDocument> getByOwnerReference(@RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericDocumentCommandRequest genericDocumentCommandRequest = GenericDocumentCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .build();
        return this.getUserDocumentsCommand.execute(genericDocumentCommandRequest);
    }

}
