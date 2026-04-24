# Quickstart: Feature 002 — Registro de Ubicaciones

## Prerrequisitos

1. **Backend** corriendo en `http://localhost:8080`
2. **Frontend** corriendo en `http://localhost:4200`
3. **Feature 001** implementada (CatalogSetupHook garantiza catálogos mínimos)

## Ejecución

```bash
./gradlew clean test -Dcucumber.filter.tags="@location-registration" aggregate
```

Reporte en `target/site/serenity/index.html`.

## Escenarios cubiertos

| Tag | Escenario | US |
|-----|-----------|----|
| `@location-registration` | Badge "Incompleta" y banner de alertas para ubicación sin datos | US1 + US2 |

## Setup automático (hooks)

El escenario se configura solo mediante dos hooks `@Before`:

| Orden | Hook | Qué hace |
|-------|------|----------|
| 1 | `CatalogSetupHook` | Verifica/crea suscriptor, agente, giro con clave incendio |
| 2 | `LocationScenarioSetupHook` | Crea folio con 2 ubicaciones vía API; `actor.remember("folioNumber", ...)` |

## Flujo de un escenario exitoso

```
@Before(order=1): CatalogSetupHook
  └─ GET /v1/subscribers, GET /v1/agents → verificar o crear catálogos

@Before(order=2): LocationScenarioSetupHook
  └─ POST /v1/folios → folioId, folioNumber
  └─ PUT /v1/folios/{folioId}/general-info
  └─ PUT /v1/quotes/{folioNumber}/locations (2 ubicaciones)
     ├─ Ubicación 1: CP 06600, giro OFICINAS/1110, garantía INCENDIO → COMPLETE
     └─ Ubicación 2: sin datos → INCOMPLETE

Step 1: actor navega a /cotizador/quotes/{folioNumber}/locations
Step 2: actor verifica badge de ubicación 2 = "Incompleta"
Step 3: actor verifica badge de ubicación 1 ≠ "Incompleta"
Step 4: actor verifica banner de alertas bloqueantes visible
Step 5: actor verifica que puede navegar a otra sección (botón "Siguiente →" habilitado)
```

## Troubleshooting

| Síntoma | Causa probable |
|---------|---------------|
| `IllegalStateException: Failed to create folio` | Backend no está corriendo en :8080 |
| Badge "Incompleta" no encontrado | Folio no creado correctamente; revisar logs del hook |
| Test falla en navegación | URL de locations no coincide con `LOCATIONS_URL_TEMPLATE` |
| Banner no visible | Backend no calculó alertas; verificar que CP `""` produce `INCOMPLETE` |
