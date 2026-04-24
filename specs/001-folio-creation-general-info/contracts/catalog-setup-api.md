# Contratos API — Setup de Catálogos (CatalogSetupHook)

Estos endpoints son invocados por `CatalogSetupHook` en `@Before(order=1)`.
No forman parte del flujo UI bajo prueba; son precondición de datos.

**Base URL backend**: `http://localhost:8080` (`Constants.BACKEND_BASE_URL`)
**Base URL core**: `http://localhost:8081` (`Constants.CORE_BASE_URL`)

---

## Suscriptores

### GET /v1/subscribers
Verifica que existe al menos un suscriptor en el catálogo.

**Response exitosa (200)**:
```json
[
  { "id": "SUB-001", "name": "Aseguradora Norte" }
]
```

**Acción del hook**: si el arreglo está vacío → invocar POST /v1/subscribers.

### POST /v1/subscribers *(solo si catálogo vacío)*
Crea un suscriptor mínimo de prueba.

**Request**:
```json
{ "id": "SUB-TEST", "name": "Suscriptor Automatización" }
```

**Response exitosa (201)**:
```json
{ "id": "SUB-TEST", "name": "Suscriptor Automatización" }
```

---

## Agentes

### GET /v1/agents
Verifica que existe al menos un agente en el catálogo.

**Response exitosa (200)**:
```json
[
  { "code": "AGT-001", "name": "Agente Prueba" }
]
```

**Acción del hook**: si el arreglo está vacío → invocar POST /v1/agents.

### POST /v1/agents *(solo si catálogo vacío)*
Crea un agente mínimo de prueba.

**Request**:
```json
{ "code": "AGT-TEST", "name": "Agente Automatización" }
```

**Response exitosa (201)**:
```json
{ "code": "AGT-TEST", "name": "Agente Automatización" }
```

---

## Notas

- Si cualquier llamada retorna un código fuera del rango 2xx, el hook lanza
  excepción y el escenario falla con error de precondición.
- Los datos creados por el hook son de prueba; no representan datos reales
  del negocio.
- Los valores de los campos de prueba están definidos en `Constants.java`.
