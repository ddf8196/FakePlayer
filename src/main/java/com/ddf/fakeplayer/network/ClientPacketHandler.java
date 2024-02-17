package com.ddf.fakeplayer.network;

import com.ddf.fakeplayer.block.*;
import com.ddf.fakeplayer.client.Client;
import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.actor.ActorDataIDs;
import com.ddf.fakeplayer.actor.SynchedActorData;
import com.ddf.fakeplayer.actor.attribute.AttributeInstance;
import com.ddf.fakeplayer.actor.attribute.AttributeOperands;
import com.ddf.fakeplayer.actor.attribute.BaseAttributeMap;
import com.ddf.fakeplayer.actor.attribute.SharedAttributes;
import com.ddf.fakeplayer.actor.mob.Mob;
import com.ddf.fakeplayer.actor.player.*;
import com.ddf.fakeplayer.container.inventory.InventoryAction;
import com.ddf.fakeplayer.container.inventory.InventorySource;
import com.ddf.fakeplayer.container.inventory.PlayerInventoryProxy;
import com.ddf.fakeplayer.container.ContainerID;
import com.ddf.fakeplayer.container.inventory.transaction.ComplexInventoryTransaction;
import com.ddf.fakeplayer.container.inventory.transaction.InventoryTransactionError;
import com.ddf.fakeplayer.item.ItemRegistry;
import com.ddf.fakeplayer.json.*;
import com.ddf.fakeplayer.level.GameType;
import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.level.MultiPlayerLevel;
import com.ddf.fakeplayer.level.dimension.ChangeDimensionRequest;
import com.ddf.fakeplayer.level.generator.GeneratorType;
import com.ddf.fakeplayer.util.*;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.AttributeData;
import org.cloudburstmc.protocol.bedrock.data.AuthoritativeMovementMode;
import org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataType;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.bedrock.util.EncryptionUtils;
import org.cloudburstmc.protocol.common.DefinitionRegistry;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.jose4j.json.JsonUtil;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwx.HeaderParameterNames;
import org.jose4j.lang.JoseException;

