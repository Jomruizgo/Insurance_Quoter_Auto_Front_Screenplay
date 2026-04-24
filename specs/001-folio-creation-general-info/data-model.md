# Data Model: Creación de Folio y Captura de Datos Generales

**Branch**: `001-folio-creation-general-info` | **Date**: 2026-04-24

## Estado de sesión del Actor

El Actor mantiene estado entre steps mediante `actor.remember` / `actor.recall`.
No se usan variables estáticas ni campos de instancia (principio V).

| Clave | Tipo | Producido por | Consumido por |
|-------|------|---------------|---------------|
| `"folioNumber"` | `String` | `CreateFolio` (Task UI) | Steps siguientes; flows 002 y 003 |

## Datos de prueba (en Constants.java)

Valores fijos usados por `CompleteGeneralInfo` Task:

| Constante | Valor de ejemplo | Usado en |
|-----------|------------------|----------|
| `TEST_RAZON_SOCIAL` | `"Empresa Prueba SA de CV"` | campo Razón social |
| `TEST_RFC` | `"EPR860101AB2"` | campo RFC |
| `TEST_EMAIL` | `"prueba@automation.com"` | campo Correo de contacto |
| `TEST_PHONE` | `"5512345678"` | campo Teléfono |

## Entidades del sistema bajo prueba

### Folio
Identificador único de cotización. Generado por el backend al invocar `POST /v1/folios`.

| Atributo | Tipo | Observabilidad en UI |
|----------|------|----------------------|
| `folioNumber` | `String` (ej. `FOL-2026-00003`) | URL activa + breadcrumb |
| `status` | `String` (ej. `Creado`) | badge de estado en dashboard |

### DatosAsegurado
Sección "Asegurado" del paso 1 del wizard.

| Campo UI | Tipo | Obligatorio |
|----------|------|-------------|
| Razón social | texto | sí |
| RFC | texto | sí |
| Correo de contacto | email | sí |
| Teléfono | texto | sí |

### DatosSuscripcion
Sección "Suscripción" del paso 1 del wizard.

| Campo UI | Tipo | Origen |
|----------|------|--------|
| Suscriptor | dropdown (catálogo) | precargado del modal de creación |
| Agente | dropdown (catálogo) | precargado del modal de creación |
| Clasificación de riesgo | dropdown (catálogo) | selección en esta pantalla |
| Tipo de negocio | dropdown (catálogo) | selección en esta pantalla |

## Clases Java del proyecto (no son entidades de dominio)

| Clase | Tipo | Propósito |
|-------|------|-----------|
| `CreateFolio` | Task UI | abre modal, selecciona suscriptor y agente, confirma |
| `CompleteGeneralInfo` | Task UI | llena datos asegurado y suscripción, avanza |
| `CatalogSetupHook` | Hook | verifica/crea catálogos mínimos vía API @Before(order=1) |
| `SectionCompletionStatus` | Question\<List\<String\>\> | badges "• Completo" visibles |
| `WizardStepIndicator` | Question\<String\> | paso activo en el stepper |
| `DashboardTargets` | Targets | localizadores de la página `/cotizador` |
| `GeneralInfoTargets` | Targets | localizadores del paso 1 del wizard |
| `Constants` | Utility | todos los valores literales del proyecto |
| `FolioTestRunner` | Runner | JUnit Suite para este feature |
