# Tasks: Registro de Ubicaciones

**Input**: Design documents from `specs/002-location-registration/`
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/ ✅

**Tests**: No aplica — constitución principio IX prohíbe pruebas unitarias sobre Tasks/Questions.

**Organization**: Tasks agrupadas por user story. US1 (P1) y US2 (P2) comparten infraestructura de la fase fundacional.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Puede ejecutarse en paralelo (archivos distintos, sin dependencias incompletas)
- **[Story]**: User story a la que pertenece ([US1], [US2])
- Paths absolutos en `src/test/java/com/sofka/automation/` y `src/test/resources/`

---

## Phase 1: Setup (Constantes)

**Purpose**: Agregar valores de prueba para ubicaciones a `Constants.java`. Sin este paso, ninguna otra clase puede compilar.

- [ ] T001 Add 6 location test constants to `src/test/java/com/sofka/automation/utils/Constants.java`: `TEST_LOCATION_ZIP_CODE = "06600"`, `TEST_LOCATION_BL_CODE = "OFICINAS"`, `TEST_LOCATION_BL_FIRE_KEY = "1110"`, `TEST_LOCATION_GUARANTEE_CODE = "INCENDIO"`, `TEST_LOCATION_INSURED_VALUE = 1_000_000`, `LOCATIONS_URL_TEMPLATE = "/cotizador/quotes/%s/locations"` <!-- #20 -->

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura compartida por US1 y US2: localizadores, Task API de setup y hook de precondición.

**⚠️ CRÍTICO**: Ninguna user story puede implementarse hasta completar esta fase.

- [ ] T002 [P] Create `src/test/java/com/sofka/automation/targets/LocationsTargets.java` with static `badgeForIndex(int index)` returning XPath Target `//table[contains(@class,'locations-table')]//tbody/tr[td[2][normalize-space(text())='{index}']]//span[contains(@class,'badge')]`, constant `BLOCKING_ALERTS_BANNER` using CSS `div.alert-banner.alert-banner--warn`, and constant `NEXT_BUTTON` using XPath `//div[contains(@class,'page-nav')]//button[contains(normalize-space(.),'Siguiente')]` <!-- #21 -->
- [ ] T003 [P] Create `src/test/java/com/sofka/automation/tasks/api/SetupLocationScenario.java` implementing `Performable`: (1) POST `{restapi.base.url}/v1/folios` with `subscriberId=Constants.TEST_SUBSCRIBER_ID` and `agentCode=Constants.TEST_AGENT_CODE` → extract `id` and `folioNumber`; (2) PUT `{restapi.base.url}/v1/folios/{id}/general-info` with `razonSocial`, `rfc`, `email`, `phone` from Constants; (3) PUT `{restapi.base.url}/v1/quotes/{folioNumber}/locations` with 2-location JSON array (location 1: complete with zipCode/businessLine/guarantees; location 2: incomplete with empty fields); call `actor.remember("folioNumber", folioNumber)` and `actor.remember("folioId", folioId)` — throw `IllegalStateException` if any call returns non-2xx <!-- #22 -->
- [ ] T004 Create `src/test/java/com/sofka/automation/hooks/LocationScenarioSetupHook.java` with `@Before(order=2, value="@location-registration")` annotation that calls `actor.attemptsTo(SetupLocationScenario.forCurrentActor())` using `OnStage.theActorCalled("agent")` (depends on T003) <!-- #23 -->

**Checkpoint**: LocationsTargets, SetupLocationScenario y LocationScenarioSetupHook listos — implementación de US1 puede comenzar.

---

## Phase 3: User Story 1 — Badge "Incompleta" y Banner de Alertas (Priority: P1) 🎯 MVP

**Goal**: Verificar que una ubicación sin CP, giro ni garantías muestra badge `"Incompleta"` y un banner de alertas bloqueantes visible en la pantalla de Ubicaciones, sin bloquear la navegación del wizard.

