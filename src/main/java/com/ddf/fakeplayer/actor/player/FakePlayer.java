package com.ddf.fakeplayer.actor.player;

import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.client.Client;
import com.ddf.fakeplayer.container.ContainerID;
import com.ddf.fakeplayer.container.inventory.transaction.ComplexInventoryTransaction;
import com.ddf.fakeplayer.container.inventory.transaction.ItemUseInventoryTransaction;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.item.VanillaItems;
import com.ddf.fakeplayer.js.JSLoader;
import com.ddf.fakeplayer.js.Script;
import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.network.NetworkBlockPosition;
import com.ddf.fakeplayer.util.*;
import com.ddf.fakeplayer.util.command.CommandDispatcherUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.nukkitx.math.vector.Vector2f;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.data.ClientPlayMode;
import com.nukkitx.protocol.bedrock.data.InputMode;
import com.nukkitx.protocol.bedrock.data.PlayerActionType;
import com.nukkitx.protocol.bedrock.packet.AnimatePacket;
import com.nukkitx.protocol.bedrock.packet.PlayerActionPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerAuthInputPacket;
import com.nukkitx.protocol.bedrock.packet.TextPacket;

import java.io.IOException;
import java.util.*;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

import static java.util.Collections.singletonList;

public class FakePlayer extends LocalPlayer {
    private long playerAuthInputTick = 0;
    public boolean sync = false;
    public long syncRuntimeID = 0;
    private final List<FakePlayer.OnPlayerChatListener> onPlayerChatListeners = Collections.synchronizedList(new ArrayList<>());
    private final CommandDispatcher<Player> chatCommandDispatcher = new CommandDispatcher<>();
    private final List<Script> scripts = new ArrayList<>();
    private final Queue<String> scriptsToLoad = new LinkedList<>();

    public FakePlayer(Level level, String name, UUID uuid, Client client) {
        super(level, name, uuid, client);
        this.addOnPlayerChatListener((source, message, xuid, platformChatId) -> {
            if (!client.isAllowChatMessageControl()
                    || message.length() <= super.getName().length()
                    || !message.startsWith(super.getName())) {
                return;
            }
            String cmd = message.substring(super.getName().length()).trim();
            ParseResults<Player> parseResults = chatCommandDispatcher.parse(cmd, level.getPlayerByName(source));
            try {
                chatCommandDispatcher.execute(parseResults);
            } catch (CommandSyntaxException ignored) {}
        });

        registerChatCommand("help", context -> {
            this.sendChatMessage(
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
            return Command.SINGLE_SUCCESS;
        });
        registerChatCommand("getPos", context -> {
            Vec3 pos = super.getPos();
            this.sendChatMessage(ColorFormat.GREEN + "[" + super.getDimensionId() + "] " + ColorFormat.AQUA + (int)pos.x + ", " + (int)(pos.y - super.mHeightOffset) + ", " + (int)pos.z + ColorFormat.RESET);
            return Command.SINGLE_SUCCESS;
        });
        registerChatCommand("getInventory", context -> {
            boolean empty = true, first = true;
            StringBuilder builder = new StringBuilder("背包内容:\n");
            ArrayList<ItemStack> slots = super.getSupplies().getSlots();
            for (int i = 0, j = 0; i < super.getSupplies().getContainerSize(ContainerID.CONTAINER_ID_INVENTORY); i++) {
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
                this.sendChatMessage(builder.toString());
            } else {
                this.sendChatMessage("空");
            }
            return Command.SINGLE_SUCCESS;
        });
        registerChatCommand("getSelectedSlot", context -> {
            this.sendChatMessage(Integer.toString(super.getSelectedItemSlot()));
            return Command.SINGLE_SUCCESS;
        });
        registerChatCommand("selectSlot",
                Collections.singletonList("slot"),
                Collections.singletonList(integer(0, 8)),
                context -> {
                    int slot = context.getArgument("slot", int.class);
                    super.getSupplies().selectSlot(slot, ContainerID.CONTAINER_ID_INVENTORY);
                    return Command.SINGLE_SUCCESS;
                });
        registerChatCommand("dropSlot", context -> {
            int slot = super.getSelectedItemSlot();
            super.getSupplies().dropSlot(slot, false, true, ContainerID.CONTAINER_ID_INVENTORY, false);
            return Command.SINGLE_SUCCESS;
        });
        registerChatCommand("dropSlot",
                singletonList("slot"),
                singletonList(integer(0, 8)),
                context -> {
                    int slot = context.getArgument("slot", int.class);
                    super.getSupplies().dropSlot(slot, false, true, ContainerID.CONTAINER_ID_INVENTORY, false);
                    return Command.SINGLE_SUCCESS;
                });
        registerChatCommand("dropAll", context -> {
            ArrayList<ItemStack> slots = super.getSupplies().getSlots();
            for (int i = 0; i < super.getSupplies().getContainerSize(ContainerID.CONTAINER_ID_INVENTORY); i++) {
                if (slots.get(i).toBoolean()) {
                    super.getSupplies().dropSlot(i, false, true, ContainerID.CONTAINER_ID_INVENTORY, false);
                }
            }
            return Command.SINGLE_SUCCESS;
        });
        registerChatCommand("sync",
                singletonList("type"),
                singletonList(string()),
                context -> {
                    String type = context.getArgument("type", String.class);
                    Player player = context.getSource();
                    if (type.equals("start") && player != null) {
                        sync = true;
                        syncRuntimeID = player.getRuntimeID();
                    } else if (type.equals("stop")) {
                        sync = false;
                        syncRuntimeID = 0;
                    }
                    return Command.SINGLE_SUCCESS;
                });
    }

    @Override
    public void normalTick() {
        super.normalTick();
        while (!scriptsToLoad.isEmpty()) {
            try {
                Script script = JSLoader.loadScript(scriptsToLoad.poll(), this);
                script.evaluate();
                scripts.add(script);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        scripts.forEach(script -> script.onTick(getLevel().getCurrentTick()));

        if (super.getLevel().isServerAuthoritativeMovement()) {
            sendPlayerAuthInput();
        }
        ItemStack selectedItem = super.getSelectedItem();
        if (selectedItem.getItem() == VanillaItems.mTrident) {
            if (!isUsingItem()) {
                startUseSelectedItem();
            } else if(VanillaItems.mTrident.getMaxUseDuration(getCarriedItem()) - super.mItemInUseDuration > 10) {
                super.getGameMode().releaseUsingItem();
            }
        }
    }

    public final void addScript(String scriptPath) {
        scriptsToLoad.add(scriptPath);
    }

    public final void removeScript(String path) {
        scriptsToLoad.remove(path);
    }

    public final void sendInventoryMismatch() {
        ComplexInventoryTransaction transaction = ComplexInventoryTransaction.fromType(ComplexInventoryTransaction.Type.InventoryMismatch_0, null);
        sendComplexInventoryTransaction(transaction);
    }

    public final void sendPlayerAuthInput() {
        PlayerAuthInputPacket packet = new PlayerAuthInputPacket();
        packet.setPosition(DataConverter.vector3f(super.getPos()));
        packet.setRotation(DataConverter.vector3f(super.getRotation(), super.getYHeadRot()));
        packet.setMotion(Vector2f.ZERO);
        packet.setInputMode(InputMode.TOUCH);
        packet.setPlayMode(ClientPlayMode.SCREEN);
        packet.setTick(playerAuthInputTick++);
        packet.setDelta(Vector3f.ZERO);
        this.sendNetworkPacket(packet);
    }

    public final void sendChatMessage(String message) {
        TextPacket packet = new TextPacket();
        packet.setType(TextPacket.Type.CHAT);
        packet.setNeedsTranslation(false);
        packet.setSourceName(super.getName());
        packet.setMessage(message);
        packet.setXuid(Long.toString(super.getClientUUID().getLeastSignificantBits()));
        this.sendNetworkPacket(packet);
    }

    public final void sendSwingArm() {
        AnimatePacket packet = new AnimatePacket();
        packet.setRuntimeEntityId(super.getRuntimeID());
        packet.setAction(AnimatePacket.Action.SWING_ARM);
        packet.setRowingTime(0);
        this.sendNetworkPacket(packet);
    }

    public final void sendStartBreak(BlockPos pos, int face) {
        PlayerActionPacket packet = new PlayerActionPacket();
        packet.setAction(PlayerActionType.START_BREAK);
        packet.setRuntimeEntityId(super.getRuntimeID());
        packet.setBlockPosition(DataConverter.vector3i(pos));
        packet.setFace(face);
        this.sendNetworkPacket(packet);
    }

    public final void sendAbortBreak(BlockPos pos) {
        PlayerActionPacket packet = new PlayerActionPacket();
        packet.setAction(PlayerActionType.ABORT_BREAK);
        packet.setRuntimeEntityId(super.getRuntimeID());
        packet.setBlockPosition(DataConverter.vector3i(pos));
        this.sendNetworkPacket(packet);
    }

    public final void sendStopBreak() {
        PlayerActionPacket packet = new PlayerActionPacket();
        packet.setRuntimeEntityId(super.getRuntimeID());
        packet.setBlockPosition(Vector3i.ZERO);
        this.sendNetworkPacket(packet);
    }

    public final void startUseSelectedItem() {
        ItemUseInventoryTransaction transaction = new ItemUseInventoryTransaction(super.getLevel().getItemRegistry());
        transaction.setSelectedItem(super.getSelectedItem())
                .setSelectedSlot(super.getSelectedItemSlot())
                .setBlockPosition(new NetworkBlockPosition(0, 0, 0))
                .setFacing(0xFF)
                .setClickPosition(new Vec3(0.0f, 0.0f, 0.0f))
                .setFromPosition(this.getPos())
                .setActionType(ItemUseInventoryTransaction.ActionType.Use_1);
        super.getGameMode().useItem(super.getSelectedItem());
        this.sendComplexInventoryTransaction(transaction);
    }

    public LiteralCommandNode<Player> registerChatCommand(String name, Command<Player> command) {
        return chatCommandDispatcher.register(
                LiteralArgumentBuilder
                        .<Player>literal(name)
                        .executes(command));
    }

    public LiteralCommandNode<Player> registerChatCommand(String name, List<String> argumentNames, List<ArgumentType<?>> argumentTypes, Command<Player> command) {
        if (name == null || argumentNames == null || argumentTypes == null || command == null
                || argumentNames.size() != argumentTypes.size() || argumentNames.size() <= 0) {
            return null;
        }
        LiteralArgumentBuilder<Player> literal = LiteralArgumentBuilder.literal(name);
        RequiredArgumentBuilder<Player, ?> last = argument(argumentNames.get(argumentNames.size() - 1), argumentTypes.get(argumentTypes.size() - 1));
        RequiredArgumentBuilder<Player, ?> first = last;
        last.executes(command);
        for (int i = argumentNames.size() - 2; i >= 0; --i) {
            first = argument(argumentNames.get(i), argumentTypes.get(i));
            first.then(last);
            last = first;
        }
        literal.then(first);
        return chatCommandDispatcher.register(literal);
    }

    public void unregisterChatCommand(String name) {
        CommandDispatcherUtil.unregister(chatCommandDispatcher, name);
    }

    public void onPlayerChat(String source, String message, String xuid, String platformChatId) {
        onPlayerChatListeners.forEach(listener -> listener.onPlayerChat(source, message, xuid, platformChatId));
    }

    public void addOnPlayerChatListener(OnPlayerChatListener listener) {
        onPlayerChatListeners.add(listener);
    }

    public void removePlayerChatListener(OnPlayerChatListener listener) {
        onPlayerChatListeners.remove(listener);
    }

    @FunctionalInterface
    public interface OnPlayerChatListener {
        void onPlayerChat(String source, String message, String xuid, String platformChatId);
    }
}
