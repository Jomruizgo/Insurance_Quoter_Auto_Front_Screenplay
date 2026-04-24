# Feature Specification: Registro de Ubicaciones

**Feature Branch**: `002-location-registration`
**Created**: 2026-04-24
**Status**: Draft
**Input**: User description: "Automatización del registro de ubicaciones en la SPA cotizador-danos-web"

## User Scenarios & Testing *(mandatory)*

### User Story 1 — Verificación de badge "Incompleta" en ubicación sin datos (Priority: P1)

El agente puede ver que una ubicación registrada sin código postal, giro ni garantías muestra el badge "Incompleta" y un banner de alertas bloqueantes en la pantalla de Ubicaciones, sin que el folio quede bloqueado para su navegación.

**Why this priority**: Garantiza que el sistema comunica claramente el estado incompleto de una ubicación y sus alertas, lo cual es crítico para que el agente tome acción correctiva sin perder el progreso del folio.

**Independent Test**: Con un folio previamente creado vía API (datos generales + layout con 2 ubicaciones definidas), abrir la pantalla de Ubicaciones y verificar que la segunda ubicación (solo nombre, sin CP/giro/garantías) muestra badge "Incompleta" y banner de alertas bloqueantes.

**Acceptance Scenarios**:

1. **Dado** que existe un folio con layout de 2 ubicaciones creado vía API, **Cuando** el agente abre la pantalla de Ubicaciones y navega a la segunda ubicación (sin CP, giro ni garantías), **Entonces** la UI muestra badge "Incompleta" para esa ubicación.
2. **Dado** la segunda ubicación sin datos completos, **Cuando** el agente la visualiza, **Entonces** la UI muestra un banner o sección de alertas bloqueantes asociadas a la ubicación.
3. **Dado** las alertas bloqueantes en la segunda ubicación, **Cuando** el agente intenta navegar a otra sección del folio, **Entonces** el folio no está bloqueado — la navegación entre pasos del wizard es posible.

---

### User Story 2 — Verificación de ubicación completa con CP válido, giro y garantía (Priority: P2)

El agente puede confirmar que una ubicación registrada con todos sus datos obligatorios (CP válido, giro con clave incendio y al menos una garantía tarifable) no muestra alertas bloqueantes y su estado refleja los datos correctamente.

**Why this priority**: Valida el flujo positivo — sin este escenario no se puede distinguir si el badge "Incompleta" se muestra correctamente o si siempre aparece.

**Independent Test**: Con la primera ubicación (CP válido, giro con clave incendio, garantía tarifable) definida vía API, abrir la pantalla de Ubicaciones y verificar que no muestra badge "Incompleta" ni alertas bloqueantes.

**Acceptance Scenarios**:

1. **Dado** que la primera ubicación tiene CP válido, giro con clave incendio y garantía tarifable, **Cuando** el agente la visualiza en la pantalla de Ubicaciones, **Entonces** la UI no muestra badge "Incompleta" para esa ubicación.
2. **Dado** la primera ubicación completa, **Cuando** el agente la visualiza, **Entonces** no aparece banner de alertas bloqueantes asociado a esa ubicación.

---

### Edge Cases

