# Tasks: Coberturas y Cálculo de Prima (003)

**Input**: Documentos de diseño en `specs/003-coverage-premium-calc/`
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/ ✅

**Organización**: Tareas agrupadas por historia de usuario para implementación y prueba independiente.

## Formato: `[ID] [P?] [Story?] Descripción con ruta de archivo`

- **[P]**: Puede ejecutarse en paralelo (archivos distintos, sin dependencias incompletas)
- **[Story]**: Historia de usuario a la que pertenece la tarea

---

## Fase 1: Setup (Infraestructura compartida)

**Propósito**: Agregar constantes necesarias para todas las historias antes de iniciar cualquier implementación.

- [ ] T001 Agregar constantes `COVERAGE_CODE_FIRE = "COV-FIRE"`, `TECHNICAL_INFO_URL_TEMPLATE = "/cotizador/quotes/%s/technical-info"` y `CALCULATION_URL_TEMPLATE = "/cotizador/quotes/%s/calculation"` a `src/test/java/com/sofka/automation/utils/Constants.java`

---

## Fase 2: Fundacional (Prerequisitos bloqueantes)

**Propósito**: Crear los Targets y la Task API compartidos. Ninguna historia de usuario puede implementarse hasta completar esta fase.

**⚠️ CRÍTICO**: Las Targets deben existir antes de implementar Tasks UI; la Task API debe existir antes de crear los hooks.

- [ ] T002 [P] Crear `src/test/java/com/sofka/automation/targets/CoveragesTargets.java` con: `COVERAGES_GRID` (CSS `app-coverage-options-grid`), `LOCATION_TAB_SELECTOR` (CSS `app-location-tab-selector`), `SAVE_COVERAGES_BUTTON` (XPath `//div[contains(@class,'technical-info-page__nav')]//button[normalize-space(.)='Guardar coberturas']`), y método estático `coverageToggleByCode(String code)` retornando XPath `//div[contains(@class,'coverage-card')][.//code[contains(@class,'coverage-card__code') and normalize-space()='{code}']]//app-switch`
- [ ] T003 [P] Crear `src/test/java/com/sofka/automation/targets/CalculationTargets.java` con: `EXECUTE_CALCULATION_BUTTON` (XPath `//button[normalize-space(.)='Ejecutar cálculo']`), `PREMIUM_SUMMARY` (CSS `.premium-summary`), `NET_PREMIUM_VALUE` (CSS `.premium-summary__card--dark .premium-summary__card-value`), `COMMERCIAL_PREMIUM_VALUE` (CSS `.premium-summary__card--brand .premium-summary__card-value`), `NO_CALCULABLE_BADGE` (XPath `//li[contains(@class,'premium-summary__location-item')]//app-badge[normalize-space(.)='No calculable']`)
- [ ] T004 [P] Crear `src/test/java/com/sofka/automation/tasks/api/SetupCoveragePremiumScenario.java` implementando `Performable` con dos métodos de fábrica estáticos: `withTwoLocations()` (crea folio + PUT general-info + PUT locations con 1 ubicación completa [CP `Constants.TEST_LOCATION_ZIP_CODE`, giro `Constants.TEST_LOCATION_BL_CODE`, garantía `Constants.TEST_LOCATION_GUARANTEE_CODE`, valor `Constants.TEST_LOCATION_INSURED_VALUE`] y 1 incompleta [solo nombre]) y `withSingleLocation()` (crea folio + PUT general-info + PUT locations con solo la ubicación completa); ambas factories persisten versión dinámica en cada PUT y llaman a `actor.remember("folioNumber", folioNumber)`

**Checkpoint**: CoveragesTargets, CalculationTargets y SetupCoveragePremiumScenario listos — implementación de US1 puede comenzar.

---

## Fase 3: Historia de Usuario 1 — Prima calculada para ubicación completa (Prioridad: P1) 🎯 MVP

**Meta**: El agente activa COV-FIRE en la ubicación completa, guarda y ejecuta el cálculo. El desglose muestra prima neta y prima comercial con valores mayores a cero.

**Prueba independiente**: Con folio de 2 ubicaciones creado vía API y tag `@premium-calculation`, navegar a `/technical-info`, activar COV-FIRE en ubicación 1, guardar, ir a `/calculation`, ejecutar cálculo y verificar que `.premium-summary__card--dark .premium-summary__card-value` y `.premium-summary__card--brand .premium-summary__card-value` contienen valores mayores a cero.

