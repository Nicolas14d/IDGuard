package com.idguardia.controller;

import com.idguardia.dto.AnalisisDTOs.*;
import com.idguardia.model.AnalisisSolicitud;
import com.idguardia.repository.AnalisisSolicitudRepository;
import com.idguardia.service.AnalisisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class AnalisisController {

    private final AnalisisService analisisService;
    private final AnalisisSolicitudRepository solicitudRepo;

    public AnalisisController(AnalisisService analisisService, AnalisisSolicitudRepository solicitudRepo) {
        this.analisisService = analisisService;
        this.solicitudRepo = solicitudRepo;
    }

    @PostMapping("/analyze")
    public ResponseEntity<Response> analizar(
            @Valid @RequestBody Request request,
            HttpServletRequest servletRequest) {
        String ip = servletRequest.getRemoteAddr();
        return ResponseEntity.ok(analisisService.procesarAnalisis(request, ip));
    }

    @GetMapping("/logs")
    public ResponseEntity<List<AnalisisSolicitud>> obtenerLogs() {
        return ResponseEntity.ok(solicitudRepo.findAllWithDetails());
    }
}