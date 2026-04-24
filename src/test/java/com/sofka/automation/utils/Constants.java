package com.sofka.automation.utils;

public final class Constants {

    public static final String BASE_URL          = "http://localhost:4200";
    public static final String BACKEND_BASE_URL  = "http://localhost:8080";
    public static final String CORE_BASE_URL     = "http://localhost:8081";

    public static final String TEST_RAZON_SOCIAL         = "Empresa Prueba SA de CV";
    public static final String TEST_RFC                  = "EPR860101AB2";
    public static final String TEST_EMAIL                = "prueba@automation.com";
    public static final String TEST_PHONE                = "5512345678";
    public static final String TEST_RISK_CLASSIFICATION  = "STANDARD";
    public static final String TEST_BUSINESS_TYPE        = "COMMERCIAL";

    public static final String TEST_SUBSCRIBER_ID   = "SUB-001";
    public static final String TEST_SUBSCRIBER_NAME = "Seguros Sofka";
    public static final String TEST_AGENT_CODE      = "AGT-123";
    public static final String TEST_AGENT_NAME      = "Juan Pérez";

    public static final String TEST_LOCATION_ZIP_CODE        = "06600";
    public static final String TEST_LOCATION_BL_CODE         = "OFICINAS";
    public static final String TEST_LOCATION_BL_FIRE_KEY     = "1110";
    public static final String TEST_LOCATION_GUARANTEE_CODE  = "INCENDIO";
    public static final int    TEST_LOCATION_INSURED_VALUE   = 1_000_000;
    public static final String LOCATIONS_URL_TEMPLATE        = "/cotizador/quotes/%s/locations";
    public static final String TECHNICAL_INFO_URL_TEMPLATE   = "/cotizador/quotes/%s/technical-info";
    public static final String CALCULATION_URL_TEMPLATE      = "/cotizador/quotes/%s/calculation";

    // 003-coverage-premium-calc: datos de ubicación que producen prima calculable
    public static final String COVERAGE_TEST_BL_CODE         = "BL-002";
    public static final String COVERAGE_TEST_BL_FIRE_KEY     = "FK-INC-02";
    public static final String COVERAGE_TEST_GUARANTEE_CODE  = "GUA-FIRE";
    // COV-BI: cobertura no seleccionada por defecto, usada para verificar el flujo de activación
    public static final String COVERAGE_CODE_BI              = "COV-BI";

    private Constants() {}
}