- [ ] T005 [P] [US1] Crear `src/test/java/com/sofka/automation/tasks/ui/ActivateCoverageForLocation.java` implementando `Performable`: recibe código de cobertura (String), usa `CoveragesTargets.coverageToggleByCode(code)` para hacer click en el switch de la tarjeta correspondiente, espera a que aparezca el badge "Activa" en esa tarjeta
- [ ] T006 [P] [US1] Crear `src/test/java/com/sofka/automation/tasks/ui/SaveCoverages.java` implementando `Performable`: hace click en `CoveragesTargets.SAVE_COVERAGES_BUTTON` y espera hasta que el botón vuelva a mostrar "Guardar coberturas" (no "Guardando...")
- [ ] T007 [P] [US1] Crear `src/test/java/com/sofka/automation/tasks/ui/ExecuteCalculation.java` implementando `Performable`: navega a URL `Constants.BASE_URL + String.format(Constants.CALCULATION_URL_TEMPLATE, actor.recall("folioNumber"))`, espera visibilidad de `CalculationTargets.EXECUTE_CALCULATION_BUTTON`, hace click en él, espera visibilidad de `CalculationTargets.PREMIUM_SUMMARY`
- [ ] T008 [P] [US1] Crear `src/test/java/com/sofka/automation/questions/NetPremiumValue.java` implementando `Question<String>`: lee texto de `CalculationTargets.NET_PREMIUM_VALUE`, retorna texto limpio sin símbolo de moneda ni formato (solo dígitos y punto decimal) para facilitar comparación; retorna `"0"` si el elemento no existe
- [ ] T009 [P] [US1] Crear `src/test/java/com/sofka/automation/questions/CommercialPremiumValue.java` implementando `Question<String>`: mismo patrón que `NetPremiumValue` pero usando `CalculationTargets.COMMERCIAL_PREMIUM_VALUE`
- [ ] T010 [US1] Crear `src/test/java/com/sofka/automation/hooks/CoveragePremiumSetupHook.java` con método `@Before(order=20, value="@premium-calculation")` que llama a `OnStage.theActorCalled("agent").attemptsTo(SetupCoveragePremiumScenario.withTwoLocations())`
- [ ] T011 [P] [US1] Crear `src/test/java/com/sofka/automation/runners/PremiumCalculationTestRunner.java` con `@Suite` y `@SelectClasspathResource("features/premium_calculation.feature")` e `includeEngineFilters = @IncludeEngines("cucumber")`, configurando tag filter `"@premium-calculation or @premium-calculation-single"`
- [ ] T012 [P] [US1] Crear `src/test/resources/features/premium_calculation.feature` en Gherkin declarativo en español con tag `@premium-calculation` y dos escenarios de US1: (1) "Verificar prima calculada para ubicación completa con cobertura de incendio" — pasos: navega a coberturas, activa COV-FIRE y guarda, ejecuta cálculo, prima neta mayor a cero, prima comercial mayor a cero; (2) "Verificar que la ubicación completa no muestra estado no calculable" — pasos: navega a coberturas, activa y guarda, ejecuta cálculo, verifica que la primera ubicación no muestra "No calculable"
- [ ] T013 [US1] Crear `src/test/java/com/sofka/automation/stepdefinitions/PremiumCalculationStepDefinitions.java` con `@Before(order=10)` / `@After` para `OnStage`, y bindings en español para los steps de US1: `@Dado("que el agente navega a la pantalla de coberturas del folio")`, `@Cuando("el agente activa la cobertura de incendio en la ubicación completa y guarda")`, `@Y("el agente ejecuta el cálculo de prima")`, `@Entonces("la prima neta es mayor a cero")` (usa `NetPremiumValue`, convierte a Double, assert > 0), `@Y("la prima comercial es mayor a cero")` (usa `CommercialPremiumValue`)

**Checkpoint**: US1 completamente funcional. `./gradlew test -Dcucumber.filter.tags="@premium-calculation" aggregate` debe mostrar al menos los escenarios de US1 pasando.

---

## Fase 4: Historia de Usuario 2 — Estado "No calculable" para ubicación incompleta (Prioridad: P2)

**Meta**: Tras el cálculo, la ubicación incompleta muestra el badge "No calculable" en el desglose, sin afectar el resultado de la ubicación completa.

**Prueba independiente**: Con folio de 2 ubicaciones y `@premium-calculation`, ejecutar el mismo flujo de US1 y verificar presencia de `app-badge` con texto "No calculable" en `li.premium-summary__location-item`.

- [ ] T014 [P] [US2] Crear `src/test/java/com/sofka/automation/questions/LocationCalculationStatus.java` implementando `Question<String>`: verifica si `CalculationTargets.NO_CALCULABLE_BADGE` es visible; retorna `"No calculable"` si existe, `"Calculable"` si no existe
- [ ] T015 [US2] Agregar escenario US2 a `src/test/resources/features/premium_calculation.feature` con tag `@premium-calculation`: "Verificar estado no calculable para ubicación incompleta" — pasos: navega a coberturas, activa COV-FIRE y guarda, ejecuta cálculo, verifica que la ubicación incompleta aparece como "No calculable"
- [ ] T016 [US2] Agregar al `PremiumCalculationStepDefinitions.java` los bindings de US2: `@Entonces("la ubicación incompleta aparece como {string}")` usando `LocationCalculationStatus` con `seeThat(..., equalTo("No calculable"))`

