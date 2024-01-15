package com.bbva.pisd.lib.r352.impl;

import com.bbva.apx.exception.io.network.TimeoutException;
import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.pisd.dto.invoice.OperationDTO;
import com.bbva.pisd.dto.invoice.constants.PISDConstant;
import com.bbva.pisd.lib.r352.impl.util.ErrorHelper;
import com.bbva.pisd.lib.r352.impl.util.JsonHelper;
import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import static java.util.Collections.singletonMap;

public class PISDR352Impl extends PISDR352Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(PISDR352Impl.class);
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String X_AMZ_DATE_HEADER = "X-Amz-Date";
	private static final String X_API_KEY_HEADER = "x-api-key";
	private static final String TRACE_ID_HEADER = "traceId";
	private static final String QUERY_SELECT_SEQUENCE_INCIDENT = "PISD.SELECT_SEQUENCE_INCIDENT";
	private static final String FIELD_NEW_INCIDENT_SEQL_NUMBER = "NEW_INCIDENT_SEQL_NUMBER";
	private static final String QUERY_INSERT_INSURANCE_INCIDENT = "PISD.INSERT_INSURANCE_INCIDENT";
	private static final String FIELD_INCIDENT_SEQL_NUMBER = "INCIDENT_SEQL_NUMBER";
	private static final String FIELD_INSURANCE_CONTRACT_ENTITY_ID = "INSURANCE_CONTRACT_ENTITY_ID";
	private static final String FIELD_INSURANCE_CONTRACT_BRANCH_ID = "INSURANCE_CONTRACT_BRANCH_ID";
	private static final String FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID = "INSRC_CONTRACT_INT_ACCOUNT_ID";
	private static final String FIELD_OPERATION_DATE = "OPERATION_DATE";
	private static final String FIELD_INCIDENCE_ERROR_ID = "INCIDENCE_ERROR_ID";
	private static final String FIELD_INCIDENCE_ERROR_DESC = "INCIDENCE_ERROR_DESC";
	private static final String FIELD_POLICY_RECEIPT_ID = "POLICY_RECEIPT_ID";
	private static final String FIELD_AUTH_ORIGIN_CURNCY_ID = "AUTH_ORIGIN_CURNCY_ID";
	private static final String FIELD_DEBIT_ACCOUNT_ID = "DEBIT_ACCOUNT_ID";
	private static final String FIELD_OPERATION_STATUS_ID = "OPERATION_STATUS_ID";
	private static final String FIELD_CREATION_USER_ID = "CREATION_USER_ID";
	private static final String FIELD_USER_AUDIT_ID = "USER_AUDIT_ID";
	private static final Integer DOSCIENTOS_CINCUENTA_Y_CINCO = 254;
	public static final String PISDJ351 = "PISDJ351";

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
		} catch (HttpStatusCodeException ex){
			String message = ex.getResponseBodyAsString();

			OperationDTO operation =OperationDTO.Builder.an()
					.withNameProp(QUERY_SELECT_SEQUENCE_INCIDENT)
					.withTypeOperation(PISDConstant.Operation.SELECT)
					.withIsForListQuery(false)
					.withParams(null).build();
			Map<String, Object> result = (Map<String, Object>)pisdR201.executeQuery(operation);
			Long resultIndex = Long.valueOf(result.get(FIELD_NEW_INCIDENT_SEQL_NUMBER).toString());


			Map<String, Object> params = new HashMap<>();
			params.put(FIELD_INCIDENT_SEQL_NUMBER, resultIndex);
			params.put(FIELD_INSURANCE_CONTRACT_ENTITY_ID, "0011" );
			params.put(FIELD_INSURANCE_CONTRACT_BRANCH_ID, "0130" );
			params.put(FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID, "4030204777" );
			params.put(FIELD_OPERATION_DATE, null );
			params.put(FIELD_INCIDENCE_ERROR_ID, "PISD35100400" );
			params.put(FIELD_INCIDENCE_ERROR_DESC, StringUtils.left(message, DOSCIENTOS_CINCUENTA_Y_CINCO) );
			params.put(FIELD_POLICY_RECEIPT_ID, null );
			params.put(FIELD_AUTH_ORIGIN_CURNCY_ID, null );
			params.put(FIELD_DEBIT_ACCOUNT_ID, null );
			params.put(FIELD_OPERATION_STATUS_ID, "01" );
			params.put(FIELD_CREATION_USER_ID, PISDJ351 );
			params.put(FIELD_USER_AUDIT_ID,  PISDJ351 );
			LOGGER.info("[PISDR352Impl] - cratedIncident() :: Incident with param:: {}", params);
			OperationDTO operation1 = OperationDTO.Builder.an()
					.withNameProp(QUERY_INSERT_INSURANCE_INCIDENT)
					.withTypeOperation(PISDConstant.Operation.UPDATE)
					.withParams(params).build();
			int resultInsert = (int) pisdR201.executeQuery(operation1);
			LOGGER.info("***** PISDR352Impl -  ***** executePrePolicyEmissionService - HttpStatusCodeException: {}", message);
			return null;
		} catch (RestClientException ex) {
			String message = ErrorHelper.getMessageErrorResponseFromRimac(ex);

			OperationDTO operation =OperationDTO.Builder.an()
					.withNameProp(QUERY_SELECT_SEQUENCE_INCIDENT)
					.withTypeOperation(PISDConstant.Operation.SELECT)
					.withIsForListQuery(false)
					.withParams(null).build();
			Map<String, Object> result = (Map<String, Object>)pisdR201.executeQuery(operation);
			Long resultIndex = Long.valueOf(result.get(FIELD_NEW_INCIDENT_SEQL_NUMBER).toString());


			Map<String, Object> params = new HashMap<>();
			params.put(FIELD_INCIDENT_SEQL_NUMBER, resultIndex);
			params.put(FIELD_INSURANCE_CONTRACT_ENTITY_ID, "0011" );
			params.put(FIELD_INSURANCE_CONTRACT_BRANCH_ID, "0130" );
			params.put(FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID, "4030204777" );
			params.put(FIELD_OPERATION_DATE, null );
			params.put(FIELD_INCIDENCE_ERROR_ID, "PISD35100400" );
			params.put(FIELD_INCIDENCE_ERROR_DESC, StringUtils.left(message, DOSCIENTOS_CINCUENTA_Y_CINCO) );
			params.put(FIELD_POLICY_RECEIPT_ID, null );
			params.put(FIELD_AUTH_ORIGIN_CURNCY_ID, null );
			params.put(FIELD_DEBIT_ACCOUNT_ID, null );
			params.put(FIELD_OPERATION_STATUS_ID, "01" );
			params.put(FIELD_CREATION_USER_ID, PISDJ351 );
			params.put(FIELD_USER_AUDIT_ID,  PISDJ351 );
			LOGGER.info("[PISDR352Impl] - cratedIncident() :: Incident with param:: {}", params);
			OperationDTO operation1 = OperationDTO.Builder.an()
					.withNameProp(QUERY_INSERT_INSURANCE_INCIDENT)
					.withTypeOperation(PISDConstant.Operation.UPDATE)
					.withParams(params).build();
			int resultInsert = (int) pisdR201.executeQuery(operation1);

			LOGGER.info("***** PISDR352Impl -  ***** executePrePolicyEmissionService - RestClientException: {}", message);
			LOGGER.info("***** PISDR352Impl -  ***** executePrePolicyEmissionService - RestClientException: {}", ex.getMessage());
			return null;
		} catch (TimeoutException ex) {
			LOGGER.info("***** PISDR352Impl -  ***** TimeoutException: {}", ex.getMessage());
			return null;
		}

		return responseBody;
	}

	@Override
	public AgregarTerceroBO executeAddParticipantsService(AgregarTerceroBO requestBody, String quotationId, String productId, String traceId) {
		LOGGER.info("***** PISDR352Impl - executeAddParticipantsService START *****");

		LOGGER.info("***** requestBody: {} :: quotationId: {}", requestBody, quotationId);
		LOGGER.info("***** productId: {} :: traceId: {}", productId, traceId);

		String jsonString = getRequestBodyAsJsonFormat(requestBody);
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
			LOGGER.info("***** PISDR352Impl - executeAddParticipantsService ***** Response: {}", output.getPayload().getMensaje());
			LOGGER.info("***** PISDR352Impl - executeAddParticipantsService END *****");
			return output;
		} catch (HttpStatusCodeException e){
			LOGGER.info("***** PISDR352Impl -  ***** executeAddParticipantsService - HttpStatusCodeException: {}", e.getResponseBodyAsString());
			return null;
		} catch (RestClientException ex) {
			String message = ErrorHelper.getMessageErrorResponseFromRimac(ex);
			LOGGER.info("***** PISDR352Impl -  ***** executeAddParticipantsService - RestClientException: {}", message);
			LOGGER.info("***** PISDR352Impl -  ***** executeAddParticipantsService - RestClientException: {}", ex.getMessage());
			return null;
		} catch (TimeoutException ex) {
			LOGGER.info("***** PISDR352Impl -  ***** TimeoutException: {}", ex.getMessage());
			return null;
		}

	}

	private String getRequestBodyAsJsonFormat(Object requestBody) {
		LOGGER.info("getRequestBodyAsJsonFormat START {} *****", requestBody);

		return JsonHelper.getInstance().toJsonString(requestBody);
	}

	private HttpHeaders createHttpHeadersAWS(SignatureAWS signature) {
		LOGGER.info("createHttpHeadersAWS START {} *****", signature);
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application","json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.set(AUTHORIZATION_HEADER, signature.getAuthorization());
		headers.set(X_AMZ_DATE_HEADER, signature.getxAmzDate());
		headers.set(X_API_KEY_HEADER, signature.getxApiKey());
		headers.set(TRACE_ID_HEADER, signature.getTraceId());

		LOGGER.info("createHttpHeadersAWS END *****");
		return headers;
	}
}
