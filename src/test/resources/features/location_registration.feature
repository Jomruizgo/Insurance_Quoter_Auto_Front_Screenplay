# language: es

Característica: Registro de ubicaciones con estado incompleto y completo
  Como agente del cotizador
  Quiero ver el estado de cada ubicación en la pantalla de Ubicaciones
  Para identificar cuáles requieren corrección antes de tarificar

  @location-registration
  Escenario: Verificar badge incompleto y alerta bloqueante para ubicación sin datos
    Dado que el agente navega a la pantalla de ubicaciones del folio
    Cuando el agente visualiza la segunda ubicación sin datos
    Entonces la segunda ubicación muestra el badge "Incompleta"
    Y aparece un banner de alertas bloqueantes en la pantalla
    Y el agente puede navegar al siguiente paso del wizard

  @location-registration
  Escenario: Verificar que ubicación completa no muestra badge de incompleta ni alertas
    Dado que el agente navega a la pantalla de ubicaciones del folio
    Cuando el agente visualiza la primera ubicación con datos completos
    Entonces la primera ubicación no muestra el badge "Incompleta"
    Y no aparece banner de alertas bloqueantes en la pantalla
    Y la primera ubicación muestra el badge "Completa"
