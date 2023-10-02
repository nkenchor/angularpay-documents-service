package io.angularpay.documents.ports.inbound;

import io.angularpay.documents.models.platform.PlatformConfigurationIdentifier;

public interface InboundMessagingPort {
    void onMessage(String message, PlatformConfigurationIdentifier identifier);
}
