# language: es

Característica: Cálculo de prima con coberturas

  @premium-calculation
  Escenario: Verificar prima calculada para ubicación completa con cobertura de incendio
    Dado que el agente navega a la pantalla de coberturas del folio
    Cuando el agente activa la cobertura de interrupción de negocio en la ubicación completa y guarda
    Y el agente ejecuta el cálculo de prima
    Entonces la prima neta es mayor a cero
    Y la prima comercial es mayor a cero

  @premium-calculation
  Escenario: Verificar que la ubicación completa no muestra estado no calculable
    Dado que el agente navega a la pantalla de coberturas del folio
    Cuando el agente activa la cobertura de interrupción de negocio en la ubicación completa y guarda
    Y el agente ejecuta el cálculo de prima
    Entonces la primera ubicación no aparece como "No calculable"

  @premium-calculation
  Escenario: Verificar estado no calculable para ubicación incompleta
    Dado que el agente navega a la pantalla de coberturas del folio
    Cuando el agente activa la cobertura de interrupción de negocio en la ubicación completa y guarda
    Y el agente ejecuta el cálculo de prima
    Entonces la ubicación incompleta aparece como "No calculable"

  @premium-calculation-single
  Escenario: Verificar que la prima de la ubicación completa es independiente de ubicaciones incompletas
    Dado que el agente navega a la pantalla de coberturas del folio
    Cuando el agente activa la cobertura de interrupción de negocio en la ubicación completa y guarda
    Y el agente ejecuta el cálculo de prima
    Entonces la prima neta es mayor a cero
