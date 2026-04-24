# Research: Coberturas y Cálculo de Prima

**Feature**: 003-coverage-premium-calc  
**Date**: 2026-04-24

---

## Decisión 1: URL de la pantalla de coberturas

**Decision**: `/cotizador/quotes/{folioNumber}/technical-info`  
**Rationale**: Inspeccionado directamente del template Angular `technical-info.page.html`. El título de la sección confirma "Opciones de cobertura por ubicación · Paso 4 de 5".  
**Alternatives considered**: `/cotizador/quotes/{folio}/coverages` — no existe; el router usa `technical-info`.

---

## Decisión 2: URL de la pantalla de cálculo

**Decision**: `/cotizador/quotes/{folioNumber}/calculation`  
**Rationale**: Inspeccionado de `calculation.page.html`. El título "Cálculo de prima · Paso 5 de 5" confirma que es el último paso del wizard.  
**Alternatives considered**: `/cotizador/quotes/{folio}/premium` — no existe.

---

## Decisión 3: Estrategia de selección de cobertura por código

**Decision**: XPath dinámico que localiza el `coverage-card` conteniendo el código de cobertura, luego desciende a `app-switch`.  
Selector: `//div[contains(@class,'coverage-card')][.//code[contains(@class,'coverage-card__code') and normalize-space()='COV-FIRE']]//app-switch`  
**Rationale**: El template muestra `<code class="coverage-card__code">{{ coverage.code }}</code>` dentro de cada tarjeta. Es la única forma determinista de identificar una cobertura por su código sin depender de posición en el listado.  
**Risk**: `app-switch` es un componente Angular custom; si no es clickable directamente, puede necesitarse `//app-switch//input` o `//app-switch//button`. Verificar en primera ejecución.  
**Alternatives considered**: Localizar por posición (primera tarjeta) — frágil si el orden de coberturas cambia.

---

## Decisión 4: Verificación de prima neta y prima comercial

**Decision**: Leer texto de `.premium-summary__card--dark .premium-summary__card-value` (prima neta) y `.premium-summary__card--brand .premium-summary__card-value` (prima comercial). Verificar que el texto no es "$0.00" ni "-".  
**Rationale**: Las clases `--dark` y `--brand` son estáticas y distinguen las dos cards. El valor es texto formateado en MXN (`$1,234.56`); se limpia con regex para extraer número y comparar > 0.  
**Alternatives considered**: Comparar con valor exacto — no viable sin conocer el tarificador; verificar `> 0` es suficiente para el escenario.

---

## Decisión 5: Verificación de "No calculable"

**Decision**: XPath `//li[contains(@class,'premium-summary__location-item')]//app-badge[normalize-space(.)='No calculable']` — verificar que al menos un elemento existe.  
**Rationale**: El template Angular muestra `<app-badge variant="warn">No calculable</app-badge>` dentro de cada `li.premium-summary__location-item` cuando `!p.calculable`. El texto es constante y distinguible.  
**Risk**: `app-badge` es custom; el texto real en el DOM puede estar en un `<span>` interno. Si XPath falla, usar `//li[contains(@class,'premium-summary__location-item')][.//text()[normalize-space()='No calculable']]`.  
**Alternatives considered**: CSS selector con clase de badge — no hay clase específica de "no calculable" en el CSS inspeccionado.

---

## Decisión 6: API de activación de coberturas (NO usada en setup)

**Decision**: La cobertura COV-FIRE se activa exclusivamente vía UI. El hook `@Before` NO llama a `PUT /v1/quotes/{folio}/coverage-options`.  
**Rationale**: La activación de cobertura es exactamente la acción UI bajo prueba en este flujo. Pre-activarla vía API convertiría el test en una validación del cálculo, no del flujo completo.  
**API endpoint disponible**: `PUT /v1/quotes/{folio}/coverage-options` con body `{ coverageOptions: [{ code, selected, deductiblePercentage, coinsurancePercentage }], version }`.  
**Alternatives considered**: Activar vía API y solo verificar en UI — descartado (no prueba la acción del agente).

---

## Decisión 7: Reutilización del setup API de feature 002

**Decision**: `SetupCoveragePremiumScenario` replica la lógica de `SetupLocationScenario` (POST folio, PUT general-info, PUT locations con 1 completa + 1 incompleta). NO se extiende ni hereda; se crea como clase independiente.  
**Rationale**: Principio XI (no overengineering). Las dos Tasks son autónomas y su lógica podría divergir en el futuro. Herencia o utilidad compartida añadiría acoplamiento innecesario.  
**Alternatives considered**: Reutilizar `SetupLocationScenario` con un parámetro — descartado; viola encapsulamiento de Task.

---

## Decisión 8: Código de cobertura de incendio

**Decision**: Usar `"COV-FIRE"` como código de la cobertura de incendio.  
**Rationale**: Indicado en la spec por el usuario. Debe verificarse contra el catálogo del backend en primera ejecución (`GET /v1/quotes/{folio}/coverage-options`).  
**Risk**: Si el backend usa un código diferente (ej. `"INC"`, `"FIRE"`, `"001"`), actualizar `Constants.COVERAGE_CODE_FIRE` y el XPath en `CoveragesTargets`.
