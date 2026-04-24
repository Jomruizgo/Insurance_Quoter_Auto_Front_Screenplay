# Research: Registro de Ubicaciones

**Feature**: 002-location-registration  
**Date**: 2026-04-24

## Decisiones

### D001 — URL de la pantalla de Ubicaciones

**Decision**: `/cotizador/quotes/{folioNumber}/locations`  
**Rationale**: Leído directamente de `cotizador.routes.ts`:
```typescript
path: 'quotes/:folioNumber',
children: [
  { path: 'locations', component: LocationsPageComponent },
]
```
El parámetro de ruta es `:folioNumber` (no `:folio`), y el componente extrae
`route.snapshot.params['folioNumber']`.  
**Alternatives considered**: N/A — fuente autoritativa es el código del frontend.

---

### D002 — Selector CSS del badge de estado por ubicación

**Decision**: Buscar `<span class="badge badge--incomplete">` o `<span class="badge badge--complete">` dentro de la fila `<tr>` que corresponde al índice.

Selectores derivados de `locations-table.component.ts`:
```typescript
validationLabel(status): string {
  return status === 'COMPLETE' ? 'Completa' : 'Incompleta';
}
badgeClass(status): string {
  return status === 'COMPLETE' ? 'badge--complete' : 'badge--incomplete';
}
```
El template renderiza:
```html
<span class="badge badge--incomplete">Incompleta</span>
<!-- o -->
<span class="badge badge--complete">Completa</span>
```

**XPath para badge de ubicación por índice** (ej. índice 2):
```
//table[contains(@class,'locations-table')]
  //tbody/tr[td[2][normalize-space(text())='2']]
  //span[contains(@class,'badge')]
```
La columna `td[2]` corresponde al número de índice (la primera columna es el checkbox).

**Rationale**: No hay IDs únicos en las filas; el índice numérico en la columna `#` es el discriminador estable.  
**Alternatives considered**: Seleccionar por `loc.locationName` (menos estable, puede cambiar); por posición de fila (frágil si el orden cambia).

---

### D003 — Selector CSS del banner de alertas bloqueantes

**Decision**: `div.alert-banner.alert-banner--warn`

Leído de `locations-alert-banner.component.ts`:
```html
<div class="alert-banner alert-banner--warn">
  <span class="alert-banner__text">N ubicación(es) con alertas bloqueantes...</span>
  <button class="alert-banner__btn">Ver detalles</button>
</div>
```
El banner se renderiza condicionalmente cuando `summary.incompleteLocations > 0`.

**Rationale**: Clase CSS semántica y estable.  
**Alternatives considered**: XPath por texto del span (frágil ante cambios de texto).

---

### D004 — Setup API de precondiciones: endpoints y secuencia

**Decision**: Secuencia de 3 llamadas:

1. `POST /v1/folios` → JSON `{ subscriberId, agentCode }` → respuesta incluye `id` (numérico) y `folioNumber` (string `"FOL-YYYY-NNNNN"`)
2. `PUT /v1/folios/{id}/general-info` → JSON con datos del asegurado (razón social, RFC, email, teléfono, clasificación, tipo negocio)
3. `PUT /v1/quotes/{folioNumber}/locations` → JSON `{ locations: [...], version: 0 }` — reemplaza la lista completa con 2 ubicaciones

La llamada 3 usa el endpoint que expone el `LocationService` del frontend (`PUT /v1/quotes/{folio}/locations`), que es equivalente a `reemplazarLista`. No se requiere el endpoint `PUT /v1/folios/{id}/layout` si se envía la lista de ubicaciones directamente con todos sus datos.

**Rationale**: El `LocationService` Angular confirma que `PUT /v1/quotes/{folio}/locations` acepta una lista completa de ubicaciones con todos sus campos. Esto es más directo que llamar layout + endpoints individuales.

**Alternatives considered**: `PUT /v1/folios/{id}/layout` para definir el número de ubicaciones y luego `PATCH /v1/quotes/{folio}/locations/{index}` por cada una — más llamadas, misma semántica final.

---

### D005 — Datos de la ubicación completa

**Decision**:
```json
{
  "index": 1,
  "locationName": "Ubicación 1",
  "address": "Reforma 222",
  "zipCode": "06600",
  "constructionType": "MASONRY",
  "level": 1,
  "constructionYear": 2010,
  "businessLine": { "code": "OFICINAS", "fireKey": "1110" },
  "guarantees": [{ "code": "INCENDIO", "insuredValue": 1000000 }]
}
```
`validationStatus` es calculado por el backend; no se envía en el request.

**Rationale**: `zipCode: "06600"` es el CP estándar de pruebas (ya usado en `CatalogSetupHook`). El hook garantiza que el giro con clave incendio existe en catálogos.  
**Alternatives considered**: N/A.

---

### D006 — Datos de la ubicación incompleta

**Decision**:
```json
{
  "index": 2,
  "locationName": "Ubicación 2",
  "address": "",
  "zipCode": "",
  "constructionType": "MASONRY",
  "level": 1,
  "constructionYear": 2000,
  "businessLine": null,
  "guarantees": []
}
```

**Rationale**: Sin CP, sin giro (businessLine null) y sin garantías — los 3 factores que producen `validationStatus: "INCOMPLETE"` y alertas bloqueantes según FR-003.  
**Alternatives considered**: N/A.

---

### D007 — Estrategia de Task API vs Hook

**Decision**: Crear `SetupLocationScenario` como clase `Task` (implements `Performable`), invocada desde un `@Before(order=2)` en `LocationScenarioSetupHook`. El folioNumber se propaga con `actor.remember("folioNumber", ...)`.

**Rationale**: Consistencia con el patrón del proyecto (principio I). `CatalogSetupHook` ya es `@Before(order=1)`; el setup de escenario es `order=2` para ejecutar después de los catálogos.  
**Alternatives considered**: Lógica en el hook directamente (viola Separation of Concerns).

---

### D008 — Question para verificar badge de ubicación

**Decision**: `LocationBadgeStatus` implementa `Question<String>` y acepta el índice de ubicación como parámetro estático. Retorna el texto del badge (`"Incompleta"` o `"Completa"`).

```java
LocationBadgeStatus.forLocationIndex(2)
```

**Rationale**: Reutilizable para US1 (índice 2, espera "Incompleta") y US2 (índice 1, espera "Completa") sin duplicar código.  
**Alternatives considered**: `Question<Boolean>` separadas por estado (más clases, menos reutilizable).

---

### D009 — Question para verificar banner de alertas

**Decision**: `BlockingAlertsBanner` implementa `Question<Boolean>`. Retorna `true` si el elemento `div.alert-banner.alert-banner--warn` está visible en el DOM.

**Rationale**: El banner solo aparece cuando `summary.incompleteLocations > 0`. Verificar presencia del elemento es suficiente (no necesita contar alertas individuales para US1/US2).  
**Alternatives considered**: Leer el texto del banner (más frágil ante cambios de copy).
