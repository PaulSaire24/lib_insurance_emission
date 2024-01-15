package com.bbva.pisd.lib.r352.impl.util;

import com.bbva.rbvd.dto.renovation.constants.RBVDConstant;
import com.bbva.rbvd.dto.renovation.external.ErrorRimacBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ErrorHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHelper.class);

    public static String getMessageErrorResponseFromRimac(RestClientException exception) {
        LOGGER.info("getMessageErrorResponseFromRimac :: Start - {}", exception.getMessage());

        if(exception instanceof HttpClientErrorException) {
            HttpClientErrorException httpClientErrorException = (HttpClientErrorException) exception;

            String responseBody = httpClientErrorException.getResponseBodyAsString();
            LOGGER.info("HttpClientErrorException - Response body: {}", responseBody);

            return responseBody;
        }

        LOGGER.info("RimacExceptionHandler not instanceof HttpServerErrorException");

        LOGGER.info("getMessageErrorResponseFromRimac :: End - {}", exception.getMostSpecificCause().getMessage());
        return exception.getMostSpecificCause().getMessage();
    }

}
