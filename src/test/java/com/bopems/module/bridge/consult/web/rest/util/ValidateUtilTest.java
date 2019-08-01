package com.bopems.module.bridge.consult.web.rest.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValidateUtilTest {

    @Test
    public void testPayloadValidate() {

        assertEquals(false, ValidateUtil.PayloadValidate(null, "{}"));
        assertEquals(false, ValidateUtil.PayloadValidate("{}", null));
        assertEquals(false, ValidateUtil.PayloadValidate("", "{}"));
        assertEquals(false, ValidateUtil.PayloadValidate("{}", ""));
        assertEquals(false, ValidateUtil.PayloadValidate("", "[]"));
        assertEquals(false, ValidateUtil.PayloadValidate("[]", ""));
        assertEquals(false, ValidateUtil.PayloadValidate("{\"carro\":\"fusca\",\"cor\":\"branca\"}", "{}"));
        assertEquals(true, ValidateUtil.PayloadValidate("{\"carro\":\"fusca\",\"cor\":\"branca\"}", "{\"carro\":\"fusca\",\"cor\":\"branca\"}"));
        assertEquals(false, ValidateUtil.PayloadValidate("{\"carro\":\"fusca\",\"cor\":\"branca\"}", "{\"carro\":\"fusca\""));
        assertEquals(false, ValidateUtil.PayloadValidate("{\"carro\":\"fusca\",\"cor\":\"branca\"}", "{\"carros\":\"fusca\",\"cor\":\"branca\"}"));
        assertEquals(true, ValidateUtil.PayloadValidate("{\"cor\":\"branca\",\"carro\":\"\"}", "{\"carro\":\"fusca\",\"cor\":\"preta\"}"));
        assertEquals(true, ValidateUtil.PayloadValidate("{}", "{}"));
        assertEquals(true, ValidateUtil.PayloadValidate("[]", "[]"));
    }
}