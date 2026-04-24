# Quickstart: Creación de Folio y Captura de Datos Generales

## Requisitos previos

| Requisito | Verificación |
|-----------|-------------|
| Java 21 | `java -version` |
| Google Chrome (última versión estable) | `google-chrome --version` |
| Backend corriendo en `http://localhost:8080` | `curl http://localhost:8080/actuator/health` |
| Core corriendo en `http://localhost:8081` | `curl http://localhost:8081/actuator/health` |
| SPA corriendo en `http://localhost:4200` | abrir en browser |

## Ejecutar el escenario

Desde la raíz del proyecto `Insurance_Quoter_Auto_Front_Screenplay/`:

```bash
./gradlew clean test aggregate
```

El hook `@Before` verifica y crea catálogos automáticamente antes de que
arranque el escenario. No se requieren seeds manuales.

## Ver el reporte

```bash
# El reporte se genera en:
target/site/serenity/index.html

# Abrir en macOS:
open target/site/serenity/index.html

# Abrir en Windows:
start target/site/serenity/index.html
```

## Ejecutar solo este feature

```bash
./gradlew clean test aggregate -Dcucumber.filter.tags="@folio-creation"
```

Agregar el tag `@folio-creation` en el archivo `.feature` para filtrar.

## Troubleshooting

| Síntoma | Causa probable | Solución |
|---------|----------------|----------|
| `CatalogSetupHook` falla con 4xx | backend no responde o endpoint no existe | verificar que backend está corriendo |
| `NoSuchElementException` en modal | SPA no cargó a tiempo | revisar `webdriver.timeouts.implicitlywait` en `serenity.conf` |
| Folio no extraído de URL | redirección tardía del Angular router | aumentar `webdriver.timeouts.pageLoadTimeout` |
| ChromeDriver incompatible | versión de Chrome actualizada | Selenium 4.33.0 usa ChromeDriver Manager automático |
