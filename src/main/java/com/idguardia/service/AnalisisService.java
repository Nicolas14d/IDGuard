package com.idguardia.service;

import com.idguardia.dto.AnalisisDTOs.*;
import com.idguardia.model.*;
import com.idguardia.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnalisisService {
    private final AnalisisSolicitudRepository solicitudRepo;
    private final UsuarioRepository usuarioRepo;
    private final MLServiceClient mlClient;

    public AnalisisService(AnalisisSolicitudRepository solicitudRepo, UsuarioRepository usuarioRepo, MLServiceClient mlClient) {
        this.solicitudRepo = solicitudRepo;
        this.usuarioRepo = usuarioRepo;
        this.mlClient = mlClient;
    }

    @Transactional
    public Response procesarAnalisis(Request req, String clientIp) {
        MLServiceResult mlRes = mlClient.inferir(req.getContenidoRaw(), req.getTipoContenido());

        Usuario usuario = null;
        if (req.getUsuarioId() != null) {
            usuario = usuarioRepo.findById(req.getUsuarioId()).orElse(null);
        }

        String solId = UUID.randomUUID().toString();
        AnalisisSolicitud solicitud = AnalisisSolicitud.builder()
                .id(solId)
                .usuario(usuario)
                .contenidoRaw(req.getContenidoRaw())
                .tipoContenido(req.getTipoContenido())
                .direccionIp(clientIp != null ? clientIp : "127.0.0.1")
                .build();

        String resId = UUID.randomUUID().toString();
        ResultadoAnalisis resultado = ResultadoAnalisis.builder()
                .id(resId)
                .solicitud(solicitud)
                .nivelRiesgo(mlRes.getNivel_riesgo())
                .nivelAmenaza(mlRes.getNivel_amenaza())
                .tiempoProcesamientoMs(mlRes.getTiempo_procesamiento_ms())
                .modeloVersion(mlRes.getModelo_version())
                .build();

        List<String> reasonsList = new ArrayList<>();
        if (mlRes.getExplicaciones() != null) {
            for (ExplicacionItem exp : mlRes.getExplicaciones()) {
                ExplicacionRiesgo entidadExp = ExplicacionRiesgo.builder()
                        .resultado(resultado)
                        .patronDetectado(exp.getPatron_detectado())
                        .pesoImpacto(exp.getPeso_impacto())
                        .descripcionUsuario(exp.getDescripcion_usuario())
                        .build();
                resultado.getExplicaciones().add(entidadExp);
                reasonsList.add(exp.getDescripcion_usuario());
            }
        }

        solicitud.setResultado(resultado);
        solicitudRepo.save(solicitud);

        // Mapeo exacto con los nombres que espera el frontend (risk, score, msg, reasons)
        Response resp = new Response();
        resp.setSolicitudId(solId);
        resp.setResultadoId(resId);
        
        int scorePorcentaje = (int) Math.round(mlRes.getNivel_riesgo() * 100);
        resp.setScore(scorePorcentaje);
        resp.setLatency(Double.parseDouble(String.format("%.2f", mlRes.getTiempo_procesamiento_ms() / 1000.0)));
        resp.setReasons(reasonsList);

        if ("CRITICO".equals(mlRes.getNivel_amenaza())) {
            resp.setRisk("bad");
            resp.setMsg("🚨 RIESGO ALTO: posible suplantación de identidad. No ingreses datos ni pagues.");
        } else if ("SOSPECHOSO".equals(mlRes.getNivel_amenaza())) {
            resp.setRisk("warn");
            resp.setMsg("⚠️ RIESGO MEDIO: contenido sospechoso. Verifica la fuente oficial antes de continuar.");
        } else {
            resp.setRisk("good");
            resp.setMsg("✅ BAJO RIESGO: no se detectaron señales claras de estafa.");
        }

        return resp;
    }
}