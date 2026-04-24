# Research: Creación de Folio y Captura de Datos Generales

**Branch**: `001-folio-creation-general-info` | **Date**: 2026-04-24

## Decisiones técnicas

### 1. Estrategia de esperas en WebDriver

**Decisión**: Esperas implícitas de Serenity WebDriver (configuradas en `serenity.conf`).

**Rationale**: Serenity gestiona el ciclo de vida del driver y aplica esperas automáticas
antes de interactuar con elementos. Elimina `Thread.sleep` (prohibido por constitución,
principio XI) y evita la fragilidad de timeouts hardcodeados.

**Alternativas descartadas**:
- `WebDriverWait` explícita: válida pero verbosa; Serenity ya la abstrae.
- `Thread.sleep`: prohibida por constitución.

### 2. Extracción del número de folio tras creación

**Decisión**: Extraer el folio de la URL activa después de que el modal cierre y el
wizard redirija (`driver.getCurrentUrl()` vía `TheWebPage.currentUrl()`).

**Rationale**: La URL contiene el folioNumber directamente (ej. `/quotes/FOL-2026-00003/general-info`).
Es más estable que buscar el folio en el DOM, que puede estar en múltiples componentes.

**Alternativas descartadas**:
- Leer el folio de un elemento DOM: frágil si el diseño del componente cambia.

### 3. Selección de elementos en dropdowns de catálogo

**Decisión**: Seleccionar el primer elemento disponible en los dropdowns de Suscriptor
y Agente (no buscar un valor específico por nombre).

**Rationale**: El hook `@Before` garantiza que existe al menos un elemento; seleccionar
el primero disponible hace el test independiente de qué catálogos específicos existan
en el ambiente.

**Alternativas descartadas**:
- Seleccionar por valor fijo en `Constants.java`: acoplamiento al dato concreto que puede
  no existir en todos los ambientes.

### 4. Setup de catálogos en hook @Before

**Decisión**: `CatalogSetupHook` llama a `GET /v1/subscribers` y `GET /v1/agents`. Si
la respuesta está vacía, llama a `POST` para crear registros mínimos con datos de
`Constants.java`.

**Rationale**: Garantiza independencia de datos (principio IV de constitución) sin
requerir seeds manuales en la BD antes de cada ejecución.

**Alternativas descartadas**:
- Fixtures de BD directa (SQL scripts): acoplamiento al motor de BD y requiere acceso
  directo a la instancia.
- Asumir datos preexistentes: violación directa de principio IV.

### 5. Paquete base del proyecto

**Decisión**: `com.sofka.automation`

**Rationale**: Consistencia con la convención de la organización (Sofka) y el proyecto
(automation).
