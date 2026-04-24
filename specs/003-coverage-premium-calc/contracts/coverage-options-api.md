# Contrato API: Coverage Options

**Feature**: 003-coverage-premium-calc  
**Base URL**: `http://localhost:8080`

---

## GET /v1/quotes/{folioNumber}/coverage-options

Obtiene las opciones de cobertura disponibles para el folio activo.  
Usado por el frontend para mostrar las tarjetas en `/technical-info`.

**Request**:
```
GET /v1/quotes/{folioNumber}/coverage-options
Content-Type: application/json
```

**Response 200**:
```json
{
  "folioNumber": "FOL-2026-001",
  "version": 2,
  "coverageOptions": [
    {
      "code": "COV-FIRE",
      "description": "Incendio",
      "selected": false,
      "deductiblePercentage": 10.0,
      "coinsurancePercentage": 0.0
    }
  ]
}
```

---

## PUT /v1/quotes/{folioNumber}/coverage-options

Persiste el estado de coberturas seleccionadas por el agente.  
Llamado al hacer clic en "Guardar coberturas".

**Request**:
```json
{
  "coverageOptions": [
    {
      "code": "COV-FIRE",
      "selected": true,
      "deductiblePercentage": 10.0,
      "coinsurancePercentage": 0.0
    }
  ],
  "version": 2
}
```

**Response 200**:
```json
{
  "folioNumber": "FOL-2026-001",
  "version": 3,
  "coverageOptions": [
    {
      "code": "COV-FIRE",
      "description": "Incendio",
      "selected": true,
      "deductiblePercentage": 10.0,
      "coinsurancePercentage": 0.0
    }
  ]
}
```

**Response 409** (version conflict):
```json
{
  "error": "VERSION_CONFLICT",
  "message": "Expected version 2 but found 3"
}
```

---

## Notas de implementación para Setup Task

- Este endpoint **NO se llama en el hook de setup** (ver research.md — Decisión 6).
- La activación de COV-FIRE es la acción UI bajo prueba; el hook solo configura folio y ubicaciones.
- Si en el futuro se necesita preactivar coberturas vía API, el body usa el mismo esquema de `coverageOptions` con `selected: true`.
