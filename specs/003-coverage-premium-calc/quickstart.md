# Quickstart: Coberturas y Cálculo de Prima

**Feature**: 003-coverage-premium-calc

---

## Prerrequisitos

1. Angular SPA corriendo en `http://localhost:4200`
2. Backend `plataforma-danos-back` corriendo en `http://localhost:8080`
3. Backend `plataforma-core-ohs` corriendo en `http://localhost:8081`
4. Chrome instalado; Selenium Manager descarga el driver automáticamente
5. Suscriptor `SUB-001` y agente `AGT-123` existentes en Core (mismos de features 001/002)

---

## Ejecución

```bash
./gradlew test -Dcucumber.filter.tags="@premium-calculation" aggregate
```

Reporte HTML: `build/serenity-output/index.html`

---

## Flujo del escenario US1 — Prima calculada

### Setup (automático, @Before hooks)

1. `CatalogSetupHook` (order=1): verifica que `SUB-001` y `AGT-123` existen en Core API.
2. `CoveragePremiumSetupHook` (order=20): 
   - `POST http://localhost:8080/v1/folios` con `subscriberId=SUB-001`, `agentCode=AGT-123` → obtiene `folioNumber` + `version`
   - `PUT /v1/quotes/{folio}/general-info` con datos del asegurado → obtiene nueva `version`
   - `PUT /v1/quotes/{folio}/locations` con 2 ubicaciones (1 completa, 1 incompleta) → obtiene nueva `version`
   - `actor.remember("folioNumber", folioNumber)`

### Ejecución UI

1. **Dado** navega a `http://localhost:4200/cotizador/quotes/{folio}/technical-info`
   - Espera: `app-coverage-options-grid` visible
2. **Cuando** activa COV-FIRE en la ubicación 1 (tab activo por defecto) y guarda
   - Click en toggle de la tarjeta COV-FIRE → badge "Activa" aparece
   - Click en "Guardar coberturas" → petición `PUT /v1/quotes/{folio}/coverage-options`
3. **Y** navega a `http://localhost:4200/cotizador/quotes/{folio}/calculation` y ejecuta
   - Click en "Ejecutar cálculo" → petición `POST /v1/quotes/{folio}/calculate`
   - Espera: `.premium-summary` visible
4. **Entonces** prima neta es mayor a cero
   - Leer `.premium-summary__card--dark .premium-summary__card-value` → parsear MXN → > 0
5. **Y** prima comercial es mayor a cero
   - Leer `.premium-summary__card--brand .premium-summary__card-value` → parsear MXN → > 0

---

## Flujo del escenario US2 — No calculable

Mismos pasos 1–3 (setup + activar COV-FIRE + calcular).

4. **Entonces** ubicación incompleta aparece como "No calculable"
   - Verificar presencia de `app-badge` con texto "No calculable" en `li.premium-summary__location-item`

---

## Posibles fallos y diagnóstico

| Síntoma | Causa probable | Acción |
|---------|----------------|--------|
| `CatalogSetupHook` falla | SUB-001 o AGT-123 no existen | Verificar datos en Core API |
| `WaitUntil` timeout en coverage-options-grid | Folio inválido o Angular error | Revisar consola del navegador |
| `toggle` no clickable | `app-switch` requiere click interno | Cambiar target a `//app-switch//input` o `//app-switch//button` |
| Prima = 0 | COV-FIRE no activada o no guardada | Verificar badge "Activa" visible antes de guardar |
| "No calculable" no encontrado | Ubicación 2 tiene datos completos en el folio | Verificar body del PUT locations en el hook |
| `VERSION_CONFLICT 409` en el cálculo | La version del folio cambió (otro escenario anterior) | El hook idempotente crea folio fresco por escenario |
| Código de cobertura incorrecto | `COV-FIRE` no existe en catálogo del backend | `GET /v1/quotes/{folio}/coverage-options` para ver códigos reales |
