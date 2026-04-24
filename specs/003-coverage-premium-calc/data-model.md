# Data Model: Coberturas y Cálculo de Prima

**Feature**: 003-coverage-premium-calc  
**Date**: 2026-04-24

---

## Entidades observables en UI

### CoverageOption

Estado de una cobertura para una ubicación en la pantalla `/technical-info`.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `code` | String | Identificador único de la cobertura (ej. `COV-FIRE`) |
| `description` | String | Nombre legible (ej. "Incendio") |
| `selected` | Boolean | `true` si el agente la activó |
| `deductiblePercentage` | Number | Porcentaje de deducible (default: proporcionado por backend) |
| `coinsurancePercentage` | Number | Porcentaje de coaseguro |

**Observable en UI**: badge `Activa` visible en la tarjeta cuando `selected = true`.

---

### CalculationResult

Resultado devuelto por `POST /v1/quotes/{folio}/calculate` y mostrado en `/calculation`.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `netPremium` | Number | Prima neta total del folio; debe ser > 0 si hay al menos una ubicación calculable |
| `commercialPremium` | Number | Prima comercial total; >= netPremium |
| `locationPremiums` | List\<LocationPremium\> | Prima por ubicación |

**Observable en UI**: 
- `.premium-summary__card--dark .premium-summary__card-value` → prima neta formateada en MXN
- `.premium-summary__card--brand .premium-summary__card-value` → prima comercial formateada en MXN

---

### LocationPremium

Prima calculada o estado "no calculable" para una ubicación individual dentro del desglose.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `index` | Integer | Número de ubicación (1-based) |
| `locationName` | String | Nombre de la ubicación |
| `calculable` | Boolean | `false` si falta CP, giro o garantías |
| `netPremium` | Number | Prima neta de esta ubicación; 0 si `calculable = false` |
| `commercialPremium` | Number | Prima comercial de esta ubicación; 0 si `calculable = false` |

**Observable en UI**:
- Si `calculable = true`: valor monetario en `li.premium-summary__location-item`
- Si `calculable = false`: badge `app-badge[variant="warn"]` con texto "No calculable"

---

## Datos de prueba (Constants.java)

| Constante | Valor | Uso |
|-----------|-------|-----|
| `COVERAGE_CODE_FIRE` | `"COV-FIRE"` | Código de la cobertura de incendio a activar |
| `TECHNICAL_INFO_URL_TEMPLATE` | `"/cotizador/quotes/%s/technical-info"` | URL de la pantalla de coberturas |
| `CALCULATION_URL_TEMPLATE` | `"/cotizador/quotes/%s/calculation"` | URL de la pantalla de cálculo |

Los datos de folio, ubicaciones y catálogos se heredan de las constantes definidas en features 001/002 (`TEST_SUBSCRIBER_ID`, `TEST_AGENT_CODE`, `TEST_LOCATION_ZIP_CODE`, etc.).

---

## Flujo de estado

```
[Folio creado vía API]
        ↓
[2 ubicaciones: 1 completa + 1 incompleta vía API]
        ↓
[Pantalla /technical-info — coberturas por ubicación]
        ↓ (agente activa COV-FIRE en ubicación 1)
[CoverageOption: code=COV-FIRE, selected=true]
        ↓ (agente guarda)
[API: PUT /v1/quotes/{folio}/coverage-options persiste estado]
        ↓ (agente navega a /calculation y ejecuta cálculo)
[API: POST /v1/quotes/{folio}/calculate]
        ↓
[CalculationResult: netPremium > 0, commercialPremium > 0]
   ├── LocationPremium(index=1): calculable=true, netPremium > 0
   └── LocationPremium(index=2): calculable=false → badge "No calculable"
```
