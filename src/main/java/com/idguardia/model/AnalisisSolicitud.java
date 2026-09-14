package com.idguardia.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "analisis_solicitudes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AnalisisSolicitud {
    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "contenido_raw", nullable = false, columnDefinition = "TEXT")
    private String contenidoRaw;

    @Column(name = "tipo_contenido", nullable = false)
    private String tipoContenido;

    @Column(name = "direccion_ip", nullable = false, length = 45)
    private String direccionIp;

    @Column(name = "fecha_analisis", nullable = false, updatable = false)
    private OffsetDateTime fechaAnalisis;

    @OneToOne(mappedBy = "solicitud", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ResultadoAnalisis resultado;

    @PrePersist
    protected void onCreate() {
        if (fechaAnalisis == null) fechaAnalisis = OffsetDateTime.now();
    }
}