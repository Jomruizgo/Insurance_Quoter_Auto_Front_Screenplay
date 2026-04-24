# Insurance Quoter Auto Front — Screenplay

Automatización UI end-to-end del cotizador de daños usando **Java 21 + Serenity BDD 4.2.34 + Screenplay**.

## Requisitos previos

| Requisito | Verificación |
|-----------|-------------|
| Java 21 | `java -version` |
| Google Chrome (última versión estable) | Automático via Selenium Manager |
| Backend corriendo en `http://localhost:8080` | `curl http://localhost:8080/actuator/health` |
| Core corriendo en `http://localhost:8081` | `curl http://localhost:8081/actuator/health` |
| SPA Angular corriendo en `http://localhost:4200` | Abrir en browser |

## Ejecución

```bash
./gradlew clean test aggregate
```

El hook `@Before(order=1)` verifica y crea catálogos automáticamente. No se requieren seeds manuales.

## Filtrar por feature

```bash
./gradlew clean test aggregate -Dcucumber.filter.tags="@folio-creation"
```

## Reporte

```
target/site/serenity/index.html
```

Abrir en Windows:
```bash
start target/site/serenity/index.html
```

## Estructura

```
src/test/java/com/sofka/automation/
├── hooks/          — @Before hooks (setup de catálogos vía API)
├── tasks/ui/       — Performable Tasks para acciones UI
├── tasks/api/      — Performable Tasks para setup HTTP
├── questions/      — Question<T> para estado observable de UI
├── targets/        — Target locators por página
├── stepdefinitions/
├── runners/
└── utils/Constants.java

src/test/resources/
├── features/
└── serenity.conf
```

## Principios de diseño

- **Screenplay Pattern**: Tasks, Questions, Targets, Actor — sin Page Object Model
- **Sin hardcoding**: todos los valores literales en `Constants.java`
- **Sin Thread.sleep**: esperas implícitas de Serenity
- **Gherkin declarativo**: máximo 5 steps en español, lenguaje de negocio
- **Independencia de datos**: hook `@Before` garantiza catálogos; cada escenario crea sus datos transaccionales por UI
