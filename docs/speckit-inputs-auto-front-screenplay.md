# Inputs para SpecKit — Auto_Front_Screenplay

Referencia de los textos a pegar en cada comando de SpecKit al inicializar
el proyecto `Auto_Front_Screenplay` de automatización de UI.

Orden de ejecución:
```
speckit constitution
speckit specify   # correr 3 veces (una por feature)
speckit plan      # correr 3 veces (una por spec generada)
```

---

## Estrategia de datos de prueba

Antes de leer los inputs, entender la estrategia de dos capas que aplica
a todos los escenarios:

```
Capa 1 — Datos maestros / catálogos (hook @Before global)
  Responsabilidad: verificar que los catálogos requeridos existen en la BD.
  Si no existen → crearlos vía API antes de que arranque cualquier escenario.
  Datos mínimos: al menos 1 suscriptor, 1 agente, 1 giro con clave incendio,
  CP 06600 con colonias asociadas, al menos 1 garantía tarifable.
  Implementación: clase CatalogSetupHook con @Before(order=1).

Capa 2 — Datos transaccionales (por escenario)
  Cada escenario crea sus propios folios y ubicaciones; no asume ningún
  folio preexistente en la BD.

  Flow 001: folio + datos generales creados por UI (ESO es lo que prueba).
  Flow 002: folio + datos generales + layout creados por API (@Before),
            luego UI para registrar y verificar ubicaciones.
  Flow 003: folio + datos generales + layout + ubicaciones creados por API
            (@Before), luego UI para coberturas + cálculo + resultados.
```

Esta separación garantiza que cada escenario prueba el comportamiento
de UI que le corresponde y no falla por estado sucio de BD.

---

## 1. `speckit constitution`

```
Proyecto: AUTO_FRONT_SCREENPLAY

Repositorio de automatización end-to-end de la SPA cotizador-danos-web
(Angular 19, http://localhost:4200) del sistema Insurance Quoter.
Tecnología: Java 21 + Serenity BDD 4.2.34 + serenity-screenplay-webdriver
4.2.34 + Selenium 4.33.0 + Cucumber 7.22.2.

El proyecto es hermano de AUTO_API_SCREENPLAY (que cubre endpoints REST)
y sigue la misma disciplina de patrón Screenplay adaptada a UI.

Principios obligatorios:

I. Screenplay Pattern
- Cada Task implementa Performable y encapsula exactamente una acción
  de negocio de usuario sobre la UI, o exactamente una operación HTTP de
  setup (ver principio V). No ejecuta validaciones.
- Cada Question implementa Question<T> y extrae exactamente un dato
  observable de la UI.
- El Actor interactúa con el sistema exclusivamente a través de Tasks y
  Questions; ningún otro componente toca WebDriver ni HTTP directamente.
- Los localizadores de elementos DEBEN vivir en clases Target separadas
  por página o sección; nunca inline en Tasks ni en StepDefinitions.

II. Bounded Scope
- El repositorio cubre exactamente 3 flujos críticos:
    001 — Creación de folio y captura de datos generales (full UI)
    002 — Registro de ubicaciones: completa e incompleta con alerta (UI)
    003 — Configuración de coberturas, cálculo y desglose de prima (UI)
- Sin flujos adicionales ni escenarios de error independientes.
- Sin Page Object Model ni Page Factory.

III. Declarative Gherkin (máximo 5 steps por escenario)
- Gherkin en español, código en inglés.
- Un step = una acción de negocio completa. Prohibido exponer clicks,
  selects, CSS selectors, IDs, rutas de URL, verbos HTTP.
- Cada escenario cubre exactamente un comportamiento observable del sistema.

IV. Test Data Independence
- Ningún escenario depende de datos preexistentes en la base de datos.
- Dos capas de setup:
    Capa 1: hook @Before(order=1) global verifica o crea datos maestros
             (catálogos) vía API antes de arrancar cualquier escenario.
    Capa 2: cada escenario crea sus propios datos transaccionales
             (folios, ubicaciones) ya sea por UI o por API según su alcance.
- Los datos transaccionales creados por API en la Capa 2 usan Tasks de
  setup que llaman a RestAssured directamente (sin Screenplay WebDriver).
- El folio generado se propaga entre steps usando actor.remember /
  actor.recall; nunca variables estáticas ni campos de instancia.

V. Separation of Concerns
- StepDefinitions: orquestación pura. Sin lógica de UI ni HTTP.
- Tasks UI (tasks/ui/): acciones de usuario sobre la interfaz.
- Tasks API setup (tasks/api/): operaciones HTTP para establecer estado
  previo al test de UI. Usan RestAssured directamente.
- Hooks (hooks/): @Before para setup de catálogos y datos transaccionales.
- Questions (questions/): extracción de un dato observable de la UI.
- Targets (targets/): constantes Target agrupadas por página.

VI. Code Clarity
- Código en inglés. Gherkin y documentación en español.
- Nombres semánticos y autoexplicativos. Sin código comentado.

VII. Pinned Dependencies

| Componente                           | Versión |
|--------------------------------------|---------|
| Java                                 | 21      |
| Gradle plugin serenity-gradle-plugin | 4.2.34  |
| serenity-core                        | 4.2.34  |
| serenity-cucumber                    | 4.2.34  |
| serenity-screenplay                  | 4.2.34  |
| serenity-screenplay-webdriver        | 4.2.34  |
| selenium-java                        | 4.33.0  |
| cucumber-junit-platform-engine       | 7.22.2  |
| junit-platform-suite                 | 1.12.2  |
| junit-jupiter                        | 5.12.2  |
| assertj-core                         | 3.27.3  |

VIII. No Overengineering
- Arquitectura mínima para los 3 flujos requeridos.
- Sin helpers ni utilidades genéricas para operaciones de un solo uso.
- Sin escenarios de error adicionales fuera del alcance de cada spec.

Stack:
- Lenguaje: Java 21 | Build: Gradle con serenity-gradle-plugin 4.2.34
- Framework: Serenity BDD 4.2.34
- Patrón: Screenplay (Tasks + Questions + Targets + Actor)
- WebDriver: serenity-screenplay-webdriver 4.2.34 + Selenium 4.33.0
- HTTP setup: RestAssured (directo, en Tasks API y Hooks)
- Runner: Cucumber 7.22.2 + cucumber-junit-platform-engine 7.22.2
- Engine: JUnit Platform Suite 1.12.2 + JUnit Jupiter 5.12.2
- Aserciones: AssertJ 3.27.3
- Browser: Chrome (webdriver.driver=chrome en serenity.conf)

Entregables:
- Proyecto ejecutable: gradle clean test aggregate
- Reporte Serenity: target/site/serenity/
- README.md con pasos de ejecución y requisitos de ambiente
```

