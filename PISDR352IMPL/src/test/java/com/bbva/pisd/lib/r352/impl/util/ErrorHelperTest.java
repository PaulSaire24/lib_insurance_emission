package com.bbva.pisd.lib.r352.impl.util;

import com.bbva.rbvd.dto.renovation.external.ErrorRimacBO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class ErrorHelperTest {

    @Test
    public void getMessageErrorResponseFromRimac() {
        String responseBody = "{\"error\":{\"code\":\"VIDA001\",\"message\":\"Error al Validar Datos\",\"details\":[\"La cotizacion esta en proceso.\"],\"httpStatus\":403}}";
        HttpClientErrorException clientException = new HttpClientErrorException(HttpStatus.FORBIDDEN, "", responseBody.getBytes(), StandardCharsets.UTF_8);
        String result = ErrorHelper.getMessageErrorResponseFromRimac(clientException);
        Assert.assertNotNull(result);
    }

    @Test
    public void getMessageErrorResponseFromRimacNull() {
        HttpClientErrorException clientException = new HttpClientErrorException(HttpStatus.FORBIDDEN, "", null, StandardCharsets.UTF_8);
        String result = ErrorHelper.getMessageErrorResponseFromRimac(clientException);
        Assert.assertNotNull(result);
    }

    @Test
    public void extractResponseFromRimac() {

        String result = ErrorHelper.extractResponseFromRimac(new ErrorRimacBO());
        Assert.assertNotNull(result);
    }
}