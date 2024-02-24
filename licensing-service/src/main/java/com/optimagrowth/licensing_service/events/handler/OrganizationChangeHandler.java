package com.optimagrowth.licensing_service.events.handler;

import com.optimagrowth.licensing_service.events.CustomChannels;
import com.optimagrowth.licensing_service.events.model.OrganizationChangeModel;
import com.optimagrowth.licensing_service.utils.ActionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(CustomChannels.class)
public class OrganizationChangeHandler { // "Приемник - это код реализующий получение сообщений из канала" в архитектуре Spring Cloud Stream

    private static final Logger logger = LoggerFactory.getLogger(OrganizationChangeHandler.class);

    @StreamListener("inboundOrgChanges")
    public void loggerSink(OrganizationChangeModel organization) {

        logger.debug("Received a message of type " + organization.getType());

        ActionEnum action = organization.getAction();

        switch(action){
            case GET:
                logger.debug("Received a GET event from the organization service for organization id {}", organization.getOrganizationId());
                break;
            case SAVE:
                logger.debug("Received a SAVE event from the organization service for organization id {}", organization.getOrganizationId());
                break;
            case UPDATED:
                logger.debug("Received a UPDATE event from the organization service for organization id {}", organization.getOrganizationId());
                break;
            case DELETED:
                logger.debug("Received a DELETE event from the organization service for organization id {}", organization.getOrganizationId());
                break;
            default:
                logger.error("Received an UNKNOWN event from the organization service of type {}", organization.getType());
                break;
        }
    }


}