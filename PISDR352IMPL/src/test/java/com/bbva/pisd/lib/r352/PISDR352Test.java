package com.bbva.pisd.lib.r352;

import com.bbva.apx.exception.io.network.TimeoutException;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;

import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.pisd.lib.r352.factory.ApiConnectorFactoryMock;
import com.bbva.pisd.lib.r352.impl.PISDR352Impl;
import com.bbva.pisd.lib.r352.impl.util.RimacUrlForker;
import com.bbva.pisd.mock.MockBundleContext;
import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/PISDR352-app.xml",
		"classpath:/META-INF/spring/PISDR352-app-test.xml",
		"classpath:/META-INF/spring/PISDR352-arc.xml",
		"classpath:/META-INF/spring/PISDR352-arc-test.xml" })
public class PISDR352Test {

	private static final Logger LOGGER = LoggerFactory.getLogger(PISDR352Test.class);
	private PISDR352Impl pisdr352 = new PISDR352Impl();
	private PISDR014 pisdr014;
	private MockData mockData;
	private APIConnector externalApiConnector;
	private RimacUrlForker rimacUrlForker;

	@Before
	public void setUp(){
		ThreadContext.set(new Context());

		mockData = MockData.getInstance();

		MockBundleContext mockBundleContext = mock(MockBundleContext.class);

		ApiConnectorFactoryMock apiConnectorFactoryMock = new ApiConnectorFactoryMock();

		externalApiConnector = apiConnectorFactoryMock.getAPIConnector(mockBundleContext, false);
		pisdr352.setExternalApiConnector(externalApiConnector);

		pisdr014 = mock(PISDR014.class);
		pisdr352.setPisdR014(pisdr014);

		rimacUrlForker = mock(RimacUrlForker.class);
		pisdr352.setRimacUrlForker(rimacUrlForker);

		when(pisdr014.executeSignatureConstruction(anyString(), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(new SignatureAWS("", "", "", ""));
	}

	@Test
	public void executePrePolicyEmissionServiceWithRestClientException() {
		LOGGER.info("PISDR352 - Executing executePrePolicyEmissionServiceWithRestClientException...");

		String responseBody = "{\"error\":{\"code\":\"VEHDAT005\",\"message\":\"Error al Validar Datos.\",\"details\":[\"\\\"contactoInspeccion.telefono\\\" debe contener caracteres\"],\"httpStatus\":400}}";
		when(rimacUrlForker.generatePropertyKeyName(anyString())).thenReturn("value");
		when(rimacUrlForker.generateUriForSignatureAWS(anyString(), anyString())).thenReturn("value");
		when(externalApiConnector.postForObject(anyString(), anyObject(), any(), anyMap())).thenThrow(new HttpServerErrorException(HttpStatus.BAD_REQUEST, "", responseBody.getBytes(), StandardCharsets.UTF_8));

		EmisionBO rimacResponse = pisdr352.executePrePolicyEmissionService(new EmisionBO(), "quotationId", "traceId", "830");
		assertNull(rimacResponse);
	}

	@Test
	public void executePrePolicyEmissionServiceOK() throws IOException {
		LOGGER.info("PISDR352 - Executing executePrePolicyEmissionServiceOK...");

		EmisionBO rimacResponse = mockData.getEmisionRimacResponse();
		when(rimacUrlForker.generatePropertyKeyName(anyString())).thenReturn("value");
		when(rimacUrlForker.generateUriForSignatureAWS(anyString(), anyString())).thenReturn("value");
		when(externalApiConnector.postForObject(anyString(), anyObject(), any(), anyMap())).thenReturn(rimacResponse);

		EmisionBO validation = pisdr352.executePrePolicyEmissionService(new EmisionBO(), "quotationId", "traceId", "830");

		assertNotNull(validation);
		assertNotNull(validation.getPayload());
		assertNotNull(validation.getPayload().getCotizacion());
		assertNotNull(validation.getPayload().getIndicadorRequierePago());
		assertNotNull(validation.getPayload().getCodProducto());
		assertNotNull(validation.getPayload().getNumeroPoliza());
		assertNotNull(validation.getPayload().getPrimaNeta());
		assertNotNull(validation.getPayload().getPrimaBruta());
		assertNotNull(validation.getPayload().getIndicadorInspeccion());
		assertNotNull(validation.getPayload().getIndicadorGps());
		assertNotNull(validation.getPayload().getEnvioElectronico());
		assertNotNull(validation.getPayload().getFinanciamiento());
		assertNotNull(validation.getPayload().getNumeroCuotas());
		assertNotNull(validation.getPayload().getFechaInicio());
		assertNotNull(validation.getPayload().getFechaFinal());
		assertNotNull(validation.getPayload().getCuotasFinanciamiento());
		assertFalse(validation.getPayload().getCuotasFinanciamiento().isEmpty());
		assertNotNull(validation.getPayload().getContratante());
		assertNotNull(validation.getPayload().getContratante().getTipoDocumento());
		assertNotNull(validation.getPayload().getContratante().getNumeroDocumento());
		assertNotNull(validation.getPayload().getContratante().getApellidoPaterno());
		assertNotNull(validation.getPayload().getContratante().getApellidoMaterno());
		assertNotNull(validation.getPayload().getContratante().getNombres());
		assertNotNull(validation.getPayload().getContratante().getSexo());
		assertNotNull(validation.getPayload().getContratante().getFechaNacimiento());
		assertNotNull(validation.getPayload().getContratante().getUbigeo());
		assertNotNull(validation.getPayload().getContratante().getNombreDistrito());
		assertNotNull(validation.getPayload().getContratante().getNombreProvincia());
		assertNotNull(validation.getPayload().getContratante().getNombreDepartamento());
		assertNotNull(validation.getPayload().getContratante().getNombreVia());
		assertNotNull(validation.getPayload().getContratante().getNumeroVia());
		assertNotNull(validation.getPayload().getContratante().getCorreo());
		assertNotNull(validation.getPayload().getContratante().getTelefono());
		assertNotNull(validation.getPayload().getResponsablePago());
		assertNotNull(validation.getPayload().getAsegurado());
	}

	@Test
	public void testExecutePrePolicyEmissionServiceWithRestClientTimeOutException() {
		LOGGER.info("PISDR352 - Executing testExecuteAddParticipantsServiceWithRestClientException...");

		when(rimacUrlForker.generatePropertyKeyName(anyString())).thenReturn("value");
		when(rimacUrlForker.generateUriForSignatureAWS(anyString(), anyString())).thenReturn("value");
		when(externalApiConnector.postForObject(anyString(), anyObject(), any(), anyMap())).thenThrow(TimeoutException.class);

		EmisionBO rimacResponse = this.pisdr352.executePrePolicyEmissionService(new EmisionBO(), "quotationId", "traceId", "830");
		assertNull(rimacResponse);
	}

	@Test
	public void testExecuteAddParticipantsService_OK() throws IOException{
		LOGGER.info("PISDR352 - Executing testExecuteAddParticipantsService_OK...");
		AgregarTerceroBO response = mockData.getAddParticipantsRimacResponse();

		when(this.rimacUrlForker.generateKeyAddParticipants(anyString())).thenReturn("key.property");
		when(this.rimacUrlForker.generateUriAddParticipants(anyString(),anyString())).thenReturn("value-key-1");
		when(this.externalApiConnector.exchange(anyString(), anyObject(),anyObject(), (Class<AgregarTerceroBO>) any(), anyMap())).thenReturn(new ResponseEntity<>(response,HttpStatus.OK));

		AgregarTerceroBO validation = this.pisdr352.executeAddParticipantsService(new AgregarTerceroBO(),"quotationId","840","traceId");

		assertNotNull(validation);
		assertNotNull(validation.getPayload());
		assertNotNull(validation.getPayload().getStatus());
		assertNotNull(validation.getPayload().getMensaje());
		assertEquals("1",validation.getPayload().getStatus());
		assertNotNull(validation.getPayload().getTerceros());
		assertEquals(3,validation.getPayload().getTerceros().size());
		assertNotNull(validation.getPayload().getTerceros().get(0));
		assertNotNull(validation.getPayload().getTerceros().get(1));
		assertNotNull(validation.getPayload().getTerceros().get(2));
		assertEquals(0,validation.getPayload().getBeneficiario().size());
	}

	@Test
	public void testExecuteAddParticipantsServiceWithRestClientException() {
		LOGGER.info("PISDR352 - Executing testExecuteAddParticipantsServiceWithRestClientException...");

		String responseBody = "{\"error\":{\"code\":\"VIDA001\",\"message\":\"ErroralValidarDatos.\",\"details\":[\"\\\"persona[0].celular\\\"esrequerido\"],\"httpStatus\":400}}";
		when(rimacUrlForker.generateUriAddParticipants(anyString(),anyString())).thenReturn("any-value");
		when(rimacUrlForker.generateKeyAddParticipants(anyString())).thenReturn("any-value");
		when(this.externalApiConnector.exchange(anyString(), anyObject(),anyObject(), (Class<AgregarTerceroBO>) any(), anyMap())).thenThrow(new HttpServerErrorException(HttpStatus.BAD_REQUEST, "", responseBody.getBytes(), StandardCharsets.UTF_8));

		AgregarTerceroBO validation = this.pisdr352.executeAddParticipantsService(new AgregarTerceroBO(),"quotationId","productId","traceId");
		assertNull(validation);
	}

	@Test
	public void testExecuteAddParticipantsServiceWithRestClientTimeOutException() {
		LOGGER.info("PISDR352 - Executing testExecuteAddParticipantsServiceWithRestClientException...");

		when(rimacUrlForker.generateUriAddParticipants(anyString(),anyString())).thenReturn("any-value");
		when(rimacUrlForker.generateKeyAddParticipants(anyString())).thenReturn("any-value");
		when(this.externalApiConnector.exchange(anyString(), anyObject(),anyObject(), (Class<AgregarTerceroBO>) any(), anyMap())).thenThrow(TimeoutException.class);

		AgregarTerceroBO validation = this.pisdr352.executeAddParticipantsService(new AgregarTerceroBO(),"quotationId","productId","traceId");
		assertNull(validation);
	}
}
