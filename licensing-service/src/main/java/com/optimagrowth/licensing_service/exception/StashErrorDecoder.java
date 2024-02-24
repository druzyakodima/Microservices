package com.optimagrowth.licensing_service.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.sun.jdi.InternalException;
import feign.Response;
import feign.codec.ErrorDecoder;
import javassist.NotFoundException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

//Для перехвата ошибок FeignClient
@Component
public class StashErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        ExceptionMessage message;
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, ExceptionMessage.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }

        switch (response.status()) {
            case 400:
                return new HystrixBadRequestException(message.getMessage() != null ? message.getMessage() : "Bad Request");
            case 404:
                return new NotFoundException(message.getMessage() != null ? message.getMessage() : "Not found");
            case 500:
                return new InternalException(message.getMessage() != null ? message.getMessage() : "Internal Server Error");
            default:
                return errorDecoder.decode(methodKey, response);
        }
    }
}
