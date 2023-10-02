package io.angularpay.documents.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class GenericDocumentListCommandRequest extends AccessControl {

    @NotNull
    @Valid
    private Paging paging;

    GenericDocumentListCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
