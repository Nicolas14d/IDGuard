package com.idguardia.service;

import com.idguardia.dto.AnalisisDTOs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MLServiceClient {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${idguardia.ml.service.url}")
    private String mlServiceUrl;

    public MLServiceResult inferir(String contenidoRaw, String tipoContenido) {
        MLServicePayload payload = new MLServicePayload();
        payload.setContenido_raw(contenidoRaw);
        payload.setTipo_contenido(tipoContenido);
        return restTemplate.postForObject(mlServiceUrl, payload, MLServiceResult.class);
    }
}