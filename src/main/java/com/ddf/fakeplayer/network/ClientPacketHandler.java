package com.ddf.fakeplayer.network;

import com.ddf.fakeplayer.Client;
import com.ddf.fakeplayer.actor.Actor;
import com.ddf.fakeplayer.actor.attribute.AttributeInstance;
import com.ddf.fakeplayer.actor.attribute.AttributeOperands;
import com.ddf.fakeplayer.actor.attribute.BaseAttributeMap;
import com.ddf.fakeplayer.actor.attribute.SharedAttributes;
import com.ddf.fakeplayer.actor.mob.Mob;
import com.ddf.fakeplayer.actor.player.LocalPlayer;
import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.actor.player.PlayerRespawnState;
import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.container.inventory.InventoryAction;
import com.ddf.fakeplayer.container.inventory.InventorySource;
import com.ddf.fakeplayer.container.inventory.PlayerInventoryProxy;
import com.ddf.fakeplayer.actor.player.RemotePlayer;
import com.ddf.fakeplayer.container.ContainerID;
import com.ddf.fakeplayer.container.inventory.transaction.ComplexInventoryTransaction;
import com.ddf.fakeplayer.container.inventory.transaction.InventoryTransactionError;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.json.*;
import com.ddf.fakeplayer.level.GameType;
import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.level.MultiPlayerLevel;
import com.ddf.fakeplayer.level.dimension.ChangeDimensionRequest;
import com.ddf.fakeplayer.util.*;
import com.nimbusds.jwt.SignedJWT;
import com.nukkitx.protocol.bedrock.data.AttributeData;
import com.nukkitx.protocol.bedrock.data.AuthoritativeMovementMode;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import io.netty.util.AsciiString;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientPacketHandler implements BedrockPacketHandler {
    private Logger logger;
    private Client client;
    private MultiPlayerLevel level;
    private LocalPlayer player;
    private boolean sync = false;
    private long runtimeID = 0;

    public ClientPacketHandler(Client client) {
        this.logger = Logger.getLogger();
        this.client = client;
        this.level = client.getLevel();
        this.player = client.getPlayer();
    }

    public void handleConnected(){
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
            throw new RuntimeException(e);
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

    @SuppressWarnings("deprecation")
    @Override
    public boolean handle(StartGamePacket packet) {
        client.getItemRegistry().initialize(packet.getItemEntries());
        level.setDefaultGameType(DataConverter.gameType(packet.getLevelGameType()));
        level.setDefaultSpawn(DataConverter.blockPos(packet.getDefaultSpawn()));
        level.setLevelId(packet.getLevelId());
        level.getLevelData().setLevelName(packet.getLevelName());
        level.setFinishedInitializing();

        player.setUniqueID(packet.getUniqueEntityId());
        player.setRuntimeID(packet.getRuntimeEntityId());
        player.setPlayerGameType(DataConverter.gameType(packet.getPlayerGameType()));
        player.setPos(DataConverter.vec3(packet.getPlayerPosition()));
        player.setRot(DataConverter.vec2(packet.getRotation()));
        player._setDimensionId(packet.getDimensionId());

        if (packet.getPlayerMovementSettings() != null) {
            player.setMovementMode(packet.getPlayerMovementSettings().getMovementMode());
            level.setServerAuthoritativeMovement(packet.getPlayerMovementSettings().getMovementMode() != AuthoritativeMovementMode.CLIENT);
        } else if (packet.getAuthoritativeMovementMode() != null){
            player.setMovementMode(packet.getAuthoritativeMovementMode());
            level.setServerAuthoritativeMovement(packet.getAuthoritativeMovementMode() != AuthoritativeMovementMode.CLIENT);
        }
        RequestChunkRadiusPacket response = new RequestChunkRadiusPacket();
        response.setRadius(client.getChunkRadius());
        client.sendPacket(response);
        return false;
    }

    @Override
    public boolean handle(PlayStatusPacket packet) {
        if (packet.getStatus() == PlayStatusPacket.Status.PLAYER_SPAWN) {
            SetLocalPlayerAsInitializedPacket response = new SetLocalPlayerAsInitializedPacket();
            response.setRuntimeEntityId(player.getRuntimeID());
            client.sendPacket(response);
            player.setInitialSpawnDone(true);
        }
        return false;
    }

    @Override
    public boolean handle(InventoryContentPacket packet) {
        switch (ContainerID.getByValue(packet.getContainerId())) {
            case CONTAINER_ID_INVENTORY: {
                PlayerInventoryProxy inventory = player.getSupplies();
                int i = 0;
                for (ItemData itemData : packet.getContents()) {
                    inventory.setItem(i++, DataConverter.itemStack(client.getItemRegistry(), itemData), ContainerID.CONTAINER_ID_INVENTORY);
                }
            }
            case CONTAINER_ID_ARMOR:
            case CONTAINER_ID_OFFHAND:
                break;
        }
        return false;
    }

    public boolean handle(PlayerHotbarPacket packet) {
        player.getSupplies().selectSlot(packet.getSelectedHotbarSlot(), ContainerID.getByValue(packet.getContainerId()));
        return false;
    }

    @Override
    public boolean handle(InventoryTransactionPacket packet) {
        ComplexInventoryTransaction complexInventoryTransaction = DataConverter.complexInventoryTransaction(client.getItemRegistry(), packet);
        //Inventory inventory = player.getSupplies().mInventory;
        if (complexInventoryTransaction != null) {
            InventoryTransactionError error = complexInventoryTransaction.handle(player, true);
            if (error == InventoryTransactionError.NoError) {
                return false;
            }
            logger.log(player.getName(), " InventoryTransaction处理失败: ", error.name());
            player.sendInventoryMismatch();
        }
        return false;
    }

    @Override
    public boolean handle(InventorySlotPacket packet) {
        return false;
    }

    @Override
    public boolean handle(TextPacket packet) {
        if (!client.getConfig().getPlayerData(this.player.getName()).isAllowChatMessageControl()
                || packet.getType() != TextPacket.Type.CHAT
                || packet.getSourceName() == null || packet.getSourceName().isEmpty()
                || packet.getMessage() == null || packet.getMessage().isEmpty()
                || packet.getMessage().length() <= player.getName().length()
                || !packet.getMessage().startsWith(player.getName())) {
            return false;
        }
        String name = packet.getMessage().substring(0, packet.getMessage().indexOf(" "));
        if (!name.equals(player.getName()))
            return false;
        try {
            String cmd = packet.getMessage().substring(player.getName().length() + 1).trim();
            if (cmd.equals("help")) {
                player.sendChatMessage(
                        "help - 查看帮助信息\n" +
                        "getPos - 获取假人当前坐标\n" +
                        "getInventory - 获取假人背包内容\n" +
                        "getSelectedSlot - 获取假人当前选择的快捷栏槽位\n" +
                        "selectSlot [0~8] - 设置假人选择的快捷栏槽位\n" +
                        "dropSlot - 丢弃假人当前选择的物品\n" +
                        "dropSlot [0~35] - 丢弃假人背包中指定槽位中的物品\n" +
                        "dropAll - 丢弃假人背包中全部的物品\n" +
                        "sync start - 开始将假人的坐标和视角与玩家同步\n" +
                        "sync stop - 停止将假人的坐标和视角与玩家同步"
                );
                return false;
            }
            if (cmd.startsWith("getPos")) {
                Vec3 pos = player.getPos();
                player.sendChatMessage(ColorFormat.GREEN + "[" + player.getDimensionId() + "] " + ColorFormat.AQUA + (int)pos.x + ", " + (int)(pos.y - player.mHeightOffset) + ", " + (int)pos.z + ColorFormat.RESET);
            } else if (cmd.startsWith("getInventory")) {
                boolean empty = true, first = true;
                StringBuilder builder = new StringBuilder("背包内容:\n");
                ArrayList<ItemStack> slots = player.getSupplies().getSlots();
                for (int i = 0, j = 0; i < player.getSupplies().getContainerSize(ContainerID.CONTAINER_ID_INVENTORY); i++) {
                    if (slots.get(i).toBoolean()) {
                        empty = false;
                        if (first)
                            first = false;
                        else
                            builder.append(ColorFormat.YELLOW)
                                    .append(", ")
                                    .append(ColorFormat.RESET);
                        if (j % 2 == 0 && j != 0)
                            builder.append("\n");
                        builder.append(ColorFormat.GREEN)
                                .append("[")
                                .append(i)
                                .append("]")
                                .append(ColorFormat.YELLOW)
                                .append("[")
                                .append(ColorFormat.AQUA)
                                .append(slots.get(i).getItem().getFullItemName())
                                .append(ColorFormat.YELLOW)
                                .append(", ")
                                .append(ColorFormat.GREEN)
                                .append(slots.get(i).getStackSize())
                                .append(ColorFormat.YELLOW)
                                .append("]")
                                .append(ColorFormat.RESET);
                        ++j;
                    }
                }
                if (!empty) {
                    player.sendChatMessage(builder.toString());
                } else {
                    player.sendChatMessage("空");
                }
            } else if (cmd.startsWith("getSelectedSlot")) {
                player.sendChatMessage(Integer.toString(player.getSelectedItemSlot()));
            } else if (cmd.startsWith("selectSlot")) {
                if (cmd.length() < 12) return false;
                int slot = Integer.parseInt(cmd.substring(11));
                player.getSupplies().selectSlot(slot, ContainerID.CONTAINER_ID_INVENTORY);
            } else if (cmd.startsWith("dropSlot")) {
                int slot;
                if (cmd.length() > 9)
                    slot = MathUtil.clamp(Integer.parseInt(cmd.substring(9)), 0, 35);
                else
                    slot = player.getSelectedItemSlot();
                player.getSupplies().dropSlot(slot, false, true, ContainerID.CONTAINER_ID_INVENTORY, false);
            } else if (cmd.startsWith("dropAll")) {
                ArrayList<ItemStack> slots = player.getSupplies().getSlots();
                for (int i = 0; i < player.getSupplies().getContainerSize(ContainerID.CONTAINER_ID_INVENTORY); i++) {
                    if (slots.get(i).toBoolean()) {
                        player.getSupplies().dropSlot(i, false, true, ContainerID.CONTAINER_ID_INVENTORY, false);
                    }
                }
            } else if (cmd.startsWith("sync")) {
                if (cmd.length() < 6) return false;
                String cmd2 = cmd.substring(5);
                if (cmd2.equals("start")) {
                    Player player = level.getPlayerByName(packet.getSourceName());
                    if (player == null)
                        return false;
                    sync = true;
                    runtimeID = player.getRuntimeID();
                } else if (cmd2.equals("stop")) {
                    sync = false;
                    runtimeID = 0;
                }
            }
        } catch (Throwable ignored) {}
        return false;
    }

    @Override
    public boolean handle(SetDefaultGameTypePacket packet) {
        level.setDefaultGameType(GameType.getByValue(packet.getGamemode()));
        return false;
    }

    @Override
    public boolean handle(SetPlayerGameTypePacket packet) {
        player.setPlayerGameType(GameType.getByValue(packet.getGamemode()));
        return false;
    }

    @Override
    public boolean handle(UpdatePlayerGameTypePacket packet) {
        Player player = level.getPlayer(packet.getEntityId());
        if (player != null) {
            player.setPlayerGameType(DataConverter.gameType(packet.getGameType()));
        }
        return false;
    }

    @Override
    public boolean handle(ChangeDimensionPacket packet) {
        ChangeDimensionRequest request = DataConverter.dimensionRequest(packet);
        request.mFromDimensionId = player.getDimensionId();
        request.mRespawn = false;
        level.requestPlayerChangeDimension(player, request);
        return false;
    }

    @Override
    public boolean handle(ShowCreditsPacket packet) {
        if (packet.getStatus() == ShowCreditsPacket.Status.START_CREDITS) {
            ShowCreditsPacket response = new ShowCreditsPacket();
            response.setStatus(ShowCreditsPacket.Status.END_CREDITS);
            response.setRuntimeEntityId(player.getRuntimeID());
            client.sendPacket(response);
        }
        return false;
    }

    @Override
    public boolean handle(SetSpawnPositionPacket packet) {
        switch (packet.getSpawnType()) {
            case WORLD_SPAWN:
                level.setDefaultSpawn(DataConverter.blockPos(packet.getBlockPosition()));
                return false;
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
        return false;
    }

    @Override
    public boolean handle(SetHealthPacket packet) {
        AttributeInstance health = player.getMutableAttribute(SharedAttributes.HEALTH);
        if (health != null && health != BaseAttributeMap.mInvalidInstance) {
            health.setDefaultValue(packet.getHealth(), AttributeOperands.OPERAND_CURRENT);
        }
        return false;
    }

    @Override
    public boolean handle(UpdateAttributesPacket packet) {
        Actor actor = level.getEntityByRuntimeId(packet.getRuntimeEntityId());
        if (actor == null) {
            return false;
        }
        for (AttributeData data : packet.getAttributes()) {
            AttributeInstance instance = actor.getMutableAttribute(data.getName());
            if (instance != null && instance != BaseAttributeMap.mInvalidInstance) {
                instance.setRange(data.getMaximum(), data.getDefaultValue(), data.getMaximum());
                instance.setDefaultValue(data.getValue(), AttributeOperands.OPERAND_CURRENT);
            }
        }
        return false;
    }

    @Override
    public boolean handle(RespawnPacket packet) {
        PlayerRespawnState state = DataConverter.playerRespawnState(packet.getState());
        if (state == PlayerRespawnState.ClientReadyToSpawn) {
            return false;
        }
        if (state == PlayerRespawnState.ReadyToSpawn) {
            if (Level.isUsableLevel(level)) {
                player.setClientRespawnPotentialPosition(DataConverter.vec3(packet.getPosition()));
                player./*setRespawnReady*/setTeleportDestination(DataConverter.vec3(packet.getPosition()));
                player.setClientRespawnState(state);
                player.respawn();
            }
            return false;
        }
        if (state == PlayerRespawnState.SearchingForSpawn) {
            player.setClientRespawnState(state);
            player.setClientRespawnPotentialPosition(DataConverter.vec3(packet.getPosition()));
        }
        return false;
    }

    @Override
    public boolean handle(MovePlayerPacket packet) {
        if (packet.getRuntimeEntityId() == player.getRuntimeID()) {
            player.setPos(DataConverter.vec3(packet.getPosition()));
            player.setRot(DataConverter.vec2(packet.getRotation()));
            player.setYHeadRot(packet.getRotation().getZ());
            return false;
        }
        Actor actor = level.getEntityByRuntimeId(packet.getRuntimeEntityId());
        if (!(actor instanceof Player))
            return false;
        actor.setPos(DataConverter.vec3(packet.getPosition()));
        actor.setRot(DataConverter.vec2(packet.getRotation()));
        ((Player) actor).setYHeadRot(packet.getRotation().getZ());
        if (sync && actor.getRuntimeID() == runtimeID) {
            player.setPos(DataConverter.vec3(packet.getPosition()));
            player.setRot(DataConverter.vec2(packet.getRotation()));
            player.setYHeadRot(packet.getRotation().getZ());
        }
        return false;
    }

    @Override
    public boolean handle(AddEntityPacket packet) {
        Mob mob = new Mob(client.getLevel());
        mob.setEntityType(packet.getEntityType());
        mob.setIdentifier(packet.getIdentifier());
        mob.getStateVectorComponentNonConst()
                .getPosDelta()
                .set(DataConverter.vec3(packet.getMotion()));
        mob.setPos(DataConverter.vec3(packet.getPosition()));
        mob.setRot(DataConverter.vec2(packet.getRotation()));
        mob.setYHeadRot(packet.getRotation().getZ());
        mob.setRuntimeID(packet.getRuntimeEntityId());
        mob.setUniqueID(packet.getUniqueEntityId());
        client.getLevel().addEntity(mob);
        return false;
    }

    @Override
    public boolean handle(AddPlayerPacket packet) {
        RemotePlayer player = new RemotePlayer(client.getLevel(), packet.getUsername(), packet.getUuid());
        player.setPos(DataConverter.vec3(packet.getMotion()));
        player.moveTo(DataConverter.vec3(packet.getPosition()), DataConverter.vec2(packet.getRotation()));
        player.setYHeadRot(packet.getRotation().getZ());

        player.setRuntimeID(packet.getRuntimeEntityId());
        player.setUniqueID(packet.getUniqueEntityId());

        client.getLevel().addEntity(player);
        return false;
    }

    @Override
    public boolean handle(SetTimePacket packet) {
        level.setTime(packet.getTime());
        return false;
    }

    @Override
    public boolean handle(TickSyncPacket packet) {
        level.getLevelData().setCurrentTick(packet.getResponseTimestamp());
        return false;
    }

    @Override
    public boolean handle(RemoveEntityPacket packet) {
        client.getLevel().removeEntity(packet.getUniqueEntityId());
        return false;
    }

    @Override
    public boolean handle(DisconnectPacket packet) {
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
        return false;
    }
}
