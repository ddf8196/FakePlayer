package com.ddf.fakeplayer.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;

import java.net.URI;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;

public class JwtUtil {
    public static String createJwt(KeyPair keyPair, String payloadString) {
        try {
            JWSSigner signer = new ECDSASigner((ECPrivateKey) keyPair.getPrivate());
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES384)
                    .x509CertURL(new URI(KeyUtil.encodeKeyToBase64(keyPair.getPublic())))
                    .build();
            Payload payload = new Payload(payloadString);

            JWSObject jws = new JWSObject(header, payload);
            jws.sign(signer);

            return jws.serialize();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
