# Implementation Plan: Registro de Ubicaciones

**Branch**: `002-location-registration` | **Date**: 2026-04-24 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `specs/002-location-registration/spec.md`

## Summary

Implementar el segundo flujo de automatización UI del cotizador: verificar que la
pantalla de Ubicaciones muestra el badge "Incompleta" y el banner de alertas
bloqueantes para una ubicación sin CP, giro ni garantías, mientras que una
ubicación completa no muestra ninguno de estos indicadores. El folio y sus
ubicaciones se crean exclusivamente vía API en un hook `@Before(order=2)`.

## Technical Context

**Language/Version**: Java 21  
**Primary Dependencies**: Serenity BDD 4.2.34, serenity-screenplay-webdriver 4.2.34,
Selenium 4.33.0, Cucumber 7.22.2, RestAssured 5.3.2  
**Storage**: N/A  
**Testing**: N/A (principio IX — sin pruebas sobre el código de automatización)  
**Target Platform**: JVM + Chrome browser, SUT en http://localhost:4200  
**Project Type**: UI test automation (Screenplay pattern)  
**Performance Goals**: Escenario completo (setup API + navegación + verificaciones) < 60s (SC-001)  
**Constraints**: Versiones fijadas por constitución; máximo 5 steps Gherkin; sin Thread.sleep  
**Scale/Scope**: 1 feature file, 1 escenario, 1 Task API, 1 Hook, 2 Questions, 1 Target class

## Constitution Check

*GATE: Debe pasar antes de iniciar la implementación.*

| Principio | Estado | Evidencia |
|-----------|--------|-----------|
| I. Screenplay Pattern | ✅ PASS | `SetupLocationScenario` implementa `Performable`; `LocationBadgeStatus` y `BlockingAlertsBanner` implementan `Question<T>`; localizadores en `LocationsTargets` |
| II. Bounded Scope | ✅ PASS | Flow 002 dentro del alcance de 3 flujos definidos; sin POM |
| III. Declarative Gherkin | ✅ PASS | ≤5 steps en español, lenguaje de negocio, sin referencias técnicas |
| IV. Test Data Independence | ✅ PASS | Catálogos: `CatalogSetupHook` @Before(order=1); folio+ubicaciones: `LocationScenarioSetupHook` @Before(order=2) vía API |
| V. Separation of Concerns | ✅ PASS | StepDefs orquestan; Task API configura datos; Questions extraen estado |
| VI. Language Convention | ✅ PASS | Código Java en inglés; Gherkin y docs en español |
| VII. No Hardcoding | ✅ PASS | Nuevas constantes en `Constants.java` (CP, giro, garantía) |
| VIII. GitFlow | ✅ PASS | Feature branch `002-location-registration` desde develop, merge vía PR |
| IX. No Self-Testing | ✅ PASS | Sin clases de test sobre Tasks ni Questions |
| X. Pinned Dependencies | ✅ PASS | Versiones exactas sin modificar desde feature 001 |
| XI. No Overengineering | ✅ PASS | 1 Task API, 2 Questions, 1 Target class — mínimo necesario |

## Project Structure

### Documentación (este feature)

```text
specs/002-location-registration/
├── spec.md
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── location-setup-api.md
└── checklists/
    └── requirements.md
```

### Código fuente (raíz del repositorio)

Archivos **nuevos** para feature 002 (los existentes de feature 001 no se modifican):

```text
src/test/java/com/sofka/automation/
├── hooks/
│   ├── CatalogSetupHook.java            ← ya existe; NO modificar
│   └── LocationScenarioSetupHook.java   ← NUEVO
├── tasks/
│   ├── ui/                              ← ya existe; NO agregar aquí
│   └── api/
│       └── SetupLocationScenario.java   ← NUEVO
├── questions/
│   ├── SectionCompletionStatus.java     ← ya existe; NO modificar
│   ├── WizardStepIndicator.java         ← ya existe; NO modificar
│   ├── LocationBadgeStatus.java         ← NUEVO
│   └── BlockingAlertsBanner.java        ← NUEVO
├── targets/
│   ├── DashboardTargets.java            ← ya existe; NO modificar
│   ├── GeneralInfoTargets.java          ← ya existe; NO modificar
│   └── LocationsTargets.java            ← NUEVO
├── stepdefinitions/
│   ├── FolioCreationStepDefinitions.java ← ya existe; NO modificar
│   └── LocationRegistrationStepDefinitions.java ← NUEVO
├── runners/
│   ├── FolioTestRunner.java             ← ya existe; NO modificar
│   └── LocationTestRunner.java          ← NUEVO
└── utils/
    └── Constants.java                   ← MODIFICAR: agregar 6 constantes nuevas

src/test/resources/
└── features/
    ├── folio_creation_general_info.feature ← ya existe; NO modificar
    └── location_registration.feature       ← NUEVO
```

## Decisiones de diseño

### `Constants.java` — Nuevas constantes

```java
public static final String TEST_LOCATION_ZIP_CODE = "06600";
public static final String TEST_LOCATION_BL_CODE = "OFICINAS";
public static final String TEST_LOCATION_BL_FIRE_KEY = "1110";
public static final String TEST_LOCATION_GUARANTEE_CODE = "INCENDIO";
public static final int TEST_LOCATION_INSURED_VALUE = 1_000_000;
public static final String LOCATIONS_URL_TEMPLATE = "/cotizador/quotes/%s/locations";
```

---

### `LocationScenarioSetupHook` — Hook `@Before(order=2)`

- Anotado con `@Before(order=2, value="@location-registration")` para ejecutarse solo en los escenarios de este feature.
- Llama `actor.attemptsTo(SetupLocationScenario.forCurrentActor())`.
- El actor que se usa es el mismo configurado en `@Before(order=10)` del step def (o se crea uno temporal si el hook necesita uno antes).

**Nota sobre el actor en hooks**: El hook usa `OnStage.theActorCalled("agent")` si el stage ya fue configurado, o inicializa el actor directamente con las habilidades RestAssured si no.

---

### `SetupLocationScenario` — Task API

Implementa `Performable`. Secuencia de 3 llamadas RestAssured:

1. `POST /v1/folios` con `subscriberId` y `agentCode` → extrae `id`, `folioNumber`
2. `PUT /v1/folios/{id}/general-info` con datos básicos del asegurado
3. `PUT /v1/quotes/{folioNumber}/locations` con lista de 2 ubicaciones:
   - Ubicación 1 (índice 1): `zipCode="06600"`, `businessLine={code, fireKey}`, `guarantees=[{code, insuredValue}]`
   - Ubicación 2 (índice 2): `zipCode=""`, `businessLine=null`, `guarantees=[]`

Al finalizar:
- `actor.remember("folioNumber", folioNumber)`
- `actor.remember("folioId", folioId)`
- Si alguna llamada retorna != 2xx: lanza `IllegalStateException` con mensaje descriptivo.

---

### `LocationsTargets` — Target locators

```java
// Badge por índice de ubicación — XPath parametrizable
// Uso: LocationsTargets.badgeForIndex(2)
public static Target badgeForIndex(int index) {
    return Target.the("badge for location " + index)
        .locatedBy("//table[contains(@class,'locations-table')]"
            + "//tbody/tr[td[2][normalize-space(text())='" + index + "']]"
            + "//span[contains(@class,'badge')]");
}

// Banner de alertas bloqueantes
public static final Target BLOCKING_ALERTS_BANNER = Target.the("blocking alerts banner")
    .located(By.cssSelector("div.alert-banner.alert-banner--warn"));

// Botón "Siguiente →" de navegación
public static final Target NEXT_BUTTON = Target.the("siguiente button")
    .located(By.xpath("//div[contains(@class,'page-nav')]//button[contains(normalize-space(.),'Siguiente')]"));
```

---

### `LocationBadgeStatus` — Question<String>

```java
public static LocationBadgeStatus forLocationIndex(int index)
```

- Usa `Serenity.webdriverFor(actor)` (o `BrowseTheWeb.as(actor)`) para encontrar el badge.
- Retorna el texto visible del span (`getText()` trimmed).
- Si el elemento no existe: retorna `""` para que la assertion falle con mensaje claro.

---

### `BlockingAlertsBanner` — Question<Boolean>

- Verifica si `LocationsTargets.BLOCKING_ALERTS_BANNER` está presente y visible.
- Retorna `true` si visible, `false` si ausente o no visible.

---

### `location_registration.feature`

```gherkin
# language: es

Característica: Registro de ubicaciones con estado incompleto y completo
  Como agente del cotizador
  Quiero ver el estado de cada ubicación en la pantalla de Ubicaciones
  Para identificar cuáles requieren corrección antes de tarificar

  @location-registration
  Escenario: Verificar badge incompleto y alerta bloqueante para ubicación sin datos
    Dado que el agente navega a la pantalla de ubicaciones del folio
    Cuando el agente visualiza la segunda ubicación sin datos
    Entonces la segunda ubicación muestra badge "Incompleta"
    Y aparece un banner de alertas bloqueantes en la pantalla
    Y el agente puede navegar al siguiente paso del wizard
```

Máximo 5 steps — ✅

---

### `LocationRegistrationStepDefinitions`

- `@Before(order=10)`: configura `OnStage` con `Cast.whereEveryoneCanBrowseTheWeb()`.
- `@After`: llama `Serenity.drawTheCurtain()`.
- Step "navega a la pantalla de ubicaciones": `Open.url(String.format(Constants.LOCATIONS_URL_TEMPLATE, actor.recall("folioNumber")))`.
- Step "visualiza la segunda ubicación": no hace nada (estado ya visible al cargar la página).
- Step "badge Incompleta": `actor.should(seeThat(LocationBadgeStatus.forLocationIndex(2), equalTo("Incompleta")))`.
- Step "banner de alertas": `actor.should(seeThat(BlockingAlertsBanner.isVisible(), is(true)))`.
- Step "navegar al siguiente paso": `actor.should(seeThat(LocationBadgeStatus.forLocationIndex(1), not(equalTo("Incompleta"))))` + click en `LocationsTargets.NEXT_BUTTON`.

---

### `LocationTestRunner`

```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/location_registration.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.sofka.automation.stepdefinitions,com.sofka.automation.hooks")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "io.cucumber.core.plugin.SerenityReporterParallel")
public class LocationTestRunner {}
```

---

## Complexity Tracking

> Sin violaciones a la constitución. No aplica.
