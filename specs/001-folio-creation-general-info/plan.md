# Implementation Plan: Creación de Folio y Captura de Datos Generales

**Branch**: `001-folio-creation-general-info` | **Date**: 2026-04-24 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `specs/001-folio-creation-general-info/spec.md`

## Summary

Implementar el primer flujo de automatización UI del cotizador: creación de folio
desde el dashboard mediante el modal de "+ Nuevo folio" y completado de los datos
generales del asegurado y la suscripción en el paso 1 del wizard. El escenario
verifica que ambas secciones muestran estado completo y el wizard avanza al layout.

## Technical Context

**Language/Version**: Java 21
**Primary Dependencies**: Serenity BDD 4.2.34, serenity-screenplay-webdriver 4.2.34,
Selenium 4.33.0, Cucumber 7.22.2, AssertJ 3.27.3
**Storage**: N/A
**Testing**: N/A (principio IX — sin pruebas sobre el código de automatización)
**Target Platform**: JVM + Chrome browser, SUT en http://localhost:4200
**Project Type**: UI test automation (Screenplay pattern)
**Performance Goals**: N/A
**Constraints**: Versiones fijadas por constitución; máximo 5 steps Gherkin; sin Thread.sleep
**Scale/Scope**: 1 feature file, 1 escenario, 2 Tasks UI, 1 Hook, 2 Questions, 2 Target classes

## Constitution Check

*GATE: Debe pasar antes de iniciar la implementación.*

| Principio | Estado | Evidencia |
|-----------|--------|-----------|
| I. Screenplay Pattern | ✅ PASS | `CreateFolio` + `CompleteGeneralInfo` implementan `Performable`; `SectionCompletionStatus` + `WizardStepIndicator` implementan `Question<T>`; localizadores en `DashboardTargets` y `GeneralInfoTargets` |
| II. Bounded Scope | ✅ PASS | Flow 001 dentro del alcance de 3 flujos definidos; sin POM |
| III. Declarative Gherkin | ✅ PASS | 4 steps en español, lenguaje de negocio, sin referencias técnicas |
| IV. Test Data Independence | ✅ PASS | `CatalogSetupHook` @Before(order=1); folio creado por UI (comportamiento bajo prueba) |
| V. Separation of Concerns | ✅ PASS | StepDefs orquestan; Tasks actúan en UI; Questions extraen estado |
| VI. Language Convention | ✅ PASS | Código Java en inglés; Gherkin y docs en español |
| VII. No Hardcoding | ✅ PASS | Todos los valores de prueba en `Constants.java` |
| VIII. GitFlow | ✅ PASS | Feature branch desde develop, merge vía PR |
| IX. No Self-Testing | ✅ PASS | Sin clases de test sobre Tasks ni Questions |
| X. Pinned Dependencies | ✅ PASS | Versiones exactas según constitución |
| XI. No Overengineering | ✅ PASS | 2 Tasks, 2 Questions, 2 Target classes — mínimo necesario |

## Project Structure

### Documentación (este feature)

```text
specs/001-folio-creation-general-info/
├── spec.md
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── catalog-setup-api.md
└── checklists/
    └── requirements.md
```

### Código fuente (raíz del repositorio)

```text
build.gradle
settings.gradle
serenity.conf
src/test/
├── java/com/sofka/automation/
│   ├── hooks/
│   │   └── CatalogSetupHook.java
│   ├── tasks/
│   │   └── ui/
│   │       ├── CreateFolio.java
│   │       └── CompleteGeneralInfo.java
│   ├── questions/
│   │   ├── SectionCompletionStatus.java
│   │   └── WizardStepIndicator.java
│   ├── targets/
│   │   ├── DashboardTargets.java
│   │   └── GeneralInfoTargets.java
│   ├── stepdefinitions/
│   │   └── FolioCreationStepDefinitions.java
│   ├── runners/
│   │   └── FolioTestRunner.java
│   └── utils/
│       └── Constants.java
└── resources/
    ├── features/
    │   └── folio_creation_general_info.feature
    └── serenity.conf
```

## Decisiones de diseño

**CreateFolio Task**:
- Navega a `Constants.BASE_URL` si no está en el dashboard.
- Clic en "+ Nuevo folio" (`DashboardTargets.NEW_FOLIO_BUTTON`).
- Espera a que el modal sea visible.
- Selecciona primer elemento de Suscriptor (`DashboardTargets.SUBSCRIBER_DROPDOWN`).
- Selecciona primer elemento de Agente (`DashboardTargets.AGENT_DROPDOWN`).
- Clic en "Crear folio" (`DashboardTargets.CREATE_FOLIO_BUTTON`).
- Extrae folioNumber de URL activa vía `TheWebPage.currentUrl()`.
- Llama `actor.remember("folioNumber", folioNumber)`.

**CompleteGeneralInfo Task**:
- Llena Razón social con `Constants.TEST_RAZON_SOCIAL`.
- Llena RFC con `Constants.TEST_RFC`.
- Llena Correo con `Constants.TEST_EMAIL`.
- Llena Teléfono con `Constants.TEST_PHONE`.
- Selecciona primera opción de Clasificación de riesgo.
- Selecciona primera opción de Tipo de negocio.
- Clic en "Siguiente →" (`GeneralInfoTargets.NEXT_BUTTON`).

**SectionCompletionStatus Question**:
- Retorna `List<String>` con nombres de secciones que muestran badge "• Completo".
- Assertion verifica que contiene "Asegurado" y "Suscripción".

**WizardStepIndicator Question**:
- Retorna texto del paso activo en el stepper.
- Assertion verifica que contiene "Layout".

**CatalogSetupHook**:
- `@Before(order=1)` global para todos los escenarios del proyecto.
- `GET /v1/subscribers` → vacío → `POST /v1/subscribers` con datos de `Constants`.
- `GET /v1/agents` → vacío → `POST /v1/agents` con datos de `Constants`.
- Usa RestAssured directamente (sin Screenplay WebDriver).

**FolioTestRunner**:
- `@Suite` + `@IncludeEngines("cucumber")`.
- `@SelectClasspathResource("features/folio_creation_general_info.feature")`.
- Plugin: `io.cucumber.core.plugin.SerenityReporterParallel`.

**serenity.conf**:
```
webdriver.driver = chrome
serenity.base.url = "http://localhost:4200"
restapi.base.url = "http://localhost:8080"
```

**build.gradle**: plugin 4.2.34, dependencias de constitución, task `test` finalizada por `aggregate`.

## Complexity Tracking

> Sin violaciones a la constitución. No aplica.
