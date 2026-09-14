package com.idguardia.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resultados_analisis")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResultadoAnalisis {
    @Id
    @Column(length = 36)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", nullable = false, unique = true)
    private AnalisisSolicitud solicitud;

    @Column(name = "nivel_riesgo", nullable = false)
    private Double nivelRiesgo;

    @Column(name = "nivel_amenaza", nullable = false)
    private String nivelAmenaza;

    @Column(name = "tiempo_procesamiento_ms", nullable = false)
    private Integer tiempoProcesamientoMs;

    @Column(name = "modelo_version", nullable = false, length = 50)
    private String modeloVersion;

    @OneToMany(mappedBy = "resultado", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExplicacionRiesgo> explicaciones = new ArrayList<>();
}