<!--
=== Sync Impact Report ===
Version change: 0.0.0 → 1.0.0
Added principles:
  - I. Screenplay Pattern
  - II. Bounded Scope
  - III. Declarative Gherkin
  - IV. Test Data Independence
  - V. Separation of Concerns
  - VI. Language Convention
  - VII. No Hardcoding
  - VIII. GitFlow
  - IX. No Self-Testing
  - X. Pinned Dependencies
  - XI. No Overengineering
Added sections:
  - Technology Stack
  - Expected Deliverables
Removed sections: none
Templates requiring updates:
  - .specify/templates/plan-template.md ✅ aligned (Constitution Check gate references these principles)
  - .specify/templates/spec-template.md ✅ aligned (declarative Gherkin, Spanish language requirement)
  - .specify/templates/tasks-template.md ✅ aligned (tests OPTIONAL note matches No Self-Testing principle)
Follow-up TODOs: none
===========================
-->

# AUTO_FRONT_SCREENPLAY Constitution

## Core Principles

### I. Screenplay Pattern

Toda la automatización MUST seguir el patrón Screenplay de Serenity BDD:

- Cada **Task** MUST implementar `Performable` y encapsular exactamente una
  acción de negocio del usuario sobre la UI, o exactamente una operación HTTP
  de setup de datos (ver principio IV).
- Cada **Question** MUST implementar `Question<T>` y extraer exactamente un
  dato observable de la UI.
- El **Actor** interactúa con el sistema exclusivamente a través de Tasks y
  Questions. Ningún otro componente toca WebDriver ni HTTP directamente.
- Los localizadores de elementos MUST vivir en clases **Target** separadas
  por página o sección (ej. `DashboardTargets`, `LocationsTargets`). NEVER
  inline en Tasks ni en StepDefinitions.

### II. Bounded Scope

Este repositorio cubre exactamente **3 flujos críticos de UI**:

- `001-folio-creation-and-general-info` — creación de folio y datos generales (full UI)
- `002-locations-with-incomplete-alert` — registro de ubicaciones completa e incompleta con alerta (UI)
- `003-premium-calculation-and-results` — coberturas, cálculo y desglose de prima (UI)

No se MUST agregar flujos adicionales ni escenarios de error independientes
fuera de estos tres. No se MUST usar Page Object Model ni Page Factory.

### III. Declarative Gherkin

Los archivos `.feature` MUST escribirse en Gherkin declarativo en español:

- Máximo **5 steps por escenario**.
- Un step = una acción de negocio completa. NEVER una interacción atómica
  de UI (click, sendKeys, findElement, esperar N segundos).
- Los steps NO MUST exponer CSS selectors, IDs de elementos, rutas de URL,
  verbos HTTP, ni nombres literales de botones.
- Correcto: `Cuando el agente registra una ubicación completa`
- Incorrecto: `Cuando el agente hace clic en el botón "+ Añadir ubicación"`

### IV. Test Data Independence

Ningún escenario MUST depender de datos preexistentes en la base de datos:

- **Capa 1 — Catálogos**: hook `@Before(order=1)` global verifica o crea
  vía API los datos maestros mínimos (suscriptores, agentes, giros con clave
  incendio, CP 06600, garantías tarifables) antes de arrancar cualquier
  escenario.
- **Capa 2 — Datos transaccionales**: cada escenario crea sus propios
  folios y ubicaciones. Flow 001 los crea por UI (eso es lo que prueba).
  Flows 002 y 003 los crean por API en `@Before(order=2)` para no repetir
  cobertura ya validada en flujos anteriores.
- El folio generado MUST propagarse entre steps con `actor.remember` /
  `actor.recall`. NEVER con variables estáticas ni campos de instancia.

### V. Separation of Concerns

La responsabilidad de cada capa MUST estar estrictamente delimitada:

- **StepDefinitions**: orquestación pura. NO MUST contener lógica de UI,
  construcción de requests ni validaciones directas.
- **Tasks UI** (`tasks/ui/`): acciones de usuario sobre la interfaz.
- **Tasks API setup** (`tasks/api/`): operaciones HTTP con RestAssured para
  establecer estado previo al test. Sin WebDriver.
- **Questions** (`questions/`): extracción de un único dato observable de UI.
- **Targets** (`targets/`): constantes `Target` agrupadas por página.
- **Hooks** (`hooks/`): lógica `@Before` para setup de datos (Capas 1 y 2).
- **Constants** (`utils/Constants.java`): todos los valores literales del
  proyecto (ver principio VII).

### VI. Language Convention

