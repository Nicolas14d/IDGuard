package com.idguardia.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "explicaciones_riesgo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExplicacionRiesgo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resultado_id", nullable = false)
    private ResultadoAnalisis resultado;

    @Column(name = "patron_detectado", nullable = false, length = 150)
    private String patronDetectado;

    @Column(name = "peso_impacto", nullable = false)
    private Double pesoImpacto;

    @Column(name = "descripcion_usuario", nullable = false, length = 255)
    private String descripcionUsuario;
}