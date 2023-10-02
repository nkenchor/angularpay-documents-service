package io.angularpay.documents.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UpdateDocumentCommandRequest extends AccessControl {

    @NotEmpty
    private String reference;

    @NotNull
    @Valid
    private GenericDocumentApiModel genericDocumentApiModel;

    UpdateDocumentCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
