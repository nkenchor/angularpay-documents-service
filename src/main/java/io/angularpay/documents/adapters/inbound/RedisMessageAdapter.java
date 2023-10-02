package io.angularpay.documents.adapters.inbound;

import io.angularpay.documents.domain.commands.PlatformConfigurationsConverterCommand;
import io.angularpay.documents.models.platform.PlatformConfigurationIdentifier;
import io.angularpay.documents.ports.inbound.InboundMessagingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.angularpay.documents.models.platform.PlatformConfigurationSource.TOPIC;

@Service
@RequiredArgsConstructor
public class RedisMessageAdapter implements InboundMessagingPort {

    private final PlatformConfigurationsConverterCommand converterCommand;

    @Override
    public void onMessage(String message, PlatformConfigurationIdentifier identifier) {
        this.converterCommand.execute(message, identifier, TOPIC);
    }
}
