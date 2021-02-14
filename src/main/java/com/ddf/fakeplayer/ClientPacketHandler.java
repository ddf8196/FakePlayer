package com.ddf.fakeplayer;

import com.ddf.fakeplayer.entity.Attribute;
import com.ddf.fakeplayer.entity.Entity;
import com.ddf.fakeplayer.entity.player.Player;
import com.ddf.fakeplayer.json.*;
import com.ddf.fakeplayer.util.JwtUtil;
import com.ddf.fakeplayer.util.KeyUtil;
import com.ddf.fakeplayer.world.World;
import com.nimbusds.jwt.SignedJWT;
import com.nukkitx.protocol.bedrock.data.AttributeData;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import io.netty.util.AsciiString;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Base64;

public class ClientPacketHandler implements BedrockPacketHandler {
    private Client client;
    private World world;
    private Player player;

    public ClientPacketHandler(Client client) {
        this.client = client;
        this.world = client.getWorld();
        this.player = client.getPlayer();
    }

    public void handleConnected(){
        System.out.println(player.getName() + " 已连接");
        byte[] chainData = ChainData.createFullChainJson(
                client.getClientKeyPair(),
                client.getServerKeyPair(),
                client.createExtraData()
        ).getBytes(StandardCharsets.UTF_8);

        byte[] skinData = JwtUtil.createJwt(
                client.getClientKeyPair(),
                client.createSkinData().toJsonString()
        ).getBytes(StandardCharsets.UTF_8);

        LoginPacket loginPacket = new LoginPacket();
        loginPacket.setProtocolVersion(client.getPacketCodec().getProtocolVersion());
        loginPacket.setChainData(new AsciiString(chainData));
        loginPacket.setSkinData(new AsciiString(skinData));
        client.sendPacket(loginPacket);
    }
    @Override
    public boolean handle(ServerToClientHandshakePacket packet) {
        try {
            SignedJWT jwt = SignedJWT.parse(packet.getJwt());
            PublicKey serverPublicKey = KeyUtil.decodePublicKey(jwt.getHeader().getX509CertURL().toASCIIString());
            byte[] salt = Base64.getDecoder().decode(jwt.getJWTClaimsSet().getStringClaim("salt"));
            SecretKey key = EncryptionUtils.getSecretKey(client.getClientKeyPair().getPrivate(), serverPublicKey, salt);
            client.getSession().enableEncryption(key);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | ParseException | InvalidKeyException e) {
            e.printStackTrace();
        }

        ClientToServerHandshakePacket clientToServerHandshake = new ClientToServerHandshakePacket();
        client.getSession().sendPacketImmediately(clientToServerHandshake);
        return false;
    }

    @Override
    public boolean handle(ResourcePacksInfoPacket packet) {
        ResourcePackClientResponsePacket response = new ResourcePackClientResponsePacket();
        response.setStatus(ResourcePackClientResponsePacket.Status.HAVE_ALL_PACKS);
        client.sendPacket(response);
        return false;
    }

    @Override
    public boolean handle(ResourcePackStackPacket packet) {
        ResourcePackClientResponsePacket response = new ResourcePackClientResponsePacket();
        response.setStatus(ResourcePackClientResponsePacket.Status.COMPLETED);
        client.sendPacket(response);
        return false;
    }

    @Override
    public boolean handle(StartGamePacket packet) {
        player.setUniqueEntityId(packet.getUniqueEntityId());
        player.setRuntimeEntityId(packet.getRuntimeEntityId());
        player.setPosition(packet.getPlayerPosition());
        player.setMovementMode(packet.getAuthoritativeMovementMode());

        RequestChunkRadiusPacket response = new RequestChunkRadiusPacket();
        response.setRadius(client.getChunkRadius());
        client.sendPacket(response);
        return false;
    }

    @Override
    public boolean handle(PlayStatusPacket packet) {
        if (packet.getStatus() == PlayStatusPacket.Status.PLAYER_SPAWN) {
            SetLocalPlayerAsInitializedPacket response = new SetLocalPlayerAsInitializedPacket();
            response.setRuntimeEntityId(player.getRuntimeEntityId());
            client.sendPacket(response);
            player.setInitialized(true);
        }
        return false;
    }

    @Override
    public boolean handle(SetSpawnPositionPacket packet) {
        player.setSpawnPosition(packet.getSpawnPosition());
        return false;
    }

    @Override
    public boolean handle(SetHealthPacket packet) {
        player.setAttribute(Entity.HEALTH, packet.getHealth());
        return false;
    }

    @Override
    public boolean handle(UpdateAttributesPacket packet) {
        Entity entity = world.getEntityByRuntimeId(packet.getRuntimeEntityId());
        if (entity == null) {
            return false;
        }
        for (AttributeData data : packet.getAttributes()) {
            Attribute attribute = new Attribute(data);
            entity.setAttribute(attribute);
        }
        return false;
    }

    @Override
    public boolean handle(MovePlayerPacket packet) {
        if (packet.getRuntimeEntityId() == player.getRuntimeEntityId()) {
            player.setRotation(packet.getRotation());
            player.setPosition(packet.getPosition());
        }
        return false;
    }

    @Override
    public boolean handle(AddEntityPacket packet) {
        Entity entity = new Entity();
        entity.setEntityType(packet.getEntityType());
        entity.setIdentifier(packet.getIdentifier());
        entity.setMotion(packet.getMotion());
        entity.setPosition(packet.getPosition());
        entity.setRotation(packet.getRotation());
        entity.setRuntimeEntityId(packet.getRuntimeEntityId());
        entity.setUniqueEntityId(packet.getUniqueEntityId());

        client.getWorld().addEntity(entity);
        return false;
    }

    @Override
    public boolean handle(AddPlayerPacket packet) {
        Player player = new Player(packet.getUsername(), packet.getUuid());
        player.setMotion(packet.getMotion());
        player.setPosition(packet.getPosition());
        player.setRotation(packet.getRotation());
        player.setRuntimeEntityId(packet.getRuntimeEntityId());
        player.setUniqueEntityId(packet.getUniqueEntityId());

        client.getWorld().addEntity(player);
        return false;
    }

    @Override
    public boolean handle(SetTimePacket packet) {
        world.setTime(packet.getTime());
        return false;
    }

    @Override
    public boolean handle(RemoveEntityPacket packet) {
        client.getWorld().removeEntity(packet.getUniqueEntityId());
        return false;
    }

    @Override
    public boolean handle(DisconnectPacket packet) {
        client.disconnect();
        return false;
    }
}