import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ClientPacketHandler implements BedrockPacketHandler {
    private Logger logger;
    private Client client;
    private MultiPlayerLevel level;
    private FakePlayer player;
    private List<BedrockPacket> unhandledPackets = new ArrayList<>();
    private boolean startGamePacketReceived = false;

    private Consumer<String> disconnectCallback;

    public ClientPacketHandler(Client client) {
        this.logger = Logger.getLogger();
        this.client = client;
        this.level = client.getLevel();
        this.player = client.getPlayer();
    }

    public void handleConnected(){
        LoginPacket loginPacket = new LoginPacket();
        loginPacket.setProtocolVersion(client.getPacketCodec().getProtocolVersion());
        loginPacket.getChain().addAll(ChainData.createFullChain(
                client.getClientKeyPair(),
                client.getServerKeyPair(),
                client.createExtraData()
        ));
        String skinData = client.createSkinData().toJsonString();
        loginPacket.setExtra(JwtUtil.createJwt(
                client.getClientKeyPair(),
                skinData));
        client.sendPacket(loginPacket);
    }

    public void setDisconnectCallback(Consumer<String> disconnectCallback) {
        this.disconnectCallback = disconnectCallback;
    }

    @Override
    public void onDisconnect(String reason) {
        if (disconnectCallback != null)
            disconnectCallback.accept(reason);
//        client.runOnClientThread(() -> {
//            client.setState(Client.State.DISCONNECTED);
//            logger.logI18N("log.client.disconnected", playerName, disconnectReason.name());
//            reconnectOrStop(address, port);
//        }));
    }

    @Override
    public PacketSignal handlePacket(BedrockPacket packet) {
        return BedrockPacketHandler.super.handlePacket(packet);
    }

    @Override
    public PacketSignal handle(ServerToClientHandshakePacket packet) {
        try {
            JsonWebSignature jws = (JsonWebSignature) JsonWebSignature.fromCompactSerialization(packet.getJwt());
            PublicKey serverPublicKey = KeyUtil.decodePublicKey(jws.getHeader(HeaderParameterNames.X509_URL));
            byte[] salt = Base64.getDecoder().decode(JsonUtil.parseJson(jws.getUnverifiedPayload()).get("salt").toString());
            SecretKey key = EncryptionUtils.getSecretKey(client.getClientKeyPair().getPrivate(), serverPublicKey, salt);
            client.getSession().enableEncryption(key);
//            SignedJWT jwt = SignedJWT.parse(packet.getJwt());
//            PublicKey serverPublicKey = KeyUtil.decodePublicKey(jwt.getHeader().getX509CertURL().toASCIIString());
//            byte[] salt = Base64.getDecoder().decode(jwt.getJWTClaimsSet().getStringClaim("salt"));
//            SecretKey key = EncryptionUtils.getSecretKey(client.getClientKeyPair().getPrivate(), serverPublicKey, salt);
//            client.getSession().enableEncryption(key);
        } catch (JoseException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        ClientToServerHandshakePacket clientToServerHandshake = new ClientToServerHandshakePacket();
        client.getSession().sendPacketImmediately(clientToServerHandshake);
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePacksInfoPacket packet) {
        ResourcePackClientResponsePacket response = new ResourcePackClientResponsePacket();
        response.setStatus(ResourcePackClientResponsePacket.Status.HAVE_ALL_PACKS);
        client.sendPacket(response);
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePackStackPacket packet) {
        ResourcePackClientResponsePacket response = new ResourcePackClientResponsePacket();
        response.setStatus(ResourcePackClientResponsePacket.Status.COMPLETED);
        client.sendPacket(response);
        return PacketSignal.HANDLED;
    }

    @SuppressWarnings("deprecation")
    @Override
    public PacketSignal handle(StartGamePacket packet) {
        client.getSession().getPeer().getCodecHelper().setItemDefinitions(new DefinitionRegistry<ItemDefinition>() {
            @Override
            public ItemDefinition getDefinition(int runtimeId) {
                return new ItemDefinition() {
                    @Override
                    public boolean isComponentBased() {
                        return false;
                    }

                    @Override
                    public String getIdentifier() {
                        return null;
                    }

                    @Override
                    public int getRuntimeId() {
                        return runtimeId;
                    }
                };
            }

            @Override
            public boolean isRegistered(ItemDefinition definition) {
                return false;
            }
        });

        client.getSession().getPeer().getCodecHelper().setBlockDefinitions(new DefinitionRegistry<BlockDefinition>() {
            @Override
            public BlockDefinition getDefinition(int runtimeId) {
                return () -> runtimeId;
            }

            @Override
            public boolean isRegistered(BlockDefinition definition) {
                return false;
            }
        });

        ItemRegistry.init(packet.getItemDefinitions());

        if (packet.getBlockPalette() != null) {
            level.getGlobalBlockPalette().initFromNbtMapList(packet.getBlockPalette());
        }

        level.setDefaultGameType(DataConverter.gameType(packet.getLevelGameType()));
        level.setDefaultSpawn(DataConverter.blockPos(packet.getDefaultSpawn()));
        level.setLevelId(packet.getLevelId());
        level.getLevelData().setLevelName(packet.getLevelName());
        level.getLevelData().setCurrentTick(packet.getCurrentTick());
        level.getLevelData().setGenerator(GeneratorType.values()[packet.getGeneratorId()]);

        level.setFinishedInitializing();

        player.setUniqueID(packet.getUniqueEntityId());
        player.setRuntimeID(packet.getRuntimeEntityId());
        player.setPlayerGameType(DataConverter.gameType(packet.getPlayerGameType()));
        player.setPos(DataConverter.vec3(packet.getPlayerPosition()));
        player.setRot(DataConverter.vec2(packet.getRotation()));
        player._setDimensionId(packet.getDimensionId());

        player.setMovementMode(packet.getAuthoritativeMovementMode());
        level.setServerAuthoritativeMovement(packet.getAuthoritativeMovementMode() != AuthoritativeMovementMode.CLIENT);

        RequestChunkRadiusPacket response = new RequestChunkRadiusPacket();
        response.setRadius(client.getChunkRadius());
        client.sendPacket(response);

        startGamePacketReceived = true;
        for (BedrockPacket p : unhandledPackets) {
            p.handle(this);
        }
        unhandledPackets.clear();
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LevelChunkPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayStatusPacket packet) {
        if (packet.getStatus() == PlayStatusPacket.Status.LOGIN_SUCCESS) {
            ClientCacheStatusPacket packet1 = new ClientCacheStatusPacket();
            packet1.setSupported(false);
            client.sendPacket(packet1);
        }
        if (packet.getStatus() == PlayStatusPacket.Status.PLAYER_SPAWN) {
            SetLocalPlayerAsInitializedPacket response = new SetLocalPlayerAsInitializedPacket();
            response.setRuntimeEntityId(player.getRuntimeID());
            client.sendPacket(response);
            player.setInitialSpawnDone(true);
        }
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(InventoryContentPacket packet) {
        if (!startGamePacketReceived) {
            unhandledPackets.add(packet);
            return PacketSignal.UNHANDLED;
        }
        switch (ContainerID.getByValue(packet.getContainerId())) {
            case CONTAINER_ID_INVENTORY: {
                PlayerInventoryProxy inventory = player.getSupplies();
                inventory.createTransactionContext((container, slot, oldItem, newItem) -> {
                    InventoryAction action = new InventoryAction(InventorySource.fromContainerWindowID(ContainerID.CONTAINER_ID_INVENTORY), slot, oldItem, newItem);
                    player.getTransactionManager().addExpectedAction(action);
                }, () -> {
                    int i = 0;
                    for (ItemData itemData : packet.getContents()) {
                        inventory.setItem(i++, DataConverter.itemStack(itemData), ContainerID.CONTAINER_ID_INVENTORY);
                    }
                });
            }
            case CONTAINER_ID_ARMOR:
            case CONTAINER_ID_OFFHAND:
                break;
        }
        return PacketSignal.HANDLED;
    }

    public PacketSignal handle(PlayerHotbarPacket packet) {
        player.getSupplies().selectSlot(packet.getSelectedHotbarSlot(), ContainerID.getByValue(packet.getContainerId()));
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateBlockPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(InventoryTransactionPacket packet) {
        if (!startGamePacketReceived) {
            unhandledPackets.add(packet);
            return PacketSignal.UNHANDLED;
        }
        ComplexInventoryTransaction complexInventoryTransaction = DataConverter.complexInventoryTransaction(packet);
        //Inventory inventory = player.getSupplies().mInventory;
        if (complexInventoryTransaction != null) {
            InventoryTransactionError error = complexInventoryTransaction.handle(player, true);
            if (error == InventoryTransactionError.NoError) {
                return PacketSignal.HANDLED;
            }
            logger.log(player.getName(), " InventoryTransaction处理失败: ", error.name());
            player.sendInventoryMismatch();
        }
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(InventorySlotPacket packet) {
        if (!startGamePacketReceived) {
            unhandledPackets.add(packet);
            return PacketSignal.UNHANDLED;
        }
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(TextPacket packet) {
        if (packet.getType() == TextPacket.Type.CHAT
                && packet.getSourceName() != null && !packet.getSourceName().isEmpty()
                && packet.getMessage() != null && !packet.getMessage().isEmpty()){
            this.player.onPlayerChat(packet.getSourceName(), packet.getMessage(), packet.getXuid(), packet.getPlatformChatId());
        }
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetDefaultGameTypePacket packet) {
        level.setDefaultGameType(GameType.getByValue(packet.getGamemode()));
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetPlayerGameTypePacket packet) {
        player.setPlayerGameType(GameType.getByValue(packet.getGamemode()));
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdatePlayerGameTypePacket packet) {
        Player player = level.getPlayer(packet.getEntityId());
        if (player != null) {
            player.setPlayerGameType(DataConverter.gameType(packet.getGameType()));
        }
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ChangeDimensionPacket packet) {
        ChangeDimensionRequest request = DataConverter.dimensionRequest(packet);
        request.mFromDimensionId = player.getDimensionId();
        request.mRespawn = false;
        level.requestPlayerChangeDimension(player, request);
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ShowCreditsPacket packet) {
        if (packet.getStatus() == ShowCreditsPacket.Status.START_CREDITS) {
            ShowCreditsPacket response = new ShowCreditsPacket();
            response.setStatus(ShowCreditsPacket.Status.END_CREDITS);
            response.setRuntimeEntityId(player.getRuntimeID());
            client.sendPacket(response);
        }
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetSpawnPositionPacket packet) {
        switch (packet.getSpawnType()) {
            case WORLD_SPAWN:
                level.setDefaultSpawn(DataConverter.blockPos(packet.getBlockPosition()));
                return PacketSignal.HANDLED;
            case PLAYER_SPAWN:
                BlockPos blockPos = DataConverter.blockPos(packet.getBlockPosition());
                BlockPos spawnPos = DataConverter.blockPos(packet.getSpawnPosition());
                if (!blockPos.equals(BlockPos.MIN)) {
                    player.setBedRespawnPosition(blockPos);
                }
                if (!spawnPos.equals(BlockPos.MIN)) {
                    player.setRespawnPosition(spawnPos, false);
                } else if (!blockPos.equals(BlockPos.MIN)) {
                    player.setRespawnPosition(blockPos, false);
                }
        }
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetHealthPacket packet) {
        AttributeInstance health = player.getMutableAttribute(SharedAttributes.HEALTH);
        if (health != null && health != BaseAttributeMap.mInvalidInstance) {
            health.setDefaultValue(packet.getHealth(), AttributeOperands.OPERAND_CURRENT);
        }
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateAttributesPacket packet) {
        Actor actor = level.getEntityByRuntimeId(packet.getRuntimeEntityId());
        if (actor == null) {
            return PacketSignal.UNHANDLED;
        }
        for (AttributeData data : packet.getAttributes()) {
            AttributeInstance instance = actor.getMutableAttribute(data.getName());
            if (instance != null && instance != BaseAttributeMap.mInvalidInstance) {
                instance.setRange(data.getMaximum(), data.getDefaultValue(), data.getMaximum());
                instance.setDefaultValue(data.getValue(), AttributeOperands.OPERAND_CURRENT);
            }
        }
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RespawnPacket packet) {
        PlayerRespawnState state = DataConverter.playerRespawnState(packet.getState());
        if (state == PlayerRespawnState.ClientReadyToSpawn) {
            return PacketSignal.HANDLED;
        }
        if (state == PlayerRespawnState.ReadyToSpawn) {
            if (Level.isUsableLevel(level)) {
                player.setClientRespawnPotentialPosition(DataConverter.vec3(packet.getPosition()));
                player./*setRespawnReady*/setTeleportDestination(DataConverter.vec3(packet.getPosition()));
                player.setClientRespawnState(state);
                player.respawn();
            }
            return PacketSignal.HANDLED;
        }
        if (state == PlayerRespawnState.SearchingForSpawn) {
            player.setClientRespawnState(state);
            player.setClientRespawnPotentialPosition(DataConverter.vec3(packet.getPosition()));
        }
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MovePlayerPacket packet) {
        if (packet.getRuntimeEntityId() == player.getRuntimeID()) {
            player.setPos(DataConverter.vec3(packet.getPosition()));
            player.setRot(DataConverter.vec2(packet.getRotation()));
            player.setYHeadRot(packet.getRotation().getZ());
            return PacketSignal.HANDLED;
        }
        Actor actor = level.getEntityByRuntimeId(packet.getRuntimeEntityId());
        if (!(actor instanceof Player))
            return PacketSignal.UNHANDLED;
        actor.setPos(DataConverter.vec3(packet.getPosition()));
        actor.setRot(DataConverter.vec2(packet.getRotation()));
        ((Player) actor).setYHeadRot(packet.getRotation().getZ());
        if (player.sync && actor.getRuntimeID() == player.syncRuntimeID) {
            player.setPos(DataConverter.vec3(packet.getPosition()));
            player.setRot(DataConverter.vec2(packet.getRotation()));
            player.setYHeadRot(packet.getRotation().getZ());
        }
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddEntityPacket packet) {
        Mob mob = new Mob(client.getLevel());
        mob.setEntityType(packet.getEntityType());
        mob.setIdentifier(packet.getIdentifier());
        mob.getStateVectorComponentNonConst()
                .getPosDelta()
                .set(DataConverter.vec3(packet.getMotion()));
        mob.setPos(DataConverter.vec3(packet.getPosition()));
        mob.setRot(DataConverter.vec2(packet.getRotation()));
        mob.setYHeadRot(packet.getHeadRotation());
        mob.setRuntimeID(packet.getRuntimeEntityId());
        mob.setUniqueID(packet.getUniqueEntityId());
        client.getLevel().addEntity(mob);
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddPlayerPacket packet) {
        RemotePlayer player = new RemotePlayer(client.getLevel(), packet.getUsername(), packet.getUuid());
        player.setPos(DataConverter.vec3(packet.getMotion()));
        player.moveTo(DataConverter.vec3(packet.getPosition()), DataConverter.vec2(packet.getRotation()));
        player.setYHeadRot(packet.getRotation().getZ());

        player.setRuntimeID(packet.getRuntimeEntityId());
        player.setUniqueID(packet.getUniqueEntityId());

        client.getLevel().addEntity(player);
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetEntityDataPacket packet) {
        Actor actor = level.getEntityByRuntimeId(packet.getRuntimeEntityId());
        if (actor == null)
            return PacketSignal.UNHANDLED;
        for (Map.Entry<EntityDataType<?>, Object> entry : packet.getMetadata().entrySet()) {
            SynchedActorData entityData = actor.getEntityData();
            Object data = entry.getValue();
            ActorDataIDs actorDataIDs = DataConverter.actorDataIDs(entry.getKey());
            if (actorDataIDs == null)
                continue;
            if (entry.getKey() == EntityDataTypes.FLAGS || entry.getKey() == EntityDataTypes.FLAGS_2) {
                continue;
            }
            switch (DataConverter.dataItemType(entry.getKey())) {
                case Byte:
                    if (entityData._find(actorDataIDs.ordinal()) != null && data instanceof Byte)
                        entityData.set(actorDataIDs.ordinal(), (Byte) data);
                    break;
                case Short:
                    if (entityData._find(actorDataIDs.ordinal()) != null && data instanceof Short)
                        entityData.set(actorDataIDs.ordinal(), (Short) data);
                    break;
                case Int_1:
                    if (entityData._find(actorDataIDs.ordinal()) != null && data instanceof Integer)
                        entityData.set(actorDataIDs.ordinal(), (Integer) data);
                    break;
                case Float_1:
                    if (entityData._find(actorDataIDs.ordinal()) != null && data instanceof Float)
                        entityData.set(actorDataIDs.ordinal(), (Float) data);
                    break;
                case String_0:
                    if (entityData._find(actorDataIDs.ordinal()) != null && data instanceof String)
                        entityData.set(actorDataIDs.ordinal(), (String) data);
                    break;
                case CompoundTag:
                    if (entityData._find(actorDataIDs.ordinal()) != null && data instanceof NbtMap)
                        entityData.set(actorDataIDs.ordinal(), DataConverter.compoundTag((NbtMap) data));
                    break;
                case Pos:
                    if (entityData._find(actorDataIDs.ordinal()) != null && data instanceof Vector3i)
                        entityData.set(actorDataIDs.ordinal(), DataConverter.blockPos((Vector3i) data));
                    break;
                case Int64:
                    if (entityData._find(actorDataIDs.ordinal()) != null && data instanceof Long)
                        entityData.set(actorDataIDs.ordinal(), (Long) data);
                    break;
                case Vec3:
                    if (entityData._find(actorDataIDs.ordinal()) != null && data instanceof Vector3f)
                        entityData.set(actorDataIDs.ordinal(), DataConverter.vec3((Vector3f) data));
                    break;
            }
        }
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetEntityLinkPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetTimePacket packet) {
        level.setTime(packet.getTime());
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(TickSyncPacket packet) {
        level.getLevelData().setCurrentTick(packet.getResponseTimestamp());
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RemoveEntityPacket packet) {
        client.getLevel().removeEntity(packet.getUniqueEntityId());
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(DisconnectPacket packet) {
        client.disconnect();
        logger.log(player.getName(), " 正在断开连接: ", packet.getKickMessage());
        switch (packet.getKickMessage()) {
            case "disconnectionScreen.outdatedClient":
            case "disconnectionScreen.outdatedServer":
                logger.log("协议版本错误");
                client.stop();
                break;
            case "disconnectionScreen.notAuthenticated":
                logger.log("请检查公钥是否已正确添加到服务器");
                client.stop();
                break;
            case "disconnectionScreen.notAllowed":
                logger.log("请检查是否已将 ", player.getName(), " 添加至白名单");
                client.stop();
                break;
            case "disconnectionScreen.serverFull":
                logger.log("服务器已满");
                client.stop();
                break;
        }
        return PacketSignal.HANDLED;
    }
}
