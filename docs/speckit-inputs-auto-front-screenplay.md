# Inputs para SpecKit — Auto_Front_Screenplay

Textos cortos a pegar en cada comando de SpecKit. El AI genera los documentos
completos a partir de estos inputs.

---

## `speckit constitution`

```
Proyecto de automatización UI end-to-end para la SPA cotizador-danos-web
(Angular 19, http://localhost:4200) usando Java 21 + Serenity BDD 4.2.34 +
serenity-screenplay-webdriver 4.2.34 + Selenium 4.33.0 + Cucumber 7.22.2.
Patrón Screenplay obligatorio (Tasks, Questions, Targets, Actor); sin Page
Object Model. Gherkin declarativo en español, máximo 5 steps por escenario.
Código siempre en inglés; specs y documentación en español. Sin hardcoding:
todos los valores literales en Constants.java. Sin pruebas unitarias sobre
el código de automatización. GitFlow: rama base develop, features por PR,
Conventional Commits. Test data independence: hook @Before verifica o crea
catálogos vía API; cada escenario crea sus propios datos transaccionales.
Dependencias fijadas; no modificar versiones.
```

---

## `speckit specify` — Feature 001

```
speckit specify 001-folio-creation-and-general-info
```

Input:

```
Automatización del flujo de creación de folio y captura de datos generales
en la SPA cotizador-danos-web. El agente abre el modal "+ Nuevo folio" desde
el dashboard, selecciona suscriptor y agente, crea el folio y completa los
datos del asegurado (razón social, RFC, correo, teléfono) y suscripción
(clasificación de riesgo, tipo de negocio). El escenario verifica que ambas
secciones muestran estado completo y el wizard avanza al paso de layout.
Setup: hook @Before garantiza catálogos mínimos vía API. Folio creado por UI
(eso es lo que prueba). Folio propagado con actor.remember("folioNumber").
```

---

## `speckit specify` — Feature 002

```
speckit specify 002-locations-with-incomplete-alert
```

Input:

```
Automatización del registro de ubicaciones en la SPA cotizador-danos-web:
una ubicación completa (CP válido, giro con clave incendio, garantía tarifable)
y una incompleta (solo nombre, sin CP ni giro ni garantías). El escenario
verifica que la UI muestra badge "Incompleta" y banner de alertas bloqueantes
para la segunda ubicación sin bloquear el folio. Setup vía API antes de abrir
el browser: POST /v1/folios, PUT general-info, PUT layout (2 ubicaciones).
La UI se prueba desde la pantalla de Ubicaciones; la creación de folio y datos
generales ya están cubiertos en el flujo 001.
```

---

## `speckit specify` — Feature 003

```
speckit specify 003-premium-calculation-and-results
```

Input:

```
Automatización del flujo de coberturas + cálculo de prima en la SPA
cotizador-danos-web. El agente activa la cobertura COV-FIRE en la ubicación
completa, guarda, y ejecuta el cálculo. El escenario verifica que el desglose
muestra prima neta y prima comercial con valores positivos, que la ubicación
completa tiene prima calculada y que la incompleta aparece como "No calculable"
con sus alertas. Setup vía API: POST folio, PUT general-info, PUT layout,
PUT/PATCH ubicaciones (1 completa, 1 incompleta). UI se prueba desde coberturas;
flows 001 y 002 ya cubren los pasos previos.
```

---

## `speckit plan`

El comando lee la spec generada automáticamente. Si pide contexto adicional:

```
Stack: Java 21, Serenity BDD 4.2.34, Selenium 4.33.0, Cucumber 7.22.2,
Chrome. Paquetes: tasks/ui/, tasks/api/, questions/, targets/, hooks/,
utils/Constants.java. Tasks UI: una por acción de negocio (no por click
atómico). Tasks API: RestAssured directo para setup de precondiciones.
Targets por página. Sin Thread.sleep; esperas implícitas de Serenity.
Un runner por feature. Plugin Serenity: SerenityReporterParallel.
```

---

## `speckit implement`

```
Recuerda cerrar cada issue a medida que la vayas termianndo, no esperes hasta el final para
hacerlas todas juntas, implementa en paralelo todas las que puedas. El código fuente del frontend a automatizar está en:
D:\Trabajo\Sofka\Insurance-Quoter\Insurance-Quoter\Insurance-Quoter-Front\
Leer los componentes Angular para extraer los selectores CSS correctos
antes de escribir las clases Target.
```
