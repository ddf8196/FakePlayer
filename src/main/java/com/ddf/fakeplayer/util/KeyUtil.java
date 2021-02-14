package com.ddf.fakeplayer.util;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class KeyUtil {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final KeyPairGenerator KEY_PAIR_GENERATOR;

    static {
        try {
            KEY_PAIR_GENERATOR = KeyPairGenerator.getInstance("EC");
            KEY_PAIR_GENERATOR.initialize(384, SECURE_RANDOM);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static KeyPair generateKeyPair() {
        try {
            return KEY_PAIR_GENERATOR.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encodeKeyToBase64(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static String encodeKeyToBase64(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static PublicKey decodePublicKey(String base64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64));
        return KeyFactory.getInstance("EC").generatePublic(x509EncodedKeySpec);
    }

    public static PrivateKey decodePrivateKey(String base64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64));
        return KeyFactory.getInstance("EC").generatePrivate(pkcs8EncodedKeySpec);
    }
}
