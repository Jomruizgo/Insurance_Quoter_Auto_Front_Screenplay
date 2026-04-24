# Feature Specification: Coberturas y Cálculo de Prima

**Feature Branch**: `003-coverage-premium-calc`  
**Created**: 2026-04-24  
**Status**: Draft  

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Activar cobertura y calcular prima para ubicación completa (Priority: P1)

El agente, desde la pantalla de coberturas del folio, selecciona la cobertura de incendio (COV-FIRE) para la ubicación que tiene datos completos (código postal válido, giro con clave incendio y garantías tarifables), guarda los cambios y ejecuta el cálculo de prima. El sistema calcula y muestra el desglose con prima neta y prima comercial con valores mayores a cero para esa ubicación.

**Why this priority**: Es el flujo central del cotizador. Sin prima calculada no hay cotización. Valida que el motor de cálculo responde correctamente cuando los datos son suficientes.

**Independent Test**: Puede probarse de forma aislada creando un folio vía API con una única ubicación completa, activando COV-FIRE desde la UI y verificando que el desglose muestra valores positivos.

**Acceptance Scenarios**:

1. **Given** un folio con una ubicación completa (CP válido, giro con clave incendio, garantía tarifable) precargado vía API, **When** el agente navega a la pantalla de coberturas, activa COV-FIRE en esa ubicación y ejecuta el cálculo, **Then** el desglose muestra prima neta con valor mayor a cero y prima comercial con valor mayor a cero para esa ubicación.

2. **Given** el cálculo fue ejecutado exitosamente, **When** el agente visualiza el resumen de la ubicación completa, **Then** la ubicación aparece con estado de prima calculada (no muestra "No calculable" ni alertas de datos insuficientes).

---

### User Story 2 - Verificar estado "No calculable" para ubicación incompleta (Priority: P2)

Al ejecutar el cálculo con un folio que contiene una ubicación incompleta (sin código postal, sin giro ni garantías), esa ubicación aparece marcada como "No calculable" y muestra sus alertas de datos faltantes, sin bloquear el resultado de la ubicación completa.

**Why this priority**: Valida que el sistema distingue correctamente entre ubicaciones calculables e incalculables dentro del mismo folio, y que el resultado parcial es visible y claro para el agente.

**Independent Test**: Puede probarse junto a US1 en el mismo folio de dos ubicaciones (1 completa + 1 incompleta). La ubicación incompleta debe mostrar "No calculable" mientras la completa muestra prima positiva.

**Acceptance Scenarios**:

1. **Given** el mismo folio con dos ubicaciones (1 completa ya calculada, 1 incompleta), **When** el agente visualiza el desglose tras el cálculo, **Then** la ubicación incompleta aparece con la etiqueta "No calculable" y muestra alertas sobre los datos faltantes.

2. **Given** la ubicación incompleta marcada como "No calculable", **When** el agente revisa el desglose total del folio, **Then** el desglose muestra únicamente la prima de la ubicación calculable, sin incluir valores de la incompleta.

---

### User Story 3 - Verificar independencia del cálculo respecto a ubicaciones incompletas (Priority: P3)

Con un folio de referencia que contiene únicamente una ubicación completa (sin ubicación incompleta), el agente activa COV-FIRE y ejecuta el cálculo. La prima neta resultante debe ser positiva y equivalente a la prima que produce la misma ubicación dentro del folio de dos ubicaciones (US1). Esto valida que la presencia de una ubicación incompleta no altera el resultado de la calculable.

**Why this priority**: Valida SC-004. Sin este escenario, SC-004 queda como afirmación no verificada.

**Independent Test**: Requiere un folio de referencia con exactamente una ubicación completa (mismos datos: CP 06600, OFICINAS, COV-FIRE, INCENDIO, $1M). El hook de setup usa los mismos datos que US1 pero sin ubicación incompleta.

**Nota de implementación**: La comparación de valor exacto entre Scenario 1 y Scenario 3 requeriría estado compartido entre escenarios (violación del principio IV). En la práctica, SC-004 se valida demostrando que el folio de una sola ubicación produce prima > 0 con los mismos datos de entrada. Si el motor de cálculo es determinista, el mismo input siempre produce el mismo output.

**Acceptance Scenarios**:

1. **Given** un folio de referencia con una única ubicación completa (sin ubicación incompleta) precargado vía API, **When** el agente activa COV-FIRE y ejecuta el cálculo, **Then** la prima neta es mayor a cero, confirmando que el resultado es independiente de la presencia de otras ubicaciones.

---

### Edge Cases

