package com.ddf.fakeplayer.util;

//import com.nimbusds.jose.*;
//import com.nimbusds.jose.crypto.ECDSASigner;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwx.HeaderParameterNames;

import java.security.KeyPair;

public class JwtUtil {
    public static String createJwt(KeyPair keyPair, String payloadString) {
        try {
            JsonWebSignature jws = new JsonWebSignature();
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.ECDSA_USING_P384_CURVE_AND_SHA384);
            jws.setHeader(HeaderParameterNames.X509_URL, KeyUtil.encodeKeyToBase64(keyPair.getPublic()));
            jws.setKey(keyPair.getPrivate());
            jws.setPayload(payloadString);
            jws.sign();

            return jws.getCompactSerialization();

//            JWSSigner signer = new ECDSASigner((ECPrivateKey) keyPair.getPrivate());
//            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES384)
//                    .x509CertURL(new URI(KeyUtil.encodeKeyToBase64(keyPair.getPublic())))
//                    .build();
//            Payload payload = new Payload(payloadString);
//
//            JWSObject jws = new JWSObject(header, payload);
//            jws.sign(signer);
//
//            return jws.serialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
