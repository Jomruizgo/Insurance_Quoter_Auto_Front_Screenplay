# Implementation Plan: Coberturas y Cálculo de Prima

**Branch**: `003-coverage-premium-calc` | **Date**: 2026-04-24 | **Spec**: [spec.md](spec.md)

## Summary

Tercer flujo de automatización UI: el agente activa la cobertura COV-FIRE en la
ubicación completa desde la pantalla de coberturas (`/technical-info`), guarda, y
ejecuta el cálculo en la pantalla de cálculo (`/calculation`). Se verifican prima
neta y prima comercial positivas para la ubicación completa, y estado "No calculable"
para la ubicación incompleta. El folio y sus dos ubicaciones se crean vía API en el
`@Before` hook; la activación de cobertura es la acción UI bajo prueba.

## Technical Context

**Language/Version**: Java 21  
**Primary Dependencies**: Serenity BDD 4.2.34, serenity-screenplay-webdriver 4.2.34,
Selenium 4.33.0, Cucumber 7.22.2, RestAssured 5.3.2  
**Storage**: N/A  
**Testing**: N/A (principio IX)  
**Target Platform**: JVM + Chrome, SUT en http://localhost:4200  
**Project Type**: UI test automation (Screenplay)  
**Performance Goals**: Escenario completo < 90s (setup API + navegación + activación + cálculo)  
**Constraints**: Versiones fijadas; máximo 5 steps Gherkin; sin Thread.sleep  
**Scale/Scope**: 1 feature file, 2 escenarios, 1 Task API, 3 Tasks UI, 3 Questions, 2 Target classes

## Constitution Check

*GATE: Debe pasar antes de iniciar la implementación.*

| Principio | Estado | Evidencia |
|-----------|--------|-----------|
| I. Screenplay Pattern | ✅ PASS | Tasks UI/API implementan `Performable`; Questions implementan `Question<T>`; localizadores en Target classes |
| II. Bounded Scope | ✅ PASS | Flow 003 `003-premium-calculation-and-results` — dentro del alcance definido en constitución; sin POM |
| III. Declarative Gherkin | ✅ PASS | ≤5 steps en español, acciones de negocio, sin referencias técnicas |
| IV. Test Data Independence | ✅ PASS | Catálogos: `CatalogSetupHook` @Before(order=1) existente; folio+ubicaciones: `CoveragePremiumSetupHook` @Before(order=20) vía API |
| V. Separation of Concerns | ✅ PASS | StepDefs orquestan; Task API configura folio; Tasks UI activan cobertura, guardan, calculan; Questions extraen prima/estado |
| VI. Language Convention | ✅ PASS | Código Java en inglés; Gherkin y docs en español |
| VII. No Hardcoding | ✅ PASS | Constantes nuevas en `Constants.java` (URLs, código cobertura) |
| VIII. GitFlow | ✅ PASS | Feature branch `003-coverage-premium-calc` desde develop, merge vía PR |
| IX. No Self-Testing | ✅ PASS | Sin clases de test sobre Tasks ni Questions |
| X. Pinned Dependencies | ✅ PASS | Versiones sin cambio; RestAssured 5.3.2 ya en build.gradle |
| XI. No Overengineering | ✅ PASS | 3 Tasks UI, 3 Questions, 2 Target classes — mínimo necesario para 2 escenarios |

## Project Structure

### Documentación (este feature)

```text
specs/003-coverage-premium-calc/
├── spec.md
├── plan.md              ← este archivo
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   ├── coverage-options-api.md
│   └── calculation-api.md
└── checklists/
    └── requirements.md
```

### Código fuente — archivos NUEVOS para feature 003

Los archivos de features 001 y 002 NO se modifican salvo `Constants.java`.

```text
src/test/java/com/sofka/automation/
├── hooks/
│   └── CoveragePremiumSetupHook.java        ← NUEVO @Before(order=20, "@premium-calculation")
├── tasks/
│   ├── api/
│   │   └── SetupCoveragePremiumScenario.java ← NUEVO (igual que SetupLocationScenario)
│   └── ui/
│       ├── ActivateCoverageForLocation.java  ← NUEVO
│       ├── SaveCoverages.java                ← NUEVO
│       └── ExecuteCalculation.java           ← NUEVO
├── questions/
│   ├── NetPremiumValue.java                  ← NUEVO Question<String>
│   ├── CommercialPremiumValue.java           ← NUEVO Question<String>
│   └── LocationCalculationStatus.java        ← NUEVO Question<String>
├── targets/
│   ├── CoveragesTargets.java                 ← NUEVO (pantalla /technical-info)
│   └── CalculationTargets.java               ← NUEVO (pantalla /calculation)
├── stepdefinitions/
│   └── PremiumCalculationStepDefinitions.java ← NUEVO
├── runners/
│   └── PremiumCalculationTestRunner.java     ← NUEVO
└── utils/
    └── Constants.java                        ← MODIFICAR: agregar constantes 003

src/test/resources/
└── features/
    └── premium_calculation.feature           ← NUEVO
```

### Selectores UI (fuente: Angular templates inspeccionados)

**Pantalla `/technical-info` — CoveragesTargets:**

| Target | Tipo | Selector |
|--------|------|----------|
| `COVERAGE_TOGGLE_BY_CODE(code)` | XPath dinámico | `//div[contains(@class,'coverage-card')][.//code[contains(@class,'coverage-card__code') and normalize-space()='{code}']]//app-switch` |
| `SAVE_COVERAGES_BUTTON` | XPath | `//div[contains(@class,'technical-info-page__nav')]//button[normalize-space(.)='Guardar coberturas']` |
| `COVERAGES_GRID` | CSS | `app-coverage-options-grid` |
| `LOCATION_TAB_SELECTOR` | CSS | `app-location-tab-selector` |

**Pantalla `/calculation` — CalculationTargets:**

| Target | Tipo | Selector |
|--------|------|----------|
| `EXECUTE_CALCULATION_BUTTON` | XPath | `//button[normalize-space(.)='Ejecutar cálculo']` |
| `NET_PREMIUM_VALUE` | CSS | `.premium-summary__card--dark .premium-summary__card-value` |
| `COMMERCIAL_PREMIUM_VALUE` | CSS | `.premium-summary__card--brand .premium-summary__card-value` |
| `PREMIUM_SUMMARY` | CSS | `.premium-summary` |
| `NO_CALCULABLE_BADGE` | XPath | `//li[contains(@class,'premium-summary__location-item')]//app-badge[normalize-space(.)='No calculable']` |

### Constantes nuevas en Constants.java

```java
public static final String COVERAGE_CODE_FIRE              = "COV-FIRE";
public static final String TECHNICAL_INFO_URL_TEMPLATE     = "/cotizador/quotes/%s/technical-info";
public static final String CALCULATION_URL_TEMPLATE        = "/cotizador/quotes/%s/calculation";
```

### Gherkin (premium_calculation.feature)

```gherkin
# language: es

Característica: Coberturas y cálculo de prima
  Como agente del cotizador
  Quiero activar coberturas y calcular la prima
  Para obtener el desglose de prima neta y comercial

  @premium-calculation
  Escenario: Verificar prima calculada para ubicación completa con cobertura de incendio
    Dado que el agente navega a la pantalla de coberturas del folio
    Cuando el agente activa la cobertura de incendio en la ubicación completa y guarda
    Y el agente ejecuta el cálculo de prima
    Entonces la prima neta es mayor a cero
    Y la prima comercial es mayor a cero

  @premium-calculation
  Escenario: Verificar estado no calculable para ubicación incompleta
    Dado que el agente navega a la pantalla de coberturas del folio
    Cuando el agente activa la cobertura de incendio en la ubicación completa y guarda
    Y el agente ejecuta el cálculo de prima
    Entonces la ubicación incompleta aparece como "No calculable"
```

## Complejidad — sin violaciones de constitución

N/A — todos los principios pasan sin excepciones.
