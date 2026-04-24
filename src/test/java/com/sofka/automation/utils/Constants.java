package com.sofka.automation.utils;

public final class Constants {

    public static final String BASE_URL          = "http://localhost:4200";
    public static final String BACKEND_BASE_URL  = "http://localhost:8080";
    public static final String CORE_BASE_URL     = "http://localhost:8081";

    public static final String TEST_RAZON_SOCIAL     = "Empresa Prueba SA de CV";
    public static final String TEST_RFC              = "EPR860101AB2";
    public static final String TEST_EMAIL            = "prueba@automation.com";
    public static final String TEST_PHONE            = "5512345678";

    public static final String TEST_SUBSCRIBER_ID   = "SUB-TEST";
    public static final String TEST_SUBSCRIBER_NAME = "Suscriptor Automatización";
    public static final String TEST_AGENT_CODE      = "AGT-TEST";
    public static final String TEST_AGENT_NAME      = "Agente Automatización";

    public static final String TEST_LOCATION_ZIP_CODE        = "06600";
    public static final String TEST_LOCATION_BL_CODE         = "OFICINAS";
    public static final String TEST_LOCATION_BL_FIRE_KEY     = "1110";
    public static final String TEST_LOCATION_GUARANTEE_CODE  = "INCENDIO";
    public static final int    TEST_LOCATION_INSURED_VALUE   = 1_000_000;
    public static final String LOCATIONS_URL_TEMPLATE        = "/cotizador/quotes/%s/locations";

    private Constants() {}
}
