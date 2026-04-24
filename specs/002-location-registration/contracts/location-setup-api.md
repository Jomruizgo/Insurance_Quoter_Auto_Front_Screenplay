# Contrato API: Setup de Ubicaciones para Feature 002

**Feature**: 002-location-registration  
**Date**: 2026-04-24  
**Purpose**: Endpoints usados en precondiciones via API (RestAssured). No son el SUT.

---

## 1. Crear Folio

**Method**: `POST`  
**URL**: `{restapi.base.url}/v1/folios`  
**Headers**: `Content-Type: application/json`

**Request Body**:
```json
{
  "subscriberId": "{Constants.TEST_SUBSCRIBER_ID}",
  "agentCode": "{Constants.TEST_AGENT_CODE}"
}
```

**Response 201**:
```json
{
  "id": 42,
  "folioNumber": "FOL-2026-00042",
  "status": "DRAFT",
  "version": 0
}
```

**Campos extraídos**:
- `id` → `actor.remember("folioId", ...)`
- `folioNumber` → `actor.remember("folioNumber", ...)`
- `version` → `actor.remember("folioVersion", ...)`

---

## 2. Completar Datos Generales

**Method**: `PUT`  
**URL**: `{restapi.base.url}/v1/folios/{folioId}/general-info`  
**Headers**: `Content-Type: application/json`

**Request Body**:
```json
{
  "razonSocial": "{Constants.TEST_RAZON_SOCIAL}",
  "rfc": "{Constants.TEST_RFC}",
  "email": "{Constants.TEST_EMAIL}",
  "phone": "{Constants.TEST_PHONE}",
  "riskClassification": "BAJO",
  "businessType": "COMERCIAL",
  "version": 0
}
```

**Response 200**: OK con versión actualizada.

**Nota**: `version` en el body se actualiza en la respuesta. Extraer `version` actualizado si necesario para la siguiente llamada.

---

## 3. Establecer Lista de Ubicaciones

**Method**: `PUT`  
**URL**: `{restapi.base.url}/v1/quotes/{folioNumber}/locations`  
**Headers**: `Content-Type: application/json`

**Request Body**:
```json
{
  "locations": [
    {
      "index": 1,
      "locationName": "Ubicación 1",
      "address": "Reforma 222",
      "zipCode": "06600",
      "constructionType": "MASONRY",
      "level": 1,
      "constructionYear": 2010,
      "businessLine": {
        "code": "OFICINAS",
        "fireKey": "1110"
      },
      "guarantees": [
        {
          "code": "INCENDIO",
          "insuredValue": 1000000
        }
      ]
    },
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
  ],
  "version": 0
}
```

**Response 200**:
```json
{
  "folioNumber": "FOL-2026-00042",
  "locations": [...],
  "version": 1
}
```

La segunda ubicación tendrá `validationStatus: "INCOMPLETE"` y `blockingAlerts` con al menos un ítem.  
La primera ubicación tendrá `validationStatus: "COMPLETE"`.

---

## Códigos de error

| HTTP | Causa |
|------|-------|
| 400 | Request inválido (campos obligatorios faltantes) |
| 404 | Folio no encontrado |
| 409 | Conflicto de versión (optimistic lock) |

**Regla de automatización**: Si alguna llamada retorna status != 2xx, el hook lanza `IllegalStateException` y el escenario falla con mensaje descriptivo.

---

## Constantes requeridas en `Constants.java`

| Constante | Valor de ejemplo | Descripción |
|-----------|-----------------|-------------|
| `TEST_SUBSCRIBER_ID` | `"SUB-001"` | Ya existe de feature 001 |
| `TEST_AGENT_CODE` | `"AGT-001"` | Ya existe de feature 001 |
| `TEST_RAZON_SOCIAL` | `"Empresa Test SA"` | Ya existe de feature 001 |
| `TEST_RFC` | `"ETST990101XXX"` | Ya existe de feature 001 |
| `TEST_EMAIL` | `"test@sofka.com"` | Ya existe de feature 001 |
| `TEST_PHONE` | `"5512345678"` | Ya existe de feature 001 |
| `TEST_LOCATION_ZIP_CODE` | `"06600"` | **Nuevo** — CP de la ubicación completa |
| `TEST_LOCATION_BL_CODE` | `"OFICINAS"` | **Nuevo** — código de giro |
| `TEST_LOCATION_BL_FIRE_KEY` | `"1110"` | **Nuevo** — clave incendio |
| `TEST_LOCATION_GUARANTEE_CODE` | `"INCENDIO"` | **Nuevo** — código de garantía |
| `TEST_LOCATION_INSURED_VALUE` | `1_000_000` | **Nuevo** — suma asegurada (int/long) |
| `LOCATIONS_URL_TEMPLATE` | `"/cotizador/quotes/%s/locations"` | **Nuevo** — template de URL |