---

## Contexto de UI real

Observaciones del frontend real que los inputs de `speckit specify`
describen y que las Tasks deben implementar.

### Flujo wizard de 5 pasos
`Datos generales → Layout → Ubicaciones → Coberturas → Cálculo`

### Paso 0 — Dashboard y creación de folio
- Página `/cotizador`, "Panel de folios"
- Botón "+ Nuevo folio" abre modal con dos dropdowns: Suscriptor + Agente
- Botón "Crear folio" genera número de folio (`FOL-YYYY-NNNNN`) y redirige al wizard

### Paso 1 — Datos generales
- Sección "Asegurado": Razón social, RFC, Correo de contacto, Teléfono
- Sección "Suscripción": Suscriptor (precargado del modal), Agente (precargado),
  Clasificación de riesgo (dropdown), Tipo de negocio (dropdown)
- Badge "• Completo" por sección cuando todos los campos requeridos están llenos
- Botones: "← Anterior" | "Guardar borrador" | "Siguiente →"

### Paso 2 — Layout
- Número de ubicaciones (numérico) + Tipo: "Ubicación única" / "Múltiples ubicaciones"
- Botones: "← Anterior" | "Guardar borrador" | "Siguiente →"

### Paso 3 — Ubicaciones
- Tabla: #, Nombre, Dirección·CP, Giro, Construcción, Suma asegurada, Estado
- Botón "+ Añadir ubicación" abre drawer lateral con 4 tabs:
  - Datos básicos: Nombre, Dirección, CP (autocompleta Estado/Municipio/Ciudad/Colonia)
  - Construcción: Tipo constructivo (radio), Niveles, Año
  - Giro: dropdown de catálogo (asociado a clave incendio)
  - Garantías: checkboxes con monto (GUA-FIRE, GUA-CONT, GUA-THEFT, GUA-GLASS,
    GUA-ELEC, GUA-CASH)