- ¿Qué ocurre si el agente intenta calcular sin haber activado ninguna cobertura en la ubicación completa? → El sistema debe impedir el cálculo o mostrar un mensaje informativo.
- ¿Qué ocurre si la cobertura COV-FIRE ya estaba activa en el folio precargado desde la API? → El escenario debe ser idempotente; activar una cobertura ya activa no debe producir error.
- ¿Qué ocurre si el agente navega al desglose sin haber guardado primero las coberturas? → El sistema debe advertir o deshabilitar el botón de cálculo hasta guardar.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El agente DEBE poder activar o desactivar coberturas individuales por ubicación desde la pantalla de coberturas.
- **FR-002**: El sistema DEBE permitir guardar el estado de las coberturas seleccionadas antes de ejecutar el cálculo.
- **FR-003**: El sistema DEBE ejecutar el cálculo de prima al recibir la instrucción del agente, procesando todas las ubicaciones del folio.
- **FR-004**: El sistema DEBE mostrar un desglose con prima neta y prima comercial con valores numéricos positivos para cada ubicación calculable.
- **FR-005**: El sistema DEBE mostrar la etiqueta "No calculable" para ubicaciones que carezcan de los datos mínimos requeridos (código postal, giro, garantías).
- **FR-006**: Las alertas de datos faltantes DEBEN ser visibles junto a la etiqueta "No calculable" de la ubicación incompleta.
- **FR-007**: El resultado de una ubicación calculable NO DEBE verse afectado por la presencia de ubicaciones incalculables en el mismo folio.
- **FR-008**: El precargado de datos del folio (folio, datos generales, layout, ubicaciones) DEBE realizarse vía API antes de abrir el navegador.

### Key Entities

- **Folio**: Unidad de cotización que agrupa ubicaciones; identificado por número de folio.
- **Ubicación completa**: Ubicación con código postal válido, giro con clave incendio y al menos una garantía tarifable.
- **Ubicación incompleta**: Ubicación sin código postal, sin giro o sin garantías; no puede ser tarifada.
- **Cobertura (COV-FIRE)**: Tipo de riesgo cubierto — incendio; debe activarse explícitamente por el agente para que la ubicación entre al cálculo.
- **Prima neta**: Valor calculado por el motor de tarifación para la cobertura y ubicación dados; debe ser mayor a cero para una ubicación completa.
- **Prima comercial**: Valor derivado de la prima neta con ajustes comerciales; debe ser mayor o igual a la prima neta.
- **Desglose de prima**: Vista resumida que presenta prima neta, prima comercial y estado de cálculo por ubicación.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El agente completa el flujo de activar cobertura, guardar y calcular en menos de 60 segundos desde que abre la pantalla de coberturas.
- **SC-002**: El desglose muestra prima neta y prima comercial con valores numéricos mayores a cero para la ubicación completa en el 100% de las ejecuciones con datos válidos.
- **SC-003**: La ubicación incompleta aparece etiquetada como "No calculable" en el 100% de los casos donde faltan datos mínimos.
- **SC-004**: El resultado de la ubicación calculable no se ve alterado por la presencia de la ubicación incompleta — verificado mediante un escenario dedicado (US3) que calcula prima para un folio de referencia con una única ubicación completa y confirma prima neta > 0 con los mismos datos de entrada.

## Clarifications

### Session 2026-04-24

- Q: ¿Qué debe hacer el escenario de automatización respecto a SC-004 (comparar con folio de referencia de una sola ubicación)? → A: Agregar un tercer escenario (US3) con folio de una sola ubicación completa que verifica prima neta > 0, validando SC-004 de forma práctica sin estado compartido entre escenarios.
- Q: ¿Cómo manejar el setup de datos para US3 sin duplicar código? → A: Una sola Task `SetupCoveragePremiumScenario` con dos métodos de fábrica estáticos: `withTwoLocations()` para US1/US2 y `withSingleLocation()` para US3. Sin clases duplicadas.

## Assumptions

- El folio se crea y se configuran datos generales, layout y ubicaciones completamente vía API antes de iniciar la prueba de UI (setup de precondiciones fuera del navegador).
- La Task API de setup `SetupCoveragePremiumScenario` expone dos métodos de fábrica: `withTwoLocations()` (1 completa + 1 incompleta, para US1/US2) y `withSingleLocation()` (1 completa, para US3). Sin clases duplicadas.
- Los flujos de creación de folio (001) y registro de ubicaciones (002) ya están cubiertos por automatizaciones anteriores y no se repiten aquí.
- La cobertura COV-FIRE es la única que se activa en este escenario; otras coberturas no se tocan.
- El motor de cálculo está disponible y responde con valores tarifados para datos de ubicación válidos (CP 06600, giro OFICINAS con clave incendio 1110, garantía INCENDIO con valor asegurado 1.000.000).
- La pantalla de coberturas es accesible mediante URL directa con el número de folio.
- El cálculo incluye ambas ubicaciones del folio simultáneamente; no existe cálculo por ubicación individual desde la UI.
