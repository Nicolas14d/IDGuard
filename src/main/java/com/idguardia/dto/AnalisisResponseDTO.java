package com.idguardia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

public class AnalisisDTOs {
    @Data
    public static class Request {
        private String usuarioId;
        @NotBlank private String contenidoRaw;
        @NotBlank private String tipoContenido;
        private String direccionIp;
    }

    @Data
    public static class MLServicePayload {
        private String contenido_raw;
        private String tipo_contenido;
    }

    @Data
    public static class MLServiceResult {
        private Double nivel_riesgo;
        private String nivel_amenaza;
        private Integer tiempo_procesamiento_ms;
        private String modelo_version;
        private List<ExplicacionItem> explicaciones;
    }

    @Data
    public static class ExplicacionItem {
        private String patron_detectado;
        private Double peso_impacto;
        private String descripcion_usuario;
    }

    @Data
    public static class Response {
        private String solicitudId;
        private String resultadoId;
        private Double nivelRiesgo;
        private String nivelAmenaza;
        private Integer latenciaMs;
        private String modeloVersion;
        private List<ExplicacionItem> explicaciones;
    }
}