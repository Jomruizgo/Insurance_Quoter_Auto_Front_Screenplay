# Tasks: Creación de Folio y Captura de Datos Generales

**Input**: Design documents from `specs/001-folio-creation-general-info/`
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/ ✅

**Tests**: No incluidas (principio IX de constitución — sin pruebas unitarias sobre código de automatización).

**Organization**: Tareas agrupadas por historia de usuario para implementación independiente.

---

## Phase 1: Setup (Infraestructura compartida)

**Purpose**: Inicialización del proyecto y estructura base. Sin estas tareas ninguna otra puede ejecutarse.

- [x] T001 Crear `build.gradle` con plugin `net.serenity-bdd.serenity-gradle-plugin:4.2.34`, dependencias fijadas por constitución (serenity-core, serenity-cucumber, serenity-screenplay, serenity-screenplay-webdriver 4.2.34; selenium-java 4.33.0; cucumber-junit-platform-engine 7.22.2; junit-platform-suite 1.12.2; junit-jupiter 5.12.2; assertj-core 3.27.3), Java toolchain 21, task `test` finalizada por `aggregate`
- [x] T002 Crear `settings.gradle` con `rootProject.name = 'Insurance_Quoter_Auto_Front_Screenplay'`
- [x] T003 Crear `src/test/resources/serenity.conf` con `webdriver.driver = chrome`, `serenity.base.url = "http://localhost:4200"`, `restapi.base.url = "http://localhost:8080"`, `serenity.project.name`, `serenity.reports = ["single-page-html"]`
- [x] T004 [P] Crear estructura de directorios vacía: `src/test/java/com/sofka/automation/hooks/`, `tasks/ui/`, `tasks/api/`, `questions/`, `targets/`, `stepdefinitions/`, `runners/`, `utils/`; `src/test/resources/features/`

**Checkpoint**: Proyecto compila con `./gradlew clean test` (aunque falle por falta de features).

---

## Phase 2: Foundational (Prerequisitos bloqueantes)

**Purpose**: Infraestructura core que DEBE completarse antes de implementar HU-FRONT-01.

⚠️ CRITICAL: No iniciar Phase 3 hasta completar esta fase.

- [x] T005 Crear `src/test/java/com/sofka/automation/utils/Constants.java` con todas las constantes del proyecto: `BASE_URL = "http://localhost:4200"`, `BACKEND_BASE_URL = "http://localhost:8080"`, `CORE_BASE_URL = "http://localhost:8081"`, `TEST_RAZON_SOCIAL = "Empresa Prueba SA de CV"`, `TEST_RFC = "EPR860101AB2"`, `TEST_EMAIL = "prueba@automation.com"`, `TEST_PHONE = "5512345678"`, `TEST_SUBSCRIBER_ID = "SUB-TEST"`, `TEST_SUBSCRIBER_NAME = "Suscriptor Automatización"`, `TEST_AGENT_CODE = "AGT-TEST"`, `TEST_AGENT_NAME = "Agente Automatización"`
- [x] T006 Crear `src/test/java/com/sofka/automation/hooks/CatalogSetupHook.java` con `@Before(order=1)`: GET `/v1/subscribers` vía RestAssured usando `Constants.BACKEND_BASE_URL`; si respuesta vacía → POST con datos de Constants; misma lógica para GET/POST `/v1/agents`; lanzar excepción si cualquier llamada retorna código fuera de 2xx
- [x] T007 Crear `src/test/resources/features/folio_creation_general_info.feature` con `# language: es`, `Característica: Creación de folio y captura de datos generales`, escenario único con 4 steps declarativos en español (Dado/Cuando/Y/Entonces) según acceptance scenarios de spec.md HU-FRONT-01
- [x] T008 Crear `src/test/java/com/sofka/automation/runners/FolioTestRunner.java` con `@Suite`, `@IncludeEngines("cucumber")`, `@SelectClasspathResource("features/folio_creation_general_info.feature")`, `@ConfigurationParameter` para glue path `com.sofka.automation.stepdefinitions` y plugin `io.cucumber.core.plugin.SerenityReporterParallel`

**Checkpoint**: Proyecto compila y el runner encuentra el feature file (el escenario falla por falta de step definitions — esperado).

---

## Phase 3: HU-FRONT-01 — Creación de Folio y Datos Generales (Priority: P1)

**Goal**: Implementar el escenario completo de creación de folio y captura de datos generales verificando badge de estado completo en ambas secciones.

**Independent Test**: Ejecutar `./gradlew clean test aggregate`. El escenario debe pasar de principio a fin con backend y core corriendo. El hook `@Before` garantiza catálogos; el escenario crea su propio folio por UI.

### Targets (localizadores — implementar primero, en paralelo)

- [x] T009 [P] [US1] Crear `src/test/java/com/sofka/automation/targets/DashboardTargets.java` con constantes `Target`: `NEW_FOLIO_BUTTON` (botón "+ Nuevo folio"), `SUBSCRIBER_DROPDOWN` (dropdown Suscriptor en modal), `SUBSCRIBER_FIRST_OPTION` (primera opción del dropdown Suscriptor), `AGENT_DROPDOWN` (dropdown Agente en modal), `AGENT_FIRST_OPTION` (primera opción del dropdown Agente), `CREATE_FOLIO_BUTTON` (botón "Crear folio" del modal) — leer HTML del frontend en `D:\Trabajo\Sofka\Insurance-Quoter\Insurance-Quoter\Insurance-Quoter-Front\` para obtener selectores CSS/atributos reales
- [x] T010 [P] [US1] Crear `src/test/java/com/sofka/automation/targets/GeneralInfoTargets.java` con constantes `Target`: `RAZON_SOCIAL_INPUT`, `RFC_INPUT`, `EMAIL_INPUT`, `PHONE_INPUT`, `RISK_CLASSIFICATION_DROPDOWN`, `RISK_CLASSIFICATION_FIRST_OPTION`, `BUSINESS_TYPE_DROPDOWN`, `BUSINESS_TYPE_FIRST_OPTION`, `NEXT_BUTTON` (botón "Siguiente →"), `SECTION_COMPLETE_BADGE` (badge "• Completo"), `WIZARD_ACTIVE_STEP` (paso activo en stepper) — leer HTML del frontend para selectores reales

### Questions (en paralelo, dependen de Targets)

- [x] T011 [P] [US1] Crear `src/test/java/com/sofka/automation/questions/SectionCompletionStatus.java` implementando `Question<List<String>>`: localiza todos los elementos `GeneralInfoTargets.SECTION_COMPLETE_BADGE` visibles y retorna lista con el texto/nombre de cada sección que muestra el badge
- [x] T012 [P] [US1] Crear `src/test/java/com/sofka/automation/questions/WizardStepIndicator.java` implementando `Question<String>`: localiza `GeneralInfoTargets.WIZARD_ACTIVE_STEP` y retorna su texto (nombre del paso activo)

### Tasks UI (en paralelo, dependen de Targets)

- [x] T013 [P] [US1] Crear `src/test/java/com/sofka/automation/tasks/ui/CreateFolio.java` implementando `Performable`: navegar a `Constants.BASE_URL`; hacer clic en `DashboardTargets.NEW_FOLIO_BUTTON`; seleccionar `DashboardTargets.SUBSCRIBER_FIRST_OPTION`; seleccionar `DashboardTargets.AGENT_FIRST_OPTION`; hacer clic en `DashboardTargets.CREATE_FOLIO_BUTTON`; extraer folioNumber de la URL activa con `TheWebPage.currentUrl()` mediante regex sobre el patrón `FOL-\d{4}-\d+`; llamar `actor.remember("folioNumber", folioNumber)`
- [x] T014 [P] [US1] Crear `src/test/java/com/sofka/automation/tasks/ui/CompleteGeneralInfo.java` implementando `Performable`: introducir `Constants.TEST_RAZON_SOCIAL` en `GeneralInfoTargets.RAZON_SOCIAL_INPUT`; introducir `Constants.TEST_RFC` en `GeneralInfoTargets.RFC_INPUT`; introducir `Constants.TEST_EMAIL` en `GeneralInfoTargets.EMAIL_INPUT`; introducir `Constants.TEST_PHONE` en `GeneralInfoTargets.PHONE_INPUT`; seleccionar `GeneralInfoTargets.RISK_CLASSIFICATION_FIRST_OPTION`; seleccionar `GeneralInfoTargets.BUSINESS_TYPE_FIRST_OPTION`; hacer clic en `GeneralInfoTargets.NEXT_BUTTON`

### Step Definitions (bloqueante — depende de T009–T014)

- [x] T015 [US1] Crear `src/test/java/com/sofka/automation/stepdefinitions/FolioCreationStepDefinitions.java`: implementar los 4 steps del feature file usando `OnStage.theActorCalled("Agente")`; step "está en el panel" → `actor.attemptsTo(NavigateTo.thePageAt(Constants.BASE_URL))`; step "crea un nuevo folio" → `actor.attemptsTo(CreateFolio.fromDashboard())`; step "completa los datos" → `actor.attemptsTo(CompleteGeneralInfo.withDefaultTestData())`; step "ambas secciones muestran estado completo" → `assertThat(actor.asksAbout(SectionCompletionStatus.forBothSections())).contains("Asegurado", "Suscripción")`; step "folio avanza al paso de layout" → `assertThat(actor.asksAbout(WizardStepIndicator.currentStep())).containsIgnoringCase("Layout")`

**Checkpoint**: `./gradlew clean test aggregate` ejecuta el escenario completo sin errores. Reporte en `target/site/serenity/`.

---

## Phase N: Polish & Cross-Cutting Concerns

- [x] T016 [P] Crear `README.md` en raíz con: requisitos previos, comando de ejecución `./gradlew clean test aggregate`, ubicación del reporte, configuración del browser, estructura del proyecto
- [x] T017 Validar ejecución end-to-end siguiendo `specs/001-folio-creation-general-info/quickstart.md`: verificar que todos los SC-001 a SC-005 pasan, reporte Serenity generado con capturas por step

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: Sin dependencias — iniciar inmediatamente
- **Phase 2 (Foundational)**: Depende de Phase 1 completa — BLOQUEA Phase 3
- **Phase 3 (HU-FRONT-01)**: Depende de Phase 2 completa
  - T009, T010 (Targets): en paralelo, sin dependencias entre sí
  - T011, T012 (Questions): en paralelo, dependen de Targets correspondientes
  - T013, T014 (Tasks UI): en paralelo, dependen de Targets correspondientes
  - T015 (StepDefinitions): bloqueante en T009–T014 todos completos
- **Phase N (Polish)**: Depende de Phase 3 completa

### Within HU-FRONT-01

Targets → Questions & Tasks UI (en paralelo) → StepDefinitions → validación

### Parallel Opportunities

```bash
# Phase 2 — en paralelo (archivos distintos):
T005 Constants.java
T006 CatalogSetupHook.java
T007 folio_creation_general_info.feature
T008 FolioTestRunner.java

# Phase 3 — en paralelo tras Phase 2:
T009 DashboardTargets.java
T010 GeneralInfoTargets.java
T011 SectionCompletionStatus.java  # tras T010
T012 WizardStepIndicator.java      # tras T010
T013 CreateFolio.java              # tras T009
T014 CompleteGeneralInfo.java      # tras T010
# T015 FolioCreationStepDefinitions.java — bloquea en T009–T014
```

---

## Implementation Strategy

### MVP (HU-FRONT-01 único)

1. Phase 1: Setup
2. Phase 2: Foundational (CRÍTICO — bloquea todo)
3. Phase 3: HU-FRONT-01
4. **STOP y VALIDAR**: `./gradlew clean test aggregate` — escenario pasa end-to-end
5. Polish: README + quickstart validation

---

## Summary

| Métrica | Valor |
|---------|-------|
| Total tareas | 17 |
| Phase 1 Setup | T001–T004 (4 tareas) |
| Phase 2 Foundational | T005–T008 (4 tareas) |
| Phase 3 HU-FRONT-01 | T009–T015 (7 tareas) |
| Phase N Polish | T016–T017 (2 tareas) |
| Oportunidades paralelo | T004, T005–T008, T009–T014 |
| Tests incluidas | Ninguna (principio IX) |
| MVP scope | Phase 1 + 2 + 3 completas |

---

## Notes

- `[P]` = archivos distintos, sin dependencias — ejecutar en paralelo
- `[US1]` mapea a HU-FRONT-01 en spec.md
- Sin tests unitarios (principio IX de constitución)
- Leer HTML de `D:\Trabajo\Sofka\Insurance-Quoter\Insurance-Quoter\Insurance-Quoter-Front\` para selectores CSS reales en T009 y T010
- Todos los valores literales en `Constants.java` (T005) — ningún valor hardcoded en otras clases
- Commit después de cada fase completada siguiendo Conventional Commits
