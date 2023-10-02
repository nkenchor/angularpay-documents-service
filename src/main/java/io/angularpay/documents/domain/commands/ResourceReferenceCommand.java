package io.angularpay.documents.domain.commands;

public interface ResourceReferenceCommand<T, R> {

    R map(T referenceResponse);
}
