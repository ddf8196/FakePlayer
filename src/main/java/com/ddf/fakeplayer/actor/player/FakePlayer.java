package com.ddf.fakeplayer.actor.player;

import com.ddf.fakeplayer.VersionInfo;
import com.ddf.fakeplayer.block.BlockPos;
import com.ddf.fakeplayer.client.Client;
import com.ddf.fakeplayer.container.ContainerID;
import com.ddf.fakeplayer.container.inventory.transaction.ComplexInventoryTransaction;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.item.VanillaItems;
import com.ddf.fakeplayer.js.JSLoader;
import com.ddf.fakeplayer.js.Script;
import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.main.I18N;
import com.ddf.fakeplayer.util.*;
import com.ddf.fakeplayer.util.command.CommandDispatcherUtil;
import com.ddf.fakeplayer.util.command.EnumArgument;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.ClientPlayMode;
import org.cloudburstmc.protocol.bedrock.data.InputInteractionModel;
import org.cloudburstmc.protocol.bedrock.data.InputMode;
import org.cloudburstmc.protocol.bedrock.data.PlayerActionType;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginData;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginType;
import org.cloudburstmc.protocol.bedrock.packet.*;

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
    private final HashMap<String, CommandNode<Player>> chatCommandNodes = new HashMap<>();
    private final HashMap<String, String> chatCommandDesc = new HashMap<String, String>(){{
        put("help",            I18N.get("cmd.desc.help"));
        put("getVersion",      I18N.get("cmd.desc.getVersion"));
        put("getPos",          I18N.get("cmd.desc.getPos"));
        put("getInventory",    I18N.get("cmd.desc.getInventory"));
        put("getSelectedSlot", I18N.get("cmd.desc.getSelectedSlot"));
        put("selectSlot",      I18N.get("cmd.desc.selectSlot"));
        put("dropSlot",        I18N.get("cmd.desc.dropSlot"));
        put("dropAll",         I18N.get("cmd.desc.dropAll"));
        put("sync",            I18N.get("cmd.desc.sync"));
    }};
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

        registerChatCommand("help",
                context -> {
                    StringBuilder builder = new StringBuilder();
                    builder.append("\n");
                    int maxLength = 0;
                    for (Map.Entry<String, CommandNode<Player>> entry : chatCommandNodes.entrySet()) {
                        CommandNode<Player> node = entry.getValue();
                        String nodeName = node.getName();
                        maxLength = Integer.max(nodeName.length(), maxLength);
                    }
                    for (Map.Entry<String, CommandNode<Player>> entry : chatCommandNodes.entrySet()) {
                        CommandNode<Player> node = entry.getValue();
                        String nodeName = node.getName();
                        String nodeDesc = chatCommandDesc.get(nodeName);
                        if (nodeDesc == null) {
                            nodeDesc = "None";
                        }
                        builder.append(String.format(ColorFormat.AQUA + "%-" + maxLength + "s" + ColorFormat.RESET + " - %s\n", nodeName, nodeDesc));
                    }
                    sendChatCommandMessage(builder);
                    return Command.SINGLE_SUCCESS;
                });
        registerChatCommand("help",
                singletonList("cmd"),
                singletonList(string()),
                context -> {
                    String myName = getName();
                    String cmd = context.getArgument("cmd", String.class);
                    CommandNode<Player> node = chatCommandNodes.get(cmd);
                    if (node == null) {
                        sendChatCommandError(I18N.get("cmd.msg.help.cmdNotFound"));
                        return 2;
                    }
                    String nodeDesc = chatCommandDesc.get(cmd);
                    if (nodeDesc == null) {
                        nodeDesc = I18N.get("cmd.msg.help.noDesc");
                    }
                    StringBuilder builder = new StringBuilder();
                    builder.append("\n")
                            .append(ColorFormat.AQUA).append(cmd).append(":\n")
                            .append(ColorFormat.RESET).append(nodeDesc).append("\n")
                            .append(I18N.get("cmd.msg.help.usage")).append("\n");
                    List<String> usages = getChatCommandUsage(node, "- " + myName);
                    for (String u : usages) {
                        builder.append(u).append("\n");
                    }
                    builder.deleteCharAt(builder.length() - 1);
                    this.sendChatCommandMessage(builder);
                    return Command.SINGLE_SUCCESS;
                });
        registerChatCommand("getVersion", context -> {
            this.sendChatCommandMessage("FakePlayer " , VersionInfo.VERSION, "\nGitHub: https://github.com/ddf8196/FakePlayer");
            return Command.SINGLE_SUCCESS;
        });
        registerChatCommand("getPos", context -> {
            Vec3 pos = super.getPos();
            this.sendChatCommandMessage(ColorFormat.GREEN, "[", super.getDimensionId(), "] ", ColorFormat.AQUA, (int)pos.x, ", ", (int)(pos.y - super.mHeightOffset), ", ", (int)pos.z, ColorFormat.RESET);
            return Command.SINGLE_SUCCESS;
        });
        registerChatCommand("getInventory", context -> {
            boolean empty = true, first = true;
            StringBuilder builder = new StringBuilder(I18N.get("cmd.msg.getInventory.content") + "\n");
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
                this.sendChatCommandMessage(builder);
            } else {
                this.sendChatCommandMessage(I18N.get("cmd.msg.empty"));
            }
            return Command.SINGLE_SUCCESS;
        });
        registerChatCommand("getSelectedSlot", context -> {
            this.sendChatCommandMessage(super.getSelectedItemSlot());
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
                singletonList("operation"),
                singletonList(EnumArgument.enumArg("start", "stop")),
                context -> {
                    String op = context.getArgument("operation", String.class);
                    Player player = context.getSource();
                    if (op.equals("start") && player != null) {
                        sync = true;
                        syncRuntimeID = player.getRuntimeID();
                    } else if (op.equals("stop")) {
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
        ItemStack selectedItem = this.getSelectedItem();
        if (selectedItem.getItem() == VanillaItems.mTrident) {
            if (!isUsingItem()) {
                this.getGameMode().baseUseItem(selectedItem);
            } else if(VanillaItems.mTrident.getMaxUseDuration(getCarriedItem()) - this.mItemInUseDuration > 10) {
                this.getGameMode().releaseUsingItem();
            }
        }
    }

    public final void addScript(String scriptPath) {
        scriptsToLoad.add(scriptPath);
    }

    public final void removeScript(String path) {
        scriptsToLoad.remove(path);
        scripts.removeIf(script -> script.getPath().equals(path));
    }

    public final void sendInventoryMismatch() {
        ComplexInventoryTransaction transaction = ComplexInventoryTransaction.fromType(ComplexInventoryTransaction.Type.InventoryMismatch_0);
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
        packet.setInputInteractionModel(InputInteractionModel.TOUCH);
        packet.setAnalogMoveVector(Vector2f.ZERO);
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

    public final void sendCommandRequest(String command) {
        CommandRequestPacket packet = new CommandRequestPacket();
        CommandOriginData commandOrigin = new CommandOriginData(CommandOriginType.PLAYER, UUID.randomUUID(), "", -1);
        packet.setCommandOriginData(commandOrigin);
        packet.setCommand(command);
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

    public LiteralCommandNode<Player> registerChatCommand(String name, Command<Player> command) {
        LiteralCommandNode<Player> node = chatCommandDispatcher.register(
                LiteralArgumentBuilder
                        .<Player>literal(name)
                        .executes(command));
        chatCommandNodes.put(name, node);
        return node;
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
        LiteralCommandNode<Player> node = chatCommandDispatcher.register(literal);
        chatCommandNodes.put(name, node);
        return node;
    }

    public void unregisterChatCommand(String name) {
        CommandDispatcherUtil.unregister(chatCommandDispatcher, name);
    }

    public void addChatCommandDescribe(String name, String desc) {
        chatCommandDesc.put(name, desc);
    }

    public List<String> getChatCommandUsage(CommandNode<Player> node, String prefix) {
        List<String> result = new ArrayList<>();
        if (node.getCommand() != null) {
            result.add(prefix);
        }
        String text = "";
        if (node.toString().contains("argument")) {
            String type = ((ArgumentCommandNode<Player, ?>) node).getType().toString();
            text = "<" + node.getName() + ": " + type + ">";
        } else {
            text = node.getName();
        }
        if (node.getChildren().isEmpty()) {
            result.add(prefix + " " + text);
            return result;
        }
        for (CommandNode<Player> arg : node.getChildren()) {
            result.addAll(getChatCommandUsage(arg, prefix + " " + text));
        }
        return result;
    }

    public void sendChatCommandError(Object... msg) {
        StringBuilder builder = new StringBuilder();
        for (Object obj : msg) {
            builder.append(obj);
        }
        sendChatMessage(ColorFormat.RED + "[ERROR] " + ColorFormat.RESET + builder);
    }

    public void sendChatCommandMessage(Object... msg) {
        StringBuilder builder = new StringBuilder();
        for (Object obj : msg) {
            builder.append(obj);
        }
        sendChatMessage(builder.toString());
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
