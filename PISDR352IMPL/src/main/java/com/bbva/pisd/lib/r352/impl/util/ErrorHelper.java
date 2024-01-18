package com.bbva.pisd.lib.r352.impl.util;

import com.bbva.rbvd.dto.insrncsale.bo.ErrorRimacBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.Objects;

public class ErrorHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHelper.class);
    private static final String NO_ERROR_RESPONSE = "¡ NO SE PUDO CAPTURAR EL ERROR !";

    private ErrorHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static ErrorRimacBO getMessageErrorResponseFromRimac(RestClientException exception) {
        LOGGER.info("getMessageErrorResponseFromRimac :: Start - {}", exception.getMessage());
        ErrorRimacBO rimacError = new ErrorRimacBO();
        if(exception instanceof HttpClientErrorException) {
            HttpClientErrorException httpClientErrorException = (HttpClientErrorException) exception;
            String responseBody = httpClientErrorException.getResponseBodyAsString();
            LOGGER.info("HttpClientErrorException - Response body: {}", responseBody);
            rimacError = getErrorObject(responseBody);
            return rimacError;
        } else if(Objects.nonNull(exception.getMostSpecificCause())){
            String mostSpecificCause = exception.getMostSpecificCause().getMessage();
            if(Objects.isNull(mostSpecificCause)){
                rimacError.setMessage(NO_ERROR_RESPONSE);
                return rimacError;
            }
            rimacError.setMessage(mostSpecificCause);
        }
        return rimacError;
    }

    private static ErrorRimacBO getErrorObject(String responseBody) {
        return JsonHelper.getInstance().fromString(responseBody, ErrorRimacBO.class);
    }
}
