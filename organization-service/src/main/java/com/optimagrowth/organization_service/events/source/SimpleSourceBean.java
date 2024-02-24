package com.optimagrowth.organization_service.events.source;

import com.optimagrowth.organization_service.events.model.OrganizationChangeModel;
import com.optimagrowth.organization_service.utils.ActionEnum;
import com.optimagrowth.organization_service.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class SimpleSourceBean { // "Источник - это код, который публикует сообщение в канал" из архитектуры Spring Cloud Stream
    private final Source source;

    private static final Logger logger = LoggerFactory.getLogger(SimpleSourceBean.class);

    @Autowired
    public SimpleSourceBean(Source source) {
        this.source = source;
    }

    public void publishOrganizationChange(ActionEnum action, String organizationId) {

        logger.debug("Sending Kafka message {} for Organization Id: {}", action, organizationId);

        OrganizationChangeModel change = new OrganizationChangeModel(
                OrganizationChangeModel.class.getTypeName(),
                action,
                organizationId,
                UserContext.getCorrelationId()); // Публикует сообщение

        source.output().send(MessageBuilder.withPayload(change).build());
    }
}
