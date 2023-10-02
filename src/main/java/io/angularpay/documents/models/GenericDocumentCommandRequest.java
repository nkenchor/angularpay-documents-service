package io.angularpay.documents.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class GenericDocumentCommandRequest extends AccessControl {

    GenericDocumentCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
