package io.angularpay.documents.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.documents.adapters.outbound.MongoAdapter;
import io.angularpay.documents.domain.Role;
import io.angularpay.documents.domain.SupportedFileTypes;
import io.angularpay.documents.domain.UserDocument;
import io.angularpay.documents.exceptions.CommandException;
import io.angularpay.documents.exceptions.ErrorObject;
import io.angularpay.documents.helpers.CommandHelper;
import io.angularpay.documents.models.UpdateDocumentCommandRequest;
import io.angularpay.documents.ports.outbound.StorageService;
import io.angularpay.documents.validation.DefaultConstraintValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static io.angularpay.documents.exceptions.ErrorCode.FILE_IO_ERROR;
import static io.angularpay.documents.exceptions.ErrorCode.UNSUPPORTED_FILE_TYPE_ERROR;
import static io.angularpay.documents.helpers.CommandHelper.getRequestByReferenceOrThrow;

@Slf4j
@Service
public class UpdateDocumentCommand extends AbstractCommand<UpdateDocumentCommandRequest, Void> {

    private final DefaultConstraintValidator validator;
    private final MongoAdapter mongoAdapter;
    private final StorageService storageService;
    private final CommandHelper commandHelper;

    public UpdateDocumentCommand(
            ObjectMapper mapper,
            DefaultConstraintValidator validator,
            MongoAdapter mongoAdapter,
            StorageService storageService,
            CommandHelper commandHelper) {
        super("UpdateDocumentCommand", mapper);
        this.validator = validator;
        this.mongoAdapter = mongoAdapter;
        this.storageService = storageService;
        this.commandHelper = commandHelper;
    }

    @Override
    protected String getResourceOwner(UpdateDocumentCommandRequest request) {
        return this.commandHelper.getRequestOwner(request.getReference());
    }

    @Override
    protected Void handle(UpdateDocumentCommandRequest request) {
        UserDocument found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getReference());

        if (Arrays.stream(SupportedFileTypes.values()).noneMatch(x -> x.getContentType().equalsIgnoreCase(request.getGenericDocumentApiModel().getFile().getContentType()))) {
            throw CommandException.builder()
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .errorCode(UNSUPPORTED_FILE_TYPE_ERROR)
                    .message(UNSUPPORTED_FILE_TYPE_ERROR.getDefaultMessage())
                    .build();
        }

        try {
            byte[] bytes = request.getGenericDocumentApiModel().getFile().getBytes();
            this.storageService.saveFile(request.getReference(), request.getAuthenticatedUser().getUserReference(), bytes);
        } catch (IOException exception) {
            throw CommandException.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode(FILE_IO_ERROR)
                    .message(FILE_IO_ERROR.getDefaultMessage())
                    .cause(exception)
                    .build();
        }

        UserDocument userDocument = found.toBuilder()
                .filename(request.getGenericDocumentApiModel().getFilename())
                .fileDescription(request.getGenericDocumentApiModel().getFileDescription())
                .build();

        this.mongoAdapter.updateDocument(userDocument);
        return null;
    }

    @Override
    protected List<ErrorObject> validate(UpdateDocumentCommandRequest request) {
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