**Independent Test**: Con folio creado vía API por el hook, navegar a `/cotizador/quotes/{folioNumber}/locations` y verificar que la ubicación 2 tiene badge `"Incompleta"` y el banner `div.alert-banner.alert-banner--warn` está visible.

- [ ] T005 [P] [US1] Create `src/test/java/com/sofka/automation/questions/LocationBadgeStatus.java` implementing `Question<String>` with factory method `forLocationIndex(int index)` — uses `LocationsTargets.badgeForIndex(index)` to find the badge span and returns its trimmed text; returns empty string `""` if element not found <!-- #24 -->
- [ ] T006 [P] [US1] Create `src/test/java/com/sofka/automation/questions/BlockingAlertsBanner.java` implementing `Question<Boolean>` with factory method `isVisible()` — checks if `LocationsTargets.BLOCKING_ALERTS_BANNER` is present and displayed; returns `true` if visible, `false` if absent <!-- #25 -->
- [ ] T007 [US1] Create `src/test/resources/features/location_registration.feature` with `# language: es` header, feature description, and `@location-registration` scenario (5 steps max): (1) `Dado que el agente navega a la pantalla de ubicaciones del folio`; (2) `Cuando el agente visualiza la segunda ubicación sin datos`; (3) `Entonces la segunda ubicación muestra el badge "Incompleta"`; (4) `Y aparece un banner de alertas bloqueantes en la pantalla`; (5) `Y el agente puede navegar al siguiente paso del wizard` (depends on T005, T006) <!-- #26 -->
- [ ] T008 [US1] Create `src/test/java/com/sofka/automation/stepdefinitions/LocationRegistrationStepDefinitions.java` with: `@Before(order=10)` configuring `OnStage.setTheStage(Cast.whereEveryoneCanBrowseTheWeb())`; `@After` calling `Serenity.drawTheCurtain()`; step `"navega a la pantalla de ubicaciones"` calls `actor.attemptsTo(Open.url(String.format(Constants.LOCATIONS_URL_TEMPLATE, actor.recall("folioNumber"))))`; step `"visualiza la segunda ubicación sin datos"` is a no-op (state visible on page load); step `"badge Incompleta"` calls `actor.should(seeThat(LocationBadgeStatus.forLocationIndex(2), equalTo("Incompleta")))`; step `"banner de alertas"` calls `actor.should(seeThat(BlockingAlertsBanner.isVisible(), is(true)))`; step `"navegar al siguiente paso"` calls `actor.attemptsTo(Click.on(LocationsTargets.NEXT_BUTTON))` (depends on T007) <!-- #27 -->
- [ ] T009 [US1] Create `src/test/java/com/sofka/automation/runners/LocationTestRunner.java` with `@Suite`, `@IncludeEngines("cucumber")`, `@SelectClasspathResource("features/location_registration.feature")`, glue `"com.sofka.automation.stepdefinitions,com.sofka.automation.hooks"`, plugin `"io.cucumber.core.plugin.SerenityReporterParallel"` (depends on T007, T008) <!-- #28 -->

**Checkpoint**: US1 completo y ejecutable — `./gradlew clean test -Dcucumber.filter.tags="@location-registration" aggregate` debe producir reporte verde para el escenario de badge incompleto.

---

## Phase 4: User Story 2 — Ubicación Completa sin Alertas (Priority: P2)

**Goal**: Confirmar que la ubicación con CP válido, giro con clave incendio y garantía tarifable NO muestra badge `"Incompleta"` ni banner de alertas bloqueantes.

**Independent Test**: En la misma pantalla de Ubicaciones (folio con 2 ubicaciones), verificar que la ubicación 1 muestra badge `"Completa"` y que las Questions de US1 funcionan correctamente para el caso positivo.

- [ ] T010 [US2] Add second scenario to `src/test/resources/features/location_registration.feature` with tag `@location-registration` (5 steps max): (1) `Dado que el agente navega a la pantalla de ubicaciones del folio`; (2) `Cuando el agente visualiza la primera ubicación con datos completos`; (3) `Entonces la primera ubicación no muestra el badge "Incompleta"`; (4) `Y no aparece banner de alertas bloqueantes en la pantalla`; (5) `Y la primera ubicación muestra el badge "Completa"` (depends on T009) <!-- #29 -->
- [ ] T011 [US2] Add US2 step bindings to `src/test/java/com/sofka/automation/stepdefinitions/LocationRegistrationStepDefinitions.java`: step `"visualiza la primera ubicación con datos completos"` is a no-op; step `"primera ubicación no muestra badge Incompleta"` calls `actor.should(seeThat(LocationBadgeStatus.forLocationIndex(1), not(equalTo("Incompleta"))))`; step `"no aparece banner"` calls `actor.should(seeThat(BlockingAlertsBanner.isVisible(), is(false)))`; step `"primera ubicación muestra badge Completa"` calls `actor.should(seeThat(LocationBadgeStatus.forLocationIndex(1), equalTo("Completa")))` (depends on T010) <!-- #30 -->

**Checkpoint**: US1 y US2 completamente funcionales — ambos escenarios producen reporte Serenity verde.

---

## Phase 5: Polish & Validación Final

**Purpose**: Validación end-to-end con reporte completo.

- [ ] T012 Execute `./gradlew clean test -Dcucumber.filter.tags="@location-registration" aggregate` and verify `target/site/serenity/index.html` shows 2 passing scenarios, SC-001 met (<60s total), zero failures <!-- #31 -->

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Sin dependencias — comenzar inmediatamente
- **Foundational (Phase 2)**: Depende de T001 completado — bloquea US1 y US2
- **US1 (Phase 3)**: Depende de T001–T004 — T005/T006 paralelos, luego T007→T008→T009
- **US2 (Phase 4)**: Depende de T005–T009 — T010→T011 secuencial
- **Polish (Phase 5)**: Depende de T001–T011

### User Story Dependencies

- **US1 (P1)**: Puede iniciar tras Foundational — sin dependencia de US2
- **US2 (P2)**: Reutiliza LocationBadgeStatus y BlockingAlertsBanner de US1 — puede iniciar tras T006

### Within US1

```
T005 ──┐
       ├──→ T007 ──→ T008 ──→ T009
T006 ──┘
```

### Within US2 (after US1 complete)

```
T010 ──→ T011
```

---

## Parallel Opportunities

### Phase 2 — Foundational

```
T002 (LocationsTargets)       ─── paralelo
T003 (SetupLocationScenario)  ─── paralelo
                                    ↓
T004 (LocationScenarioSetupHook)  ─── espera T003
```

### Phase 3 — US1

```
T005 (LocationBadgeStatus)    ─── paralelo
T006 (BlockingAlertsBanner)   ─── paralelo
                                    ↓
T007 (feature file)           ─── espera T005, T006
T008 (step definitions)       ─── espera T007
T009 (runner)                 ─── espera T007, T008
```

---

## Implementation Strategy

### MVP (US1 solamente)

1. Completar Phase 1: T001
2. Completar Phase 2: T002, T003 (paralelo) → T004
3. Completar Phase 3: T005, T006 (paralelo) → T007 → T008 → T009
4. **VALIDAR**: `./gradlew clean test -Dcucumber.filter.tags="@location-registration"` — badge incompleto verificado

### Entrega incremental completa

1. MVP (US1) → badge + banner verificados
2. Agregar US2 (T010, T011) → ubicación completa verificada
3. Polish (T012) → reporte final Serenity

---

## Notes

- Archivos de feature 001 NO SE MODIFICAN — CatalogSetupHook, Constants.java (solo adición), FolioTestRunner, etc.
- `LocationBadgeStatus.forLocationIndex(n)` es reutilizable — US1 usa índice 2, US2 usa índice 1
- El hook `@Before(order=2)` se activa solo para `@location-registration` — no interfiere con otros escenarios
- Si RestAssured retorna non-2xx en SetupLocationScenario → `IllegalStateException` → el escenario falla con mensaje descriptivo antes de abrir el browser
