package com.bbva.pisd.lib.r352;

import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;

/**
 * The  interface PISDR352 class...
 */
public interface PISDR352 {

	EmisionBO executePrePolicyEmissionService(EmisionBO requestBody, String quotationId, String traceId, String productId);
	AgregarTerceroBO executeAddParticipantsService(AgregarTerceroBO requestBody, String quotationId, String productId, String traceId);
}