**Checkpoint**: US1 + US2 pasan. 3 escenarios con `@premium-calculation` verdes.

---

## Fase 5: Historia de Usuario 3 — Independencia del cálculo (Prioridad: P3)

**Meta**: Un folio con una sola ubicación completa produce prima neta > 0, validando que la presencia de la ubicación incompleta en US1/US2 no altera el resultado de la calculable (SC-004).

**Prueba independiente**: Con folio de 1 sola ubicación completa creado por `withSingleLocation()` y tag `@premium-calculation-single`, ejecutar el mismo flujo de activar + calcular y verificar prima neta > 0.

- [ ] T017 [US3] Agregar a `CoveragePremiumSetupHook.java` un segundo método `@Before(order=20, value="@premium-calculation-single")` que llama a `OnStage.theActorCalled("agent").attemptsTo(SetupCoveragePremiumScenario.withSingleLocation())`
- [ ] T018 [US3] Agregar escenario US3 a `premium_calculation.feature` con tag `@premium-calculation-single`: "Verificar que la prima de la ubicación completa es independiente de ubicaciones incompletas" — pasos: navega a coberturas, activa COV-FIRE y guarda, ejecuta cálculo, prima neta mayor a cero
- [ ] T019 [US3] Verificar que los step bindings existentes en `PremiumCalculationStepDefinitions.java` cubren todos los steps del escenario US3 (reutilizan los mismos steps de US1); si algún step no está cubierto, agregarlo

**Checkpoint**: Los 3 escenarios US1 + US2 (`@premium-calculation`) y US3 (`@premium-calculation-single`) pasan. 4 escenarios en verde.

---

## Fase 6: Polish y validación final

**Propósito**: Verificación end-to-end y limpieza.

- [ ] T020 Ejecutar `./gradlew test -Dcucumber.filter.tags="@premium-calculation or @premium-calculation-single" aggregate` y confirmar que los 4 escenarios pasan con BUILD SUCCESSFUL; si alguno falla, corregir antes de cerrar el feature

---

## Dependencias y orden de ejecución

### Dependencias entre fases

- **Fase 1 (Setup)**: Sin dependencias — comenzar de inmediato
- **Fase 2 (Fundacional)**: Depende de Fase 1 — bloquea todas las historias
- **Fases 3–5 (Historias)**: Dependen de Fase 2; pueden ejecutarse secuencialmente P1 → P2 → P3
- **Fase 6 (Polish)**: Depende de Fases 3–5 completas

### Dependencias dentro de US1 (Fase 3)

- T005–T009 [P]: paralelos entre sí; requieren Fase 2 completa
- T010: requiere T004 (SetupCoveragePremiumScenario)
- T011, T012 [P]: paralelos con T010
- T013: requiere T005–T012 completos

### Dependencias US2 (Fase 4)

- T014 [P]: paralelo con T005–T009 (archivo diferente)
- T015: requiere T012 (mismo archivo .feature)
- T016: requiere T013 + T014 + T015

### Dependencias US3 (Fase 5)

- T017: requiere T010 (modifica mismo archivo de hook)
- T018: requiere T015 (modifica mismo archivo .feature)
- T019: requiere T016 + T017 + T018

---

## Oportunidades de paralelismo

```
# Fase 2 — paralelo:
T002 (CoveragesTargets) + T003 (CalculationTargets) + T004 (SetupCoveragePremiumScenario)

# Fase 3 — paralelo tras Fase 2:
T005 (ActivateCoverageForLocation) + T006 (SaveCoverages) + T007 (ExecuteCalculation)
+ T008 (NetPremiumValue) + T009 (CommercialPremiumValue)
+ T010 (Hook) + T011 (Runner) + T012 (Feature file)

# Fase 4 — T014 puede iniciarse en paralelo con Fase 3 (archivo diferente)
```

---

## Estrategia de implementación

### MVP (solo US1)

1. Completar Fase 1 + Fase 2
2. Completar Fase 3 (US1)
3. **Validar**: `./gradlew test -Dcucumber.filter.tags="@premium-calculation" aggregate`
4. 2 escenarios verdes = MVP entregado

### Entrega incremental

1. Setup + Fundacional → base lista
2. US1 → activación + prima calculada ✅
3. US2 → badge "No calculable" ✅
4. US3 → independencia SC-004 ✅
5. Polish → 4 escenarios verdes → PR a develop
