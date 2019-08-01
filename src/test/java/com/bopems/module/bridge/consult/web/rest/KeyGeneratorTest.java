package com.bopems.module.bridge.consult.web.rest;

import org.springframework.security.crypto.codec.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Test;

public class KeyGeneratorTest {

    @Test
    public void gerarChave() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        System.out.println(Hex.encode(secretKey.getEncoded()));
    }
}

