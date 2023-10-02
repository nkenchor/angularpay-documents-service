package io.angularpay.documents.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.documents.adapters.outbound.MongoAdapter;
import io.angularpay.documents.domain.Role;
import io.angularpay.documents.domain.SupportedFileTypes;
import io.angularpay.documents.domain.UserDocument;
import io.angularpay.documents.exceptions.CommandException;
import io.angularpay.documents.exceptions.ErrorObject;
import io.angularpay.documents.models.CreateDocumentCommandRequest;
import io.angularpay.documents.models.ResourceReferenceResponse;
import io.angularpay.documents.ports.outbound.StorageService;
import io.angularpay.documents.validation.DefaultConstraintValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static io.angularpay.documents.exceptions.ErrorCode.FILE_IO_ERROR;
import static io.angularpay.documents.exceptions.ErrorCode.UNSUPPORTED_FILE_TYPE_ERROR;

@Slf4j
@Service
public class CreateDocumentCommand extends AbstractCommand<CreateDocumentCommandRequest, ResourceReferenceResponse> {

    private final DefaultConstraintValidator validator;
    private final MongoAdapter mongoAdapter;
    private final StorageService storageService;

    public CreateDocumentCommand(
            ObjectMapper mapper,
            DefaultConstraintValidator validator,
            MongoAdapter mongoAdapter,
            StorageService storageService) {
        super("CreateDocumentCommand", mapper);
        this.validator = validator;
        this.mongoAdapter = mongoAdapter;
        this.storageService = storageService;
    }

    @Override
    protected String getResourceOwner(CreateDocumentCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected ResourceReferenceResponse handle(CreateDocumentCommandRequest request) {
        String reference = UUID.randomUUID().toString();
        String userReference = request.getAuthenticatedUser().getUserReference();

        if (Arrays.stream(SupportedFileTypes.values()).noneMatch(x -> x.getContentType().equalsIgnoreCase(request.getGenericDocumentApiModel().getFile().getContentType()))) {
            throw CommandException.builder()
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .errorCode(UNSUPPORTED_FILE_TYPE_ERROR)
                    .message(UNSUPPORTED_FILE_TYPE_ERROR.getDefaultMessage())
                    .build();
        }

        try {
            byte[] bytes = request.getGenericDocumentApiModel().getFile().getBytes();
            this.storageService.saveFile(reference, userReference, bytes);
        } catch (IOException exception) {
            throw CommandException.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode(FILE_IO_ERROR)
                    .message(FILE_IO_ERROR.getDefaultMessage())
                    .cause(exception)
                    .build();
        }

        UserDocument userDocument = UserDocument.builder()
                .reference(reference)
                .ownerReference(request.getAuthenticatedUser().getUserReference())
                .filename(request.getGenericDocumentApiModel().getFilename())
                .fileDescription(request.getGenericDocumentApiModel().getFileDescription())
                .build();

        UserDocument response = this.mongoAdapter.createDocuments(userDocument);
        return new ResourceReferenceResponse(response.getReference());
    }

    @Override
    protected List<ErrorObject> validate(CreateDocumentCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Arrays.asList(
                Role.ROLE_DOCUMENTS_ADMIN,
                Role.ROLE_PLATFORM_ADMIN
        );
    }

}
