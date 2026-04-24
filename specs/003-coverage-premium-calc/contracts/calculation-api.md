# Contrato API: Cálculo de Prima

**Feature**: 003-coverage-premium-calc  
**Base URL**: `http://localhost:8080`

---

## POST /v1/quotes/{folioNumber}/calculate

Dispara el motor de tarifación para todas las ubicaciones del folio.  
Llamado al hacer clic en "Ejecutar cálculo" en la pantalla `/calculation`.

**Request**:
```json
{
  "version": 3
}
```

**Response 200**:
```json
{
  "folioNumber": "FOL-2026-001",
  "netPremium": 12500.00,
  "commercialPremium": 14375.00,
  "locationPremiums": [
    {
      "index": 1,
      "locationName": "Ubicación 1",
      "calculable": true,
      "netPremium": 12500.00,
      "commercialPremium": 14375.00
    },
    {
      "index": 2,
      "locationName": "Ubicación 2",
      "calculable": false,
      "netPremium": 0,
      "commercialPremium": 0,
      "alerts": ["Código postal requerido", "Giro requerido", "Garantías requeridas"]
    }
  ]
}
```

**Reglas de negocio**:
- `netPremium` del folio = suma de `netPremium` de ubicaciones calculables.
- Si una ubicación tiene `calculable: false`, no aporta prima y muestra "No calculable" en UI.
- `commercialPremium >= netPremium` siempre.

**Response 409** (version conflict):
```json
{
  "error": "VERSION_CONFLICT",
  "message": "Expected version 3 but found 4"
}
```

**Response 422** (sin coberturas activas):
```json
{
  "error": "VALIDATION_ERROR",
  "message": "No active coverages found for calculable locations"
}
```

---

## GET /v1/quotes/{folioNumber}/calculation-result

Obtiene el último resultado de cálculo (si ya fue ejecutado).  
Usado por el frontend para mostrar el desglose sin volver a calcular.

**Response 200**: mismo schema que POST /calculate response 200.

**Response 404** (cálculo no ejecutado):
```json
{
  "error": "NOT_FOUND",
  "message": "No calculation result found for folio FOL-2026-001"
}
```

---

## Notas de implementación

- El setup API (hook `@Before`) NO llama a este endpoint; el cálculo es la acción UI bajo prueba.
- La `version` del request debe obtenerse del resultado del último PUT exitoso (general-info o locations).
- La `version` retornada en la respuesta NO necesita usarse posteriormente en este flujo (es el último paso del wizard).
- Los valores de `netPremium` y `commercialPremium` son numéricos. La UI los formatea como MXN (`$12,500.00`). Las Questions deben parsear el texto formateado y comparar > 0.
