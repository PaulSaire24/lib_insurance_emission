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

            //ErrorRimacBO errorRimacBO = getErrorObject(responseBody);
            //String resultMessage = extractResponseFromRimac(errorRimacBO);

            /*if(resultMessage.equalsIgnoreCase(RBVDConstant.MessageResponse.NO_ERROR_RESPONSE)){
                String mostSpecificCause = httpClientErrorException.getMostSpecificCause().getMessage();

                LOGGER.info("getMessageErrorResponseFromRimac :: End - mostSpecificCause - {}", mostSpecificCause);

                return  Objects.isNull(mostSpecificCause) ? RBVDConstant.MessageResponse.NO_ERROR_RESPONSE : mostSpecificCause;
            }

            LOGGER.info("getMessageErrorResponseFromRimac :: End - resultMessage - {}", resultMessage);*/

            return responseBody;
        }

        LOGGER.info("RimacExceptionHandler not instanceof HttpServerErrorException");

        LOGGER.info("getMessageErrorResponseFromRimac :: End - {}", exception.getMostSpecificCause().getMessage());
        return exception.getMostSpecificCause().getMessage();
    }

    /*public static String extractResponseFromRimac(ErrorRimacBO errorRimacBO){
        LOGGER.info("extractResponseFromRimac :: Start - {}", errorRimacBO);

        if(Objects.isNull(errorRimacBO)){
            LOGGER.info("getMessageErrorResponseFromRimac :: End - NO SE PUDO CAPTURAR EL ERROR");
            return RBVDConstant.MessageResponse.NO_ERROR_RESPONSE;
        }else if(Objects.isNull(errorRimacBO.getError()) || Objects.isNull(errorRimacBO.getError().getDetails())){
            LOGGER.info("getMessageErrorResponseFromRimac :: End - erroMessage - {}", errorRimacBO.getMessage());
            return Objects.isNull(errorRimacBO.getMessage())? RBVDConstant.MessageResponse.NO_ERROR_RESPONSE : errorRimacBO.getMessage();
        }
        String messageError = errorRimacBO.getError().getMessage();
        StringBuilder stringBuilder = new StringBuilder(Objects.isNull(messageError)?RBVDConstant.MessageResponse.NO_ERROR_RESPONSE: messageError.replace(".",""));
        stringBuilder.append(" : ");
        List<String> details = CollectionUtils.isEmpty(errorRimacBO.getError().getDetails()) ? Collections.emptyList() : errorRimacBO.getError().getDetails();
        details.forEach(value ->{
            stringBuilder.append(value);
            stringBuilder.append(" ");
        });

        LOGGER.info("extractResponseFromRimac :: End - {}", stringBuilder);
        return stringBuilder.toString();
    }

    private static ErrorRimacBO getErrorObject(String responseBody) {
        LOGGER.info("getErrorObject :: Start");
        return JsonHelper.getInstance().fromString(responseBody, ErrorRimacBO.class);
    }*/

}
