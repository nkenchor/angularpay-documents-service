package io.angularpay.documents.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.documents.adapters.outbound.MongoAdapter;
import io.angularpay.documents.domain.Role;
import io.angularpay.documents.domain.UserDocument;
import io.angularpay.documents.exceptions.ErrorObject;
import io.angularpay.documents.models.GenericDocumentCommandRequest;
import io.angularpay.documents.validation.DefaultConstraintValidator;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GetUserDocumentsCommand extends AbstractCommand<GenericDocumentCommandRequest, List<UserDocument>>
        implements LargeDataResponseCommand {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;

    public GetUserDocumentsCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator) {
        super("GetUserDocumentsCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
    }

    @Override
    protected String getResourceOwner(GenericDocumentCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected List<UserDocument> handle(GenericDocumentCommandRequest request) {
        return this.mongoAdapter.findDocumentByOwnerReference(request.getAuthenticatedUser().getUserReference());
    }

    @Override
    protected List<ErrorObject> validate(GenericDocumentCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }
}