- Todo el código (clases, métodos, variables, constantes, paquetes) MUST
  estar en **inglés**.
- Gherkin, specs, documentación y comentarios MUST estar en **español**.
- Sin excepciones en ninguna dirección.

### VII. No Hardcoding

- Ningún valor literal (URLs, datos de prueba, códigos de catálogo, sumas
  aseguradas, timeouts, porcentajes) MUST aparecer inline en Tasks, Questions,
  StepDefinitions ni Hooks.
- Todos los valores constantes MUST declararse en
  `src/test/java/com/sofka/automation/utils/Constants.java`.
- Ejemplos: `Constants.BASE_URL`, `Constants.TEST_ZIP_CODE`,
  `Constants.FIRE_GUARANTEE_AMOUNT`, `Constants.BACKEND_BASE_URL`.

### VIII. GitFlow

- Rama de integración: **develop**. Toda feature parte de `develop`.
- Nomenclatura: `feature/<nombre-descriptivo>`.
- Merge a `develop` exclusivamente vía **Pull Request**. Sin push directo.
- `main` recibe únicamente merges desde `release/*` o `hotfix/*`.
- Commits en formato **Conventional Commits**:
  `feat:`, `fix:`, `chore:`, `docs:`, `refactor:`, `test:`.

### IX. No Self-Testing

- Este repositorio NO MUST contener pruebas unitarias sobre el código de
  automatización (Tasks, Questions, Targets, Hooks, Constants).
- La validación del código ocurre ejecutando los escenarios Cucumber contra
  el sistema real bajo prueba.
- No se MUST crear clases de test para verificar Tasks ni Questions.

### X. Pinned Dependencies

Las versiones están fijadas y NO MUST modificarse salvo incompatibilidad
demostrada y documentada:

| Dependencia | Versión |
|---|---|
| Java | 21 |
| Gradle plugin `net.serenity-bdd.serenity-gradle-plugin` | 4.2.34 |
| serenity-core | 4.2.34 |
| serenity-cucumber | 4.2.34 |
| serenity-screenplay | 4.2.34 |
| serenity-screenplay-webdriver | 4.2.34 |
| selenium-java | 4.33.0 |
| cucumber-junit-platform-engine | 7.22.2 |
| junit-platform-suite | 1.12.2 |
| junit-jupiter | 5.12.2 |
| assertj-core | 3.27.3 |

### XI. No Overengineering

- La arquitectura MUST ser la mínima suficiente para los 3 flujos requeridos.
- NO MUST crearse helpers, utilidades genéricas ni abstracciones para
  operaciones de un solo uso.
- NO MUST usarse `Thread.sleep`; usar esperas implícitas de Serenity WebDriver.

## Technology Stack

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Build tool | Gradle con `net.serenity-bdd.serenity-gradle-plugin` 4.2.34 |
| Framework | Serenity BDD 4.2.34 |
| Patrón | Screenplay (Tasks + Questions + Targets + Actor) |
| WebDriver | serenity-screenplay-webdriver 4.2.34 + Selenium 4.33.0 |
| HTTP setup | RestAssured (directo, en Tasks API y Hooks) |
| Runner | Cucumber 7.22.2 + `cucumber-junit-platform-engine` 7.22.2 |
| Engine | JUnit Platform Suite 1.12.2 + JUnit Jupiter 5.12.2 |
| Aserciones | AssertJ 3.27.3 |
| Browser | Chrome (`webdriver.driver=chrome` en `serenity.conf`) |
| SUT | cotizador-danos-web Angular 19 en `http://localhost:4200` |
| Backend | plataforma-danos-back en `http://localhost:8080` |

## Expected Deliverables

- Proyecto ejecutable con `gradle clean test aggregate`.
- Reporte Serenity generado en `target/site/serenity/`.
- `README.md` con instrucciones de ejecución y requisitos de ambiente.

## Governance

- Esta constitución es el documento rector del repositorio. Toda decisión de
  diseño o implementación MUST ser consistente con los principios aquí definidos.
- Enmiendas requieren: justificación documentada, actualización de versión
  semántica y revisión de impacto en templates dependientes.
- Política de versionado:
  - MAJOR: eliminación o redefinición incompatible de principios existentes.
  - MINOR: adición de principios o expansión material de guías.
  - PATCH: correcciones de redacción, typos, refinamientos no semánticos.
- Todo código entregado MUST verificarse contra estos principios antes de
  considerarse completo.

**Version**: 1.0.0 | **Ratified**: 2026-04-24 | **Last Amended**: 2026-04-24
