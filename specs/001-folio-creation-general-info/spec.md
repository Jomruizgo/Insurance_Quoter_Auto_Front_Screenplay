# Feature Specification: Creación de Folio y Captura de Datos Generales

**Feature Branch**: `001-folio-creation-general-info`
**Created**: 2026-04-24
**Status**: Draft
**Input**: Automatización del flujo de creación de folio y captura de datos generales en la SPA cotizador-danos-web.

## User Scenarios & Testing *(mandatory)*

### HU-FRONT-01 — Creación de folio y registro de datos generales (Priority: P1)

Como agente del cotizador,
quiero crear un nuevo folio seleccionando suscriptor y agente, y completar
los datos del asegurado y la suscripción,
para iniciar una cotización con toda la información general registrada.

**Why this priority**: Es el punto de entrada obligatorio de toda cotización.
Sin folio creado y datos generales completos, ningún paso posterior del wizard
(layout, ubicaciones, coberturas, cálculo) puede ejecutarse.

**Independent Test**: El escenario es autónomo — crea su propio folio desde
el dashboard y verifica que ambas secciones muestran estado completo al finalizar.
No depende de datos preexistentes más allá de los catálogos mínimos garantizados
por el hook `@Before`.

**Acceptance Scenarios**:

1. **Given** el agente está en el panel de cotizaciones,
   **When** crea un nuevo folio con un suscriptor y agente disponibles,
   **Then** el sistema genera un número de folio único y muestra el paso 1 del wizard.

2. **Given** el folio fue creado y el wizard muestra el paso de datos generales,
   **When** el agente completa los datos del asegurado y la suscripción,
   **Then** ambas secciones muestran estado completo y el wizard permite avanzar al layout.

---

### Edge Cases

- ¿Qué sucede si los catálogos de suscriptores o agentes están vacíos?
  El hook `@Before` garantiza al menos un elemento en cada catálogo antes
  de que arranque el escenario; si falla el hook, el escenario falla con
  error de precondición, no de UI.
- ¿Qué sucede si el backend no responde al crear el folio?
  El escenario falla con error de conexión; no se requiere reintento automático.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: La automatización MUST navegar al dashboard del cotizador
  (`/cotizador`) y verificar que el panel de folios está visible.
- **FR-002**: La automatización MUST abrir el modal de creación de folio,
  seleccionar el primer suscriptor disponible en el catálogo y el primer
  agente disponible, y confirmar la creación.
- **FR-003**: El número de folio generado MUST capturarse y almacenarse en
  la sesión del actor (`actor.remember("folioNumber")`) para propagación
  entre steps.
- **FR-004**: La automatización MUST completar la sección "Asegurado" con
  valores válidos: razón social, RFC, correo de contacto y teléfono.
- **FR-005**: La automatización MUST completar la sección "Suscripción"
  seleccionando clasificación de riesgo y tipo de negocio de los catálogos.
- **FR-006**: La automatización MUST verificar que ambas secciones muestran
  el badge de estado completo antes de avanzar.
- **FR-007**: La automatización MUST avanzar al siguiente paso del wizard
  y verificar que el stepper indica el paso de Layout activo.

### Key Entities

- **Folio**: identificador único de cotización generado por el sistema
  (formato `FOL-YYYY-NNNNN`). Atributos relevantes: `folioNumber`.
- **DatosAsegurado**: razón social, RFC, correo de contacto, teléfono.
- **DatosSuscripcion**: suscriptor (precargado del modal), agente (precargado
  del modal), clasificación de riesgo, tipo de negocio.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El escenario completo se ejecuta de principio a fin sin errores
  cuando el backend y el core están disponibles.
- **SC-002**: El número de folio generado es no vacío y sigue el formato
  esperado (`FOL-YYYY-NNNNN`).
- **SC-003**: Ambas secciones (Asegurado y Suscripción) muestran badge de
  estado completo al finalizar el step de datos generales.
- **SC-004**: El reporte Serenity muestra el resultado del escenario con
  detalle paso a paso y capturas de pantalla.
- **SC-005**: El escenario no depende de estado previo en la base de datos
  más allá de los catálogos garantizados por el hook `@Before`.

## Assumptions

- El backend (`http://localhost:8080`) y el core (`http://localhost:8081`)
  están corriendo antes de ejecutar la automatización.
- El hook `@Before(order=1)` garantiza al menos 1 suscriptor y 1 agente en
  los catálogos antes de que arranque el escenario.
- Los valores de prueba para la sección "Asegurado" son fijos (hardcoded en
  `Constants.java`): razón social, RFC, correo y teléfono de ejemplo.
- No se requiere autenticación para acceder a la aplicación.
- El folio se crea por UI en este escenario porque eso es el comportamiento
  que se está probando; flows 002 y 003 reutilizan esta cobertura y crean el
  folio por API en su setup.
- El wizard avanza automáticamente al paso de Layout al hacer clic en
  "Siguiente →" con todas las secciones completas.
