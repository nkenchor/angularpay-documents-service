package io.angularpay.documents.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.documents.adapters.outbound.MongoAdapter;
import io.angularpay.documents.domain.Role;
import io.angularpay.documents.domain.UserDocument;
import io.angularpay.documents.exceptions.CommandException;
import io.angularpay.documents.exceptions.ErrorObject;
import io.angularpay.documents.helpers.CommandHelper;
import io.angularpay.documents.models.GenericDocumentReferenceCommandRequest;
import io.angularpay.documents.validation.DefaultConstraintValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static io.angularpay.documents.exceptions.ErrorCode.REQUEST_NOT_FOUND;

@Service
public class GetDocumentByReferenceCommand extends AbstractCommand<GenericDocumentReferenceCommandRequest, UserDocument>
        implements LargeDataResponseCommand {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CommandHelper commandHelper;

    public GetDocumentByReferenceCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            CommandHelper commandHelper) {
        super("GetDocumentByReferenceCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.commandHelper = commandHelper;
    }

    @Override
    protected String getResourceOwner(GenericDocumentReferenceCommandRequest request) {
        return this.commandHelper.getRequestOwner(request.getReference());
    }

    @Override
    protected UserDocument handle(GenericDocumentReferenceCommandRequest request) {
        return this.mongoAdapter.findDocumentByReference(request.getReference())
                .orElseThrow(() -> CommandException.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .errorCode(REQUEST_NOT_FOUND)
                        .message(REQUEST_NOT_FOUND.getDefaultMessage())
                        .build());
    }

    @Override
    protected List<ErrorObject> validate(GenericDocumentReferenceCommandRequest request) {
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
