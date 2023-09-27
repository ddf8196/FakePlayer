package com.ddf.fakeplayer.json;

import com.ddf.fakeplayer.util.JwtUtil;
import com.ddf.fakeplayer.util.KeyUtil;
import com.google.gson.*;

import java.security.KeyPair;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChainData {
    private static final Random rand = new Random();
    private static final Gson gson = new Gson();

    private Long exp;
    private Long nbf;
    private String iss;
    private Long iat;

    private Boolean certificateAuthority;
    private String identityPublicKey;
    private Long randomNonce;
    private ExtraData extraData;

    public ChainData() {}

    public ChainData(ChainData chainData) {
        this.exp = chainData.exp;
        this.nbf = chainData.nbf;
        this.iss = chainData.iss;
        this.iat = chainData.iat;
        this.certificateAuthority = chainData.certificateAuthority;
        this.identityPublicKey = chainData.identityPublicKey;
        this.randomNonce = chainData.randomNonce;
        this.extraData = chainData.extraData;
    }

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

    public static List<String> createFullChain(KeyPair clientKeyPair, KeyPair serverKeyPair, ExtraData extraData) {
        List<String> fullChain = new ArrayList<>();
        KeyPair tempKeyPair = KeyUtil.generateKeyPair();

        //JWT0 sign: clientPrivateKey, verify: clientPublicKey, identityPublicKey: serverPublicKey
        ChainData chainData = ChainData.createChainData();
        chainData.identityPublicKey = KeyUtil.encodeKeyToBase64(serverKeyPair.getPublic());
        chainData.iss = null;
        chainData.iat = null;
        chainData.randomNonce = null;
        fullChain.add(JwtUtil.createJwt(clientKeyPair, chainData.toJsonString()));

        //JWT1 sign: serverPrivateKey, verify: serverPublicKey, identityPublicKey: tempPublicKey
        chainData = ChainData.createChainData();
        chainData.identityPublicKey = KeyUtil.encodeKeyToBase64(tempKeyPair.getPublic());
        fullChain.add(JwtUtil.createJwt(serverKeyPair, chainData.toJsonString()));

        //JWT2 sign: tempPrivateKey, verify: tempPublicKey, identityPublicKey: clientPublicKey
        chainData = ChainData.createChainData();
        chainData.identityPublicKey = KeyUtil.encodeKeyToBase64(clientKeyPair.getPublic());
        chainData.extraData = extraData;
        chainData.certificateAuthority = null;
        fullChain.add(JwtUtil.createJwt(tempKeyPair, chainData.toJsonString()));

        return fullChain;
    }

    public static String createFullChainJson(KeyPair clientKeyPair, KeyPair serverKeyPair, ExtraData extraData) {
        List<String> fullChain = createFullChain(clientKeyPair, serverKeyPair, extraData);
        JsonArray chain = new JsonArray();
        for (String data : fullChain)
            chain.add(data);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("chain", chain);
        return jsonObject.toString();
    }

    public String toJsonString(){
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
}
