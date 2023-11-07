package com.bbva.pisd.lib.r352.impl;

import com.bbva.apx.exception.io.network.TimeoutException;
import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.pisd.lib.r352.impl.util.JsonHelper;
import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import static java.util.Collections.singletonMap;

public class PISDR352Impl extends PISDR352Abstract {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String X_AMZ_DATE_HEADER = "X-Amz-Date";
	private static final String X_API_KEY_HEADER = "x-api-key";
	private static final String TRACE_ID_HEADER = "traceId";
	private static final Logger LOGGER = LoggerFactory.getLogger(PISDR352Impl.class);

	@Override
	public EmisionBO executePrePolicyEmissionService(EmisionBO requestBody, String quotationId, String traceId, String productId){
		LOGGER.info("***** PISDR352Impl - executePrePolicyEmissionService START *****");

		String jsonString = getRequestBodyAsJsonFormat(requestBody);

		LOGGER.info("***** PISDR352Impl - executePrePolicyEmissionService ***** Param: {}", jsonString);

		EmisionBO responseBody = null;

		SignatureAWS signature = this.pisdR014.executeSignatureConstruction(jsonString, HttpMethod.POST.toString(),
				this.rimacUrlForker.generateUriForSignatureAWS(productId, quotationId), null, traceId);

		HttpEntity<String> entity = new HttpEntity<>(jsonString, createHttpHeadersAWS(signature));

		Map<String, String> uriParam = new HashMap<>();
		uriParam.put("ideCotizacion", quotationId);

		try {
			responseBody = this.externalApiConnector.postForObject(this.rimacUrlForker.generatePropertyKeyName(productId), entity,
					EmisionBO.class, uriParam);
			LOGGER.info("***** PISDR352Impl - executePrePolicyEmissionService ***** Response: {}", getRequestBodyAsJsonFormat(responseBody));
			LOGGER.info("***** PISDR352Impl - executePrePolicyEmissionService END *****");
		} catch (RestClientException ex) {
			LOGGER.info("***** PISDR352Impl - executePrePolicyEmissionService ***** Exception: {}", ex.getMessage());
		} catch (TimeoutException ex) {
			LOGGER.info("***** PISDR352Impl - executePrePolicyEmissionService ***** Exception: {}", ex.getMessage());
			return null;
		}

		return responseBody;
	}

	@Override
	public AgregarTerceroBO executeAddParticipantsService(AgregarTerceroBO requestBody, String quotationId, String productId, String traceId) {
		LOGGER.info("***** PISDR352Impl - executeAddParticipantsService START *****");

		LOGGER.info("***** requestBody: {} :: quotationId: {}", requestBody, quotationId);
		LOGGER.info("***** productId: {} :: traceId: {}", productId, traceId);


		LOGGER.info("***** PISDR352Impl:  LOG DE PRUEBA  *****");
		String jsonString = this.getRequestBodyAsJsonFormat(requestBody);
		LOGGER.info("***** PISDR352Impl - jsonString: {}", jsonString);

		AgregarTerceroBO output = null;

		SignatureAWS signature = this.pisdR014.executeSignatureConstruction(jsonString, HttpMethod.PATCH.toString(),
				this.rimacUrlForker.generateUriAddParticipants(quotationId,productId), null, traceId
		);
		LOGGER.info("***** PISDR352Impl - executeAddParticipantsService ***** signature: {}", signature);

		HttpEntity<String> entity = new HttpEntity<>(jsonString, createHttpHeadersAWS(signature));
		LOGGER.info("***** PISDR352Impl - executeAddParticipantsService ***** entity: {}", entity);

		try {
			ResponseEntity<AgregarTerceroBO> response = this.externalApiConnector.exchange(this.rimacUrlForker.generateKeyAddParticipants(productId),HttpMethod.PATCH, entity,
					AgregarTerceroBO.class, singletonMap("cotizacion",quotationId));
			output = response.getBody();
			LOGGER.info("***** PISDR352Impl - executeAddParticipantsService ***** Response: {}", this.getRequestBodyAsJsonFormat(output));
			LOGGER.info("***** PISDR352Impl - executeAddParticipantsService END *****");
			return output;
		} catch (RestClientException ex) {
			if(ex instanceof HttpStatusCodeException) {
				HttpStatusCodeException exception = (HttpClientErrorException) ex;
				LOGGER.info("***** PISDR352Impl -  ***** Exception: {}", exception);
				LOGGER.info("***** PISDR352Impl -  ***** Exception: {}", exception.getResponseBodyAsString());
			}
			LOGGER.info("***** PISDR352Impl -  ***** RestClientException: {}", ex.getMessage());
			return null;
		} catch (TimeoutException ex) {
			LOGGER.info("***** PISDR352Impl -  ***** TimeoutException: {}", ex.getMessage());
			return null;
		}

	}

	private String getRequestBodyAsJsonFormat(Object requestBody) {
		LOGGER.info("getRequestBodyAsJsonFormat START *****");

		return JsonHelper.getInstance().toJsonString(requestBody);
	}

	private HttpHeaders createHttpHeadersAWS(SignatureAWS signature) {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application","json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.set(AUTHORIZATION_HEADER, signature.getAuthorization());
		headers.set(X_AMZ_DATE_HEADER, signature.getxAmzDate());
		headers.set(X_API_KEY_HEADER, signature.getxApiKey());
		headers.set(TRACE_ID_HEADER, signature.getTraceId());
		return headers;
	}
}
