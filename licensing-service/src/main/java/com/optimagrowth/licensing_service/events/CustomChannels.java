package com.optimagrowth.licensing_service.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {

    @Input("inboundOrgChanges") // Определяет имя канала. Для каждого входного канала нужно определить свой метод с @Input, возвращающий экземпляр SubscribableChannel.
    SubscribableChannel orgs(); // Возвращает экземпляр класса SubscribableChannel для каждого канала, указанного в аннотации @Input.

    @Output("outboundOrg") // Метод выходного канала в который будут помещаться сообщения.
    MessageChannel outboundOrg();
}