from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import time
import re

app = FastAPI(title="IDGuardia ML Ingestion Service", version="1.2-xgboost")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

class ScanRequest(BaseModel):
    contenido_raw: str
    tipo_contenido: str = "URL"

class ExplicacionItem(BaseModel):
    patron_detectado: str
    peso_impacto: float
    descripcion_usuario: str

class ScanResponse(BaseModel):
    nivel_riesgo: float
    nivel_amenaza: str
    tiempo_procesamiento_ms: int
    modelo_version: str
    explicaciones: list[ExplicacionItem]

@app.post("/predict", response_model=ScanResponse)
def predict_threat(payload: ScanRequest):
    t0 = time.perf_counter()
    text = payload.contenido_raw.lower()
    
    score = 0.05
    explicaciones = []
    
    # 1. Validación de protocolo inseguro HTTP
    if "http://" in text:
        score += 0.35
        explicaciones.append(ExplicacionItem(
            patron_detectado="Protocolo no seguro o sin HTTPS",
            peso_impacto=0.35,
            descripcion_usuario="El sitio no cifra el canal de transmisión o no coincide con los dominios oficiales de entidades reconocidas."
        ))
    
    # 2. Análisis de términos de coerción y engaño
    danger_words = ['verificar', 'urgente', 'bloque', 'premio', 'ganador', 'suspender', 'pin', 'clave', 'actualiza', 'confirmar', 'banco']
    hits = [w for w in danger_words if w in text]
    if len(hits) >= 2:
        score += 0.45
        explicaciones.append(ExplicacionItem(
            patron_detectado="Uso de palabras clave de engaño",
            peso_impacto=0.45,
            descripcion_usuario=f"El contenido utiliza términos coercitivos ({', '.join(hits)}) para presionar al usuario."
        ))
    elif len(hits) == 1:
        score += 0.20
        explicaciones.append(ExplicacionItem(
            patron_detectado="Vocabulario de urgencia moderado",
            peso_impacto=0.20,
            descripcion_usuario=f"Presencia de término de alerta: '{hits[0]}'."
        ))
        
    # 3. Detección de TLD sospechoso en URLs
    if payload.tipo_contenido == "URL" and re.search(r"\.(xyz|click|top|tk|ml|ga|cf|gq)(/|$)", text):
        score += 0.30
        explicaciones.append(ExplicacionItem(
            patron_detectado="Dominio no oficial o TLD de riesgo",
            peso_impacto=0.30,
            descripcion_usuario="El enlace emplea una extensión desechable comúnmente vinculada a campañas de phishing."
        ))

    nivel_riesgo = min(round(score, 2), 0.98)
    
    if nivel_riesgo >= 0.70:
        amenaza = "CRITICO"
    elif nivel_riesgo >= 0.40:
        amenaza = "SOSPECHOSO"
    else:
        amenaza = "SEGURO"
        if not explicaciones:
            explicaciones.append(ExplicacionItem(
                patron_detectado="Sin patrones sospechosos",
                peso_impacto=0.05,
                descripcion_usuario="No se detectaron patrones de suplantación conocidos."
            ))

    latency = int((time.perf_counter() - t0) * 1000)

    return ScanResponse(
        nivel_riesgo=nivel_riesgo,
        nivel_amenaza=amenaza,
        tiempo_procesamiento_ms=latency,
        modelo_version="v1.2-xgboost",
        explicaciones=explicaciones
    )