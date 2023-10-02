package io.angularpay.documents.helpers;

import io.angularpay.documents.adapters.outbound.MongoAdapter;
import io.angularpay.documents.domain.UserDocument;
import io.angularpay.documents.exceptions.CommandException;
import io.angularpay.documents.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static io.angularpay.documents.exceptions.ErrorCode.REQUEST_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CommandHelper {

    private final MongoAdapter mongoAdapter;

    public String getRequestOwner(String requestReference) {
        UserDocument found = this.mongoAdapter.findDocumentByReference(requestReference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
        return found.getOwnerReference();
    }

    public static UserDocument getRequestByReferenceOrThrow(MongoAdapter mongoAdapter, String reference) {
        return mongoAdapter.findDocumentByReference(reference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
    }

    private static CommandException commandException(HttpStatus status, ErrorCode errorCode) {
        return CommandException.builder()
                .status(status)
                .errorCode(errorCode)
                .message(errorCode.getDefaultMessage())
                .build();
    }

}
