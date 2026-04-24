# Insurance_Quoter_Auto_Front_Screenplay Development Guidelines

Auto-generated from feature plans. Last updated: 2026-04-24

## Active Technologies

- Java 21 + Serenity BDD 4.2.34 + serenity-screenplay-webdriver 4.2.34 + Selenium 4.33.0 (001-folio-creation-general-info)
- RestAssured 5.3.2 para setup API de precondiciones (002-location-registration)

## Project Structure

```text
src/test/java/com/sofka/automation/
├── hooks/          — @Before hooks (catalog setup, transactional setup)
├── tasks/ui/       — Performable Tasks para acciones UI
├── tasks/api/      — Performable Tasks para setup HTTP (RestAssured)
├── questions/      — Question<T> para estado observable de UI
├── targets/        — Target locators por página
├── stepdefinitions/
├── runners/
└── utils/Constants.java

src/test/resources/
├── features/
└── serenity.conf
```

## Commands

```bash
./gradlew clean test aggregate
```

Reporte en `target/site/serenity/index.html`.

## Code Style

- Java 21: código en inglés, Gherkin en español
- Sin hardcoding: todos los valores en Constants.java
- Sin Thread.sleep: esperas implícitas de Serenity
- Sin pruebas unitarias sobre Tasks/Questions (principio IX)
- GitFlow: features desde develop, merge vía PR, Conventional Commits

## Recent Changes

- 001-folio-creation-general-info: setup inicial Java 21 + Serenity BDD Screenplay WebDriver
- 002-location-registration: LocationsTargets (badge + banner), SetupLocationScenario (Task API), LocationBadgeStatus + BlockingAlertsBanner (Questions), LocationScenarioSetupHook @Before(order=2)

<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
