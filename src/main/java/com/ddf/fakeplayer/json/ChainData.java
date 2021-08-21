package com.ddf.fakeplayer.json;

import com.ddf.fakeplayer.util.JwtUtil;
import com.ddf.fakeplayer.util.KeyUtil;
import com.google.gson.*;

import java.lang.annotation.*;
import java.security.KeyPair;
import java.time.Instant;
import java.util.Random;

public class ChainData {
    private static final Random rand = new Random();

    @Stage({0, 1, 2})
    private long exp;
    @Stage({0, 1, 2})
    private long nbf;
    @Stage({1, 2})
    private String iss;
    @Stage({1, 2})
    private long iat;

    @Stage({0, 1})
    private boolean certificateAuthority;
    @Stage({0, 1, 2})
    private String identityPublicKey;
    @Stage({1, 2})
    private long randomNonce;
    @Stage(2)
    private ExtraData extraData;

    public static ChainData createChainData() {
        ChainData chainData = new ChainData();
        chainData.nbf = Instant.now().getEpochSecond() - 100;
        chainData.exp = chainData.nbf + 100000;
        chainData.iat = chainData.nbf + 100;
        chainData.iss = "FakePlayer";

        chainData.certificateAuthority = true;
        chainData.randomNonce = rand.nextLong();
        return chainData;
    }

    public static String createFullChainJson(KeyPair clientKeyPair, KeyPair serverKeyPair, ExtraData extraData){
        KeyPair tempKeyPair = KeyUtil.generateKeyPair();

        JsonArray chain = new JsonArray();
        ChainData chainData = ChainData.createChainData();
        chainData.extraData = extraData;

        //JWT0 sign: clientPrivateKey, verify: clientPublicKey, identityPublicKey: serverPublicKey
        chainData.identityPublicKey = KeyUtil.encodeKeyToBase64(serverKeyPair.getPublic());
        chain.add(JwtUtil.createJwt(clientKeyPair, chainData.toJsonString(0)));

        //JWT1 sign: serverPrivateKey, verify: serverPublicKey, identityPublicKey: tempPublicKey
        chainData.identityPublicKey = KeyUtil.encodeKeyToBase64(tempKeyPair.getPublic());
        chain.add(JwtUtil.createJwt(serverKeyPair, chainData.toJsonString(1)));

        //JWT2 sign: tempPrivateKey, verify: tempPublicKey, identityPublicKey: clientPublicKey
        chainData.identityPublicKey = KeyUtil.encodeKeyToBase64(clientKeyPair.getPublic());
        chain.add(JwtUtil.createJwt(tempKeyPair, chainData.toJsonString(2)));

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("chain", chain);
        return jsonObject.toString();
    }

    public String toJsonString(int stage){
        Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                        if (!fieldAttributes.getDeclaringClass().equals(ChainData.class)) {
                            return false;
                        }
                        Stage s = fieldAttributes.getAnnotation(Stage.class);
                        if (s != null) {
                            for (int i : s.value())
                                if (i == stage) return false;
                        }
                        return true;
                    }
                    @Override
                    public boolean shouldSkipClass(Class<?> aClass) {
                        return stage != 2 && aClass.equals(ExtraData.class);
                    }
                }).create();
        return gson.toJson(this);
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getNbf() {
        return nbf;
    }

    public void setNbf(long nbf) {
        this.nbf = nbf;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public long getIat() {
        return iat;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    public boolean isCertificateAuthority() {
        return certificateAuthority;
    }

    public void setCertificateAuthority(boolean certificateAuthority) {
        this.certificateAuthority = certificateAuthority;
    }

    public String getIdentityPublicKey() {
        return identityPublicKey;
    }

    public void setIdentityPublicKey(String identityPublicKey) {
        this.identityPublicKey = identityPublicKey;
    }

    public long getRandomNonce() {
        return randomNonce;
    }

    public void setRandomNonce(long randomNonce) {
        this.randomNonce = randomNonce;
    }

    public ExtraData getExtraData() {
        return extraData;
    }

    public void setExtraData(ExtraData extraData) {
        this.extraData = extraData;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Stage {
        int[] value();
    }
}