- Footer del drawer: tags de alertas activas ("CP faltante", "Clave incendio
  faltante", "Sin garantías activas") + botón "Guardar ubicación"
- Badge en tabla: "Completa" (verde) o "Incompleta (N)" (rojo)
- Banner amarillo cuando hay alertas bloqueantes activas

### Paso 4 — Coberturas
- Tabs por ubicación (UBIC 01, UBIC 02...)
- Coberturas con toggle + Deducible (%) + Coaseguro (%):
  COV-FIRE (Incendio), COV-CAT (Catastrófica), COV-BI (Interrupción de negocio)
- Botones: "← Anterior" | "Guardar coberturas" | "Siguiente →"

### Paso 5 — Cálculo
- Antes de ejecutar: conteo "N calculables / N con alertas" + botón "Ejecutar cálculo"
- Después de ejecutar (sin recarga de página):
  - Card "PRIMA NETA" con valor numérico
  - Card "PRIMA COMERCIAL" con valor numérico
  - Card "POR UBICACIÓN": prima numérica o badge "No calculable" por ubicación
  - Sección "Ubicaciones no calculadas" con razones ("CP requerido",
    "Clave incendio requerida", "Se requiere garantía tarifable")
  - Botón "Continuar a términos y condiciones"

---

## 2. `speckit specify` — Feature 001

Nombre: `001-folio-creation-and-general-info`

```
Automatización del flujo de creación de folio y captura de datos generales
en la SPA cotizador-danos-web. Este escenario prueba la UI de principio a
fin: desde el dashboard hasta completar el primer paso del wizard.

Estrategia de datos:
- Capa 1 (hook @Before global): verificar o crear vía API al menos 1
  suscriptor y 1 agente en los catálogos. Si existen, no se recrean.
- Capa 2 (este escenario): el folio se crea por UI — eso es exactamente
  lo que se está probando.

Escenario Gherkin (declarativo, máximo 5 steps):

  Scenario: El agente crea un folio y registra los datos generales del asegurado
    Given el agente está en el panel de cotizaciones
    When crea un nuevo folio con un suscriptor y agente disponibles
    And completa los datos del asegurado y la suscripción
    Then ambas secciones muestran estado completo
    And el folio aparece en la barra de progreso del wizard

Flujo de UI que respalda cada step:

  When "crea un nuevo folio...":
    Task CreateFolio:
      - Hace clic en "+ Nuevo folio"
      - Selecciona primer elemento disponible en dropdown Suscriptor
      - Selecciona primer elemento disponible en dropdown Agente
      - Hace clic en "Crear folio"
      - Extrae número de folio de la URL o del DOM y llama actor.remember("folioNumber")

  And "completa los datos del asegurado...":
    Task CompleteGeneralInfo:
      - Llena Razón social, RFC, Correo, Teléfono con valores fijos de prueba
      - Selecciona primera opción en Clasificación de riesgo
      - Selecciona primera opción en Tipo de negocio
      - Hace clic en "Siguiente →"

  Then "ambas secciones muestran estado completo":
    Question SectionStatus: verifica presencia de badge "• Completo" en ambas secciones

  And "el folio aparece en la barra de progreso":
    Question WizardStep: verifica que el stepper indica paso 2 (Layout) activo

Supuestos:
- Backend http://localhost:8080 y core http://localhost:8081 corriendo.
- Los catálogos tienen al menos un suscriptor y un agente (garantizado por Capa 1).
- Sin autenticación requerida.
- Valores de prueba hardcodeados en CompleteGeneralInfo (RFC ECO860313AB2,
  razón social "Empresa Prueba SA de CV", etc.). Sin Scenario Outline.
```

---

## 3. `speckit specify` — Feature 002

Nombre: `002-locations-with-incomplete-alert`

```
Automatización del comportamiento de la UI al registrar una ubicación
incompleta en la sección Ubicaciones de riesgo. El escenario prueba que
el sistema muestra alertas bloqueantes en la UI sin impedir continuar.

Estrategia de datos:
- Capa 1 (hook @Before global): verificar o crear vía API al menos 1 giro
  con clave incendio válida, CP 06600 con colonias, y garantías tarifables.
- Capa 2 (hook @Before de este feature): crear vía API el estado previo
  necesario para que el test empiece directamente en la sección de Ubicaciones:
    POST /v1/folios                           → obtener folioNumber
    PUT  /v1/quotes/{folio}/general-info      → datos mínimos válidos
    PUT  /v1/quotes/{folio}/locations/layout  → { numberOfLocations: 2 }
  El folio creado se guarda con actor.remember("folioNumber").
  El browser se abre directamente en /quotes/{folio}/locations.
- Por qué API para el setup: crear folio + general info + layout por UI es
  comportamiento ya cubierto en flow 001. Repetirlo aquí agrega tiempo de
  ejecución sin agregar cobertura nueva.

Escenario Gherkin (declarativo, máximo 5 steps):

  Scenario: El sistema alerta sobre ubicación incompleta sin bloquear el folio
    Given existe un folio con layout configurado para 2 ubicaciones
    When el agente registra una ubicación completa y una sin datos requeridos
    Then la primera ubicación aparece en estado válido
    And la segunda muestra alertas bloqueantes en la tabla

Flujo de UI que respalda cada step:

  Given "existe un folio con layout...":
    Task SetUpFolioWithLayout (API, no WebDriver):
      - POST /v1/folios → guarda folioNumber
      - PUT /v1/quotes/{folio}/general-info
      - PUT /v1/quotes/{folio}/locations/layout { numberOfLocations: 2 }
      - Navega browser a /quotes/{folioNumber}/locations

  When "registra una ubicación completa...":
    Task RegisterCompleteLocation (WebDriver):
      - Clic en "+ Añadir ubicación"
      - Tab Datos básicos: nombre "Ubicación 1", CP "06600", espera autocompletado
      - Tab Construcción: selecciona Mampostería, niveles 1, año 2000
      - Tab Giro: selecciona primer giro disponible con clave incendio
      - Tab Garantías: activa GUA-FIRE con suma 1000000
      - Clic "Guardar ubicación"
    Task RegisterIncompleteLocation (WebDriver):
      - Clic en "+ Añadir ubicación"
      - Tab Datos básicos: nombre "Ubicación 2" únicamente (sin CP, sin dirección)
      - No toca tabs Giro ni Garantías
      - Clic "Guardar ubicación"

  Then / And:
    Question LocationRowStatus(1): extrae badge de estado de fila 1 → "Completa"
    Question LocationRowStatus(2): extrae badge de estado de fila 2 → "Incompleta (3)"
    Question BlockingAlertBannerVisible: verifica presencia del banner amarillo

Supuestos:
- CP 06600 existe en catálogo (garantizado por Capa 1).
- Guardar ubicación sin CP/giro/garantías acepta el guardado pero retorna
  estado incompleto (la UI no bloquea el guardado).
- Los tags "CP faltante", "Clave incendio faltante", "Sin garantías activas"
  son visibles en el drawer antes de guardar.
```

---

## 4. `speckit specify` — Feature 003

Nombre: `003-premium-calculation-and-results`

```
Automatización del flujo de configuración de coberturas, ejecución del
cálculo de prima y verificación del desglose de resultados. Cubre los
pasos 4 (Coberturas) y 5 (Cálculo) del wizard.

Estrategia de datos:
- Capa 1 (hook @Before global): verificar catálogos (mismos de features 001/002).
- Capa 2 (hook @Before de este feature): crear vía API todo el estado previo
  necesario para que el test empiece directamente en Coberturas:
    POST /v1/folios
    PUT  /v1/quotes/{folio}/general-info
    PUT  /v1/quotes/{folio}/locations/layout     → { numberOfLocations: 2 }
    PUT  /v1/quotes/{folio}/locations            → ubicación 1 completa
    PATCH /v1/quotes/{folio}/locations/2         → ubicación 2: solo nombre
  El folio creado se guarda con actor.remember("folioNumber").
  El browser se abre directamente en el paso 4 (Coberturas).
- Por qué API para el setup: el comportamiento de ubicaciones ya está cubierto
  en flow 002. Repetirlo aquí no agrega cobertura; sí agrega fragilidad y tiempo.

Escenario Gherkin (declarativo, máximo 5 steps):

  Scenario: El sistema calcula la prima de la ubicación válida y omite la incompleta
    Given existe un folio con una ubicación completa y una incompleta registradas
    When el agente activa la cobertura de incendio y ejecuta el cálculo de prima
    Then el desglose muestra prima neta y prima comercial con valores positivos
    And la ubicación completa tiene prima calculada y la incompleta aparece como no calculable

Flujo de UI que respalda cada step:

  Given "existe un folio con una ubicación completa y una incompleta":
    Task SetUpFolioWithLocations (API, no WebDriver):
      - POST /v1/folios → guarda folioNumber
      - PUT /v1/quotes/{folio}/general-info
      - PUT /v1/quotes/{folio}/locations/layout { numberOfLocations: 2 }
      - PUT /v1/quotes/{folio}/locations (ubicación 1 completa: CP 06600,
        giro con claveIncendio, garantía GUA-FIRE con suma 1000000)
      - PATCH /v1/quotes/{folio}/locations/2 (solo nombre "Ubicación 2")
      - Navega browser a paso 4 (Coberturas)

  When "activa la cobertura de incendio y ejecuta el cálculo":
    Task ActivateFireCoverageAndCalculate (WebDriver):
      - En tab UBIC 01: activa toggle COV-FIRE, deja Deducible y Coaseguro
        con valores por defecto
      - Clic "Guardar coberturas"
      - Clic "Siguiente →" para ir al paso 5 (Cálculo)
      - Clic "Ejecutar cálculo"
      - Espera hasta que card "PRIMA NETA" sea visible (espera implícita
        de Serenity WebDriver; sin Thread.sleep)

  Then "el desglose muestra prima neta y prima comercial":
    Question NetPremiumValue: extrae texto del card PRIMA NETA → valor > 0
    Question CommercialPremiumValue: extrae texto del card PRIMA COMERCIAL → valor > 0

  And "la ubicación completa tiene prima / la incompleta es no calculable":
    Question LocationPremiumStatus(1): extrae valor numérico de Ubicación 1
    Question LocationPremiumStatus(2): verifica badge "No calculable" en Ubicación 2
    Question UncalculatedAlertVisible: verifica sección "Ubicaciones no calculadas"

Supuestos:
- El backend calcula prima > 0 para la combinación: CP 06600 + giro con
  claveIncendio + GUA-FIRE activa.
- La UI actualiza el desglose sin redirigir (Angular actualiza el componente).
- No se valida el valor exacto de la prima; solo que sea numérico y positivo.
- El paso de términos y condiciones NO se automatiza en este escenario.
```

---

## 5. `speckit plan` — Contexto adicional

Si `speckit plan` pide contexto adicional, pegar el siguiente bloque
(aplica a los 3 planes sin modificación):

```
Stack: Java 21 + Serenity BDD 4.2.34 + serenity-screenplay-webdriver 4.2.34
+ Selenium 4.33.0 + Cucumber 7.22.2 + JUnit Platform Suite 1.12.2.
Browser: Chrome. Build: Gradle single-module.

Estructura de paquetes bajo src/test/java/com/sofka/automation/:
  tasks/ui/       — Tasks WebDriver (Performable), una por acción de negocio
  tasks/api/      — Tasks de setup HTTP (RestAssured), sin Screenplay
  questions/      — Questions<T> para estado observable de UI
  targets/        — constantes Target por página:
                    DashboardTargets, GeneralInfoTargets, LocationsTargets,
                    CoveragesTargets, CalculationTargets
  stepdefinitions/
  runners/
  hooks/          — CatalogSetupHook (@Before order=1, verifica/crea catálogos)

Decisiones bloqueadas (no reabrir):
- Screenplay; sin Page Object Model.
- Targets en clases separadas por página; nunca inline.
- Tasks UI: una Task por acción de negocio completa (no por click atómico).
- Tasks API en tasks/api/: RestAssured directo. No usan WebDriver.
- CatalogSetupHook: @Before(order=1), compartido por los 3 features.
  Verifica via GET que catálogos existen; si no, los crea via POST.
- Setup transaccional por feature: @Before(order=2) específico por feature,
  implementado como Task API que crea el folio y su estado previo.
- Estado via actor.remember("folioNumber") / actor.recall("folioNumber").
- serenity.conf: webdriver.driver=chrome,
  serenity.base.url=http://localhost:4200,
  restapi.base.url=http://localhost:8080.
- Un runner por feature: FolioTestRunner, LocationsTestRunner,
  CalculationTestRunner.
- Plugin Serenity: io.cucumber.core.plugin.SerenityReporterParallel.
- Specs bajo specs/: 001-folio-creation-and-general-info/,
  002-locations-with-incomplete-alert/, 003-premium-calculation-and-results/.

Restricciones:
- Versiones de dependencias fijadas por constitución; no cambiar.
- Sin Thread.sleep; usar esperas implícitas de Serenity WebDriver.
- Sin helpers genéricos para operaciones de un solo uso.
```

---

## Referencia rápida de comandos

```bash
speckit init          # omitir si ya existe .specify/
speckit constitution  # → pegar sección 1

speckit specify 001-folio-creation-and-general-info   # → sección 2
speckit specify 002-locations-with-incomplete-alert   # → sección 3
speckit specify 003-premium-calculation-and-results   # → sección 4

speckit plan 001-folio-creation-and-general-info      # → sección 5 si la pide
speckit plan 002-locations-with-incomplete-alert      # → sección 5 si la pide
speckit plan 003-premium-calculation-and-results      # → sección 5 si la pide
```