- ¿Qué ocurre si el folio tiene solo una ubicación incompleta? El folio sigue navegable; las alertas son por ubicación, no bloquean el folio completo.
- ¿Qué pasa si el CP existe pero el giro no tiene clave incendio asociada? La ubicación se considera incompleta para fines de tarifación.
- ¿Qué sucede si no hay garantías registradas? La UI debe mostrar alerta bloqueante por garantías faltantes.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El escenario de automatización DEBE configurar el folio completo (datos generales + layout con 2 ubicaciones) exclusivamente vía API antes de abrir el navegador, sin interacción UI para estas precondiciones.
- **FR-002**: La pantalla de Ubicaciones DEBE mostrar badge de estado por cada ubicación registrada.
- **FR-003**: Una ubicación sin código postal, sin giro con clave incendio y sin garantías tarifables DEBE mostrar badge "Incompleta".
- **FR-004**: La UI DEBE mostrar un banner o sección de alertas bloqueantes para la ubicación incompleta.
- **FR-005**: Las alertas bloqueantes de una ubicación incompleta NO DEBEN impedir la navegación entre pasos del wizard del folio.
- **FR-006**: Una ubicación con CP válido, giro con clave incendio y al menos una garantía tarifable NO DEBE mostrar badge "Incompleta" ni alertas bloqueantes.
- **FR-007**: El número de folio obtenido del setup API DEBE ser recordado por el actor para navegar a la URL correcta de Ubicaciones.

### Key Entities

- **Folio**: Identificador único de cotización. Creado vía API en precondición. Tiene número de folio (ej. `FOL-2026-00001`).
- **Layout**: Estructura del folio que define el número y nombre de ubicaciones. Definido vía API en precondición.
- **Ubicación**: Unidad geográfica dentro del layout. Tiene: nombre, código postal, giro de negocio (con o sin clave incendio) y garantías tarifables.
- **Badge de estado**: Indicador visual por ubicación que refleja su completitud ("Incompleta" vs. estado completo).
- **Alerta bloqueante**: Mensaje de validación que indica que la ubicación no puede ser tarifada sin corrección, pero no bloquea la navegación del folio.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El escenario completo (setup API + navegación UI + verificaciones) se ejecuta en menos de 60 segundos.
- **SC-002**: El badge "Incompleta" es visible y verificable en la segunda ubicación sin datos en el 100% de las ejecuciones.
- **SC-003**: Al menos 1 alerta bloqueante es detectable en la UI para la ubicación incompleta.
- **SC-004**: La navegación entre pasos del wizard permanece funcional con ubicaciones incompletas — verificable en el 100% de los casos.
- **SC-005**: La primera ubicación (completa) no muestra badge "Incompleta" ni alertas bloqueantes en ninguna ejecución.

## Assumptions

- El flujo de creación de folio y datos generales (flujo 001) ya está cubierto; este flujo NO repite esos pasos en UI.
- El setup de precondiciones usa los endpoints: `POST /v1/folios`, `PUT /v1/folios/{id}/general-info`, `PUT /v1/folios/{id}/layout`.
- El layout de 2 ubicaciones se define en el PUT de layout; los datos específicos de cada ubicación (CP, giro, garantías) se configuran en endpoints de ubicación.
- El código postal válido para la ubicación completa produce una clave incendio asociada al giro seleccionado en el sistema backend.
- Los catálogos mínimos (suscriptor, agente) ya están garantizados por el `CatalogSetupHook` del flujo 001 (`@Before(order=1)`).
- El actor usa `actor.remember("folioNumber", ...)` para propagar el número de folio del setup API a la navegación UI.
- "Alertas bloqueantes" son mensajes de validación visibles en la UI que indican falta de datos para tarificación, pero no deshabilitan la navegación del wizard.
- Las garantías tarifables son las que tienen suficiente información para calcular prima; garantías vacías o sin suma asegurada se consideran no tarifables.

## Clarifications

### Session 2026-04-24

- Q: ¿El setup de ubicaciones (CP, giro, garantías) se realiza vía API o vía UI? → A: Exclusivamente vía API antes de abrir el browser, igual que POST folio y PUT layout.
- Q: ¿El badge de la ubicación incompleta dice exactamente "Incompleta" o puede ser otro texto? → A: El texto exacto se confirmará al leer el HTML del frontend; el criterio es que el badge difiere del estado completo.
- Q: ¿Las alertas bloqueantes aparecen automáticamente al abrir la ubicación o requieren acción del usuario? → A: Aparecen automáticamente al visualizar la ubicación incompleta, sin acción adicional del agente.
