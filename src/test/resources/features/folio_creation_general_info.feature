# language: es

Característica: Creación de folio y captura de datos generales
  Como agente del cotizador
  Quiero crear un folio nuevo y completar los datos generales
  Para avanzar al paso de layout con ambas secciones completas

  @folio-creation
  Escenario: Completar datos generales y avanzar al layout
    Dado que el agente está en el panel de cotizaciones
    Cuando crea un nuevo folio seleccionando suscriptor y agente
    Y completa los datos del asegurado y suscripción
    Entonces ambas secciones muestran estado completo y el folio avanza al paso de layout
