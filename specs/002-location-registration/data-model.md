# Data Model: Registro de Ubicaciones

**Feature**: 002-location-registration  
**Date**: 2026-04-24

## Entidades del dominio

### Folio

Cotización identificada por un número de folio.

| Campo | Tipo | Notas |
|-------|------|-------|
| `id` | `Long` | ID numérico del backend; usado en endpoints `PUT /v1/folios/{id}/...` |
| `folioNumber` | `String` | Formato `"FOL-YYYY-NNNNN"`; usado en URLs del frontend `/cotizador/quotes/{folioNumber}/...` y en endpoints `PUT /v1/quotes/{folioNumber}/...` |
| `version` | `int` | Optimistic locking; empieza en 0; se propaga entre llamadas |

**Actor remember keys**: `"folioNumber"`, `"folioId"`, `"folioVersion"`

---

### Location

Ubicación de riesgo dentro de un folio.

| Campo | Tipo | Regla de validación |
|-------|------|---------------------|
| `index` | `int` | 1-based; identifica la ubicación dentro del folio |
| `locationName` | `String` | Nombre de la ubicación |
| `address` | `String` | Dirección física |
| `zipCode` | `String` | CP; vacío → alerta `MISSING_ZIP_CODE` |
| `constructionType` | `ConstructionType` | MASONRY, STEEL, CONCRETE, WOOD, MIXED |
| `level` | `int` | Nivel del edificio |
| `constructionYear` | `int` | Año de construcción |
| `businessLine` | `BusinessLine \| null` | Giro del negocio; null → alerta `MISSING_FIRE_KEY` |
| `guarantees` | `List<Guarantee>` | Garantías tarifables; vacío → alerta de garantías faltantes |
| `validationStatus` | `ValidationStatus` | `COMPLETE` o `INCOMPLETE` (calculado por backend) |
| `blockingAlerts` | `List<BlockingAlert>` | Lista de alertas que impiden tarifación |

---

### ValidationStatus

| Valor | Condición |
|-------|-----------|
| `COMPLETE` | CP válido + giro con clave incendio + al menos una garantía tarifable |
| `INCOMPLETE` | Falta alguno de los anteriores |

---

### BlockingAlert

| Campo | Tipo |
|-------|------|
| `code` | `String` — ej. `"MISSING_ZIP_CODE"`, `"MISSING_FIRE_KEY"`, `"MISSING_GUARANTEES"` |
| `message` | `String` — texto legible para el usuario |

---

### BusinessLine

| Campo | Tipo |
|-------|------|
| `code` | `String` — ej. `"OFICINAS"` |
| `fireKey` | `String` — ej. `"1110"` |

---

### Guarantee

| Campo | Tipo |
|-------|------|
| `code` | `String` — ej. `"INCENDIO"` |
| `insuredValue` | `number` — suma asegurada |

---

### LocationsSummary

Resumen de estado de todas las ubicaciones del folio. Devuelto por `GET /v1/quotes/{folio}/locations/summary`.

| Campo | Tipo |
|-------|------|
| `folioNumber` | `String` |
| `totalLocations` | `int` |
| `completeLocations` | `int` |
| `incompleteLocations` | `int` |
| `locations` | `List<LocationSummaryItem>` |

El frontend muestra el banner de alertas cuando `incompleteLocations > 0`.

---

## Relaciones

```
Folio (1) ──── (*) Location
Location (1) ──── (*) BlockingAlert
Location (1) ──── (*) Guarantee
Location (0..1) ── BusinessLine
```

---

## Estado de las ubicaciones en el escenario de prueba

| Índice | locationName | zipCode | businessLine | guarantees | validationStatus esperado |
|--------|-------------|---------|-------------|------------|--------------------------|
| 1 | "Ubicación 1" | "06600" | OFICINAS / 1110 | INCENDIO / $1M | `COMPLETE` |
| 2 | "Ubicación 2" | "" | null | [] | `INCOMPLETE` |

---

## Mapeo a clases de automatización

| Entidad de dominio | Clase de automatización |
|--------------------|------------------------|
| `Folio.folioNumber` | `actor.remember("folioNumber", ...)` |
| `Folio.id` | `actor.remember("folioId", ...)` |
| `Folio.version` | `actor.remember("folioVersion", ...)` |
| `Location.validationStatus` | `LocationBadgeStatus.forLocationIndex(n)` |
| `LocationsSummary.incompleteLocations > 0` | `BlockingAlertsBanner.isVisible()` |
| `Location.blockingAlerts` | No verificado directamente; solo presencia del banner |
