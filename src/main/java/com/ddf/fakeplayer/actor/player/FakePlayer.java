package com.ddf.fakeplayer.actor.player;

import com.ddf.fakeplayer.Client;
import com.ddf.fakeplayer.container.ContainerID;
import com.ddf.fakeplayer.container.inventory.transaction.ComplexInventoryTransaction;
import com.ddf.fakeplayer.container.inventory.transaction.ItemUseInventoryTransaction;
import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.item.VanillaItems;
import com.ddf.fakeplayer.level.Level;
import com.ddf.fakeplayer.network.NetworkBlockPosition;
import com.ddf.fakeplayer.util.ColorFormat;
import com.ddf.fakeplayer.util.DataConverter;
import com.ddf.fakeplayer.util.MathUtil;
import com.ddf.fakeplayer.util.Vec3;
import com.nukkitx.math.vector.Vector2f;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.ClientPlayMode;
import com.nukkitx.protocol.bedrock.data.InputMode;
import com.nukkitx.protocol.bedrock.packet.PlayerAuthInputPacket;
import com.nukkitx.protocol.bedrock.packet.TextPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FakePlayer extends LocalPlayer {
    private long playerAuthInputTick = 0;
    public boolean sync = false;
    public long syncRuntimeID = 0;
    private final List<FakePlayer.OnPlayerChatListener> onPlayerChatListeners = Collections.synchronizedList(new ArrayList<>());

    public FakePlayer(Level level, String name, UUID uuid, Client client) {
        super(level, name, uuid, client);
        this.addOnPlayerChatListener((source, message, xuid, platformChatId) -> {
            if (!client.isAllowChatMessageControl()
                    || message.length() <= super.getName().length()
                    || !message.startsWith(super.getName())
                    || !message.substring(0, message.indexOf(" ")).equals(super.getName())) {
                return;
            }
            try {
                String cmd = message.substring(super.getName().length() + 1).trim();
                String lowerCaseCmd = cmd.toLowerCase();
                if (cmd.equals("help")) {
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
                    return;
                }
                if (lowerCaseCmd.startsWith("getPos".toLowerCase())) {
                    Vec3 pos = super.getPos();
                    this.sendChatMessage(ColorFormat.GREEN + "[" + super.getDimensionId() + "] " + ColorFormat.AQUA + (int)pos.x + ", " + (int)(pos.y - super.mHeightOffset) + ", " + (int)pos.z + ColorFormat.RESET);
                } else if (lowerCaseCmd.startsWith("getInventory".toLowerCase())) {
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
                } else if (lowerCaseCmd.startsWith("getSelectedSlot".toLowerCase())) {
                    this.sendChatMessage(Integer.toString(super.getSelectedItemSlot()));
                } else if (lowerCaseCmd.startsWith("selectSlot".toLowerCase())) {
                    if (cmd.length() < 12) return;
                    int slot = Integer.parseInt(cmd.substring(11));
                    super.getSupplies().selectSlot(slot, ContainerID.CONTAINER_ID_INVENTORY);
                } else if (lowerCaseCmd.startsWith("dropSlot".toLowerCase())) {
                    int slot;
                    if (cmd.length() > 9)
                        slot = MathUtil.clamp(Integer.parseInt(cmd.substring(9)), 0, 35);
                    else
                        slot = super.getSelectedItemSlot();
                    super.getSupplies().dropSlot(slot, false, true, ContainerID.CONTAINER_ID_INVENTORY, false);
                } else if (lowerCaseCmd.startsWith("dropAll".toLowerCase())) {
                    ArrayList<ItemStack> slots = super.getSupplies().getSlots();
                    for (int i = 0; i < super.getSupplies().getContainerSize(ContainerID.CONTAINER_ID_INVENTORY); i++) {
                        if (slots.get(i).toBoolean()) {
                            super.getSupplies().dropSlot(i, false, true, ContainerID.CONTAINER_ID_INVENTORY, false);
                        }
                    }
                } else if (lowerCaseCmd.startsWith("sync".toLowerCase())) {
                    if (cmd.length() < 6) return;
                    String cmd2 = cmd.substring(5);
                    if (cmd2.equals("start")) {
                        Player player = level.getPlayerByName(source);
                        if (player == null)
                            return;
                        sync = true;
                        syncRuntimeID = player.getRuntimeID();
                    } else if (cmd2.equals("stop")) {
                        sync = false;
                        syncRuntimeID = 0;
                    }
                }
            } catch (Throwable ignored) {}
        });
    }

    @Override
    public void normalTick() {
        super.normalTick();
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

    private void startUseSelectedItem() {
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

    public final void sendChatMessage(String message) {
        TextPacket packet = new TextPacket();
        packet.setType(TextPacket.Type.CHAT);
        packet.setNeedsTranslation(false);
        packet.setSourceName(super.getName());
        packet.setMessage(message);
        packet.setXuid(Long.toString(super.getClientUUID().getLeastSignificantBits()));
        this.sendNetworkPacket(packet);
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
