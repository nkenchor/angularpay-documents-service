package io.angularpay.documents.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.documents.adapters.outbound.MongoAdapter;
import io.angularpay.documents.domain.UserDocument;
import io.angularpay.documents.domain.Role;
import io.angularpay.documents.exceptions.ErrorObject;
import io.angularpay.documents.models.GenericDocumentListCommandRequest;
import io.angularpay.documents.validation.DefaultConstraintValidator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GetDocumentListCommand extends AbstractCommand<GenericDocumentListCommandRequest, List<UserDocument>>
        implements LargeDataResponseCommand {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;

    public GetDocumentListCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator) {
        super("GetDocumentListCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
    }

    @Override
    protected String getResourceOwner(GenericDocumentListCommandRequest request) {
        return "";
    }

    @Override
    protected List<UserDocument> handle(GenericDocumentListCommandRequest request) {
        Pageable pageable = PageRequest.of(request.getPaging().getIndex(), request.getPaging().getSize());
        return this.mongoAdapter.listDocuments(pageable);
    }

    @Override
    protected List<ErrorObject> validate(GenericDocumentListCommandRequest request) {
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
