package com.bbva.pisd.lib.r352.impl.util;

import com.bbva.rbvd.dto.insrncsale.bo.ErrorRimacBO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.charset.StandardCharsets;

public class ErrorHelperTest {

    @Test
    public void getMessageErrorResponseFromRimac() {
        String responseBody = "{\"error\":{\"code\":\"VIDA001\",\"message\":\"Error al Validar Datos\",\"details\":[\"La cotizacion esta en proceso.\"],\"httpStatus\":403}}";
        HttpClientErrorException clientException = new HttpClientErrorException(HttpStatus.FORBIDDEN, "", responseBody.getBytes(), StandardCharsets.UTF_8);
        ErrorRimacBO result = ErrorHelper.getMessageErrorResponseFromRimac(clientException);
        Assert.assertNotNull(result);
    }

    @Test
    public void getMessageErrorResponseFromRimacNull() {
        HttpServerErrorException clientException = new HttpServerErrorException(HttpStatus.FORBIDDEN, "", null, StandardCharsets.UTF_8);
        ErrorRimacBO result = ErrorHelper.getMessageErrorResponseFromRimac(clientException);
        Assert.assertNotNull(result);
    }

}