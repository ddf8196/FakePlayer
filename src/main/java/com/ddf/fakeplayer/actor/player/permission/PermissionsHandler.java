package com.ddf.fakeplayer.actor.player.permission;

import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PermissionsHandler {
    private static final HashMap<PlayerPermissionLevel, String> playerPermissionStrings = new HashMap<>();
    private CommandPermissionLevel mCommandPermissions;
    private PlayerPermissionLevel mPlayerPermissions;

    static {
        playerPermissionStrings.put(PlayerPermissionLevel.Visitor, "visitor");
        playerPermissionStrings.put(PlayerPermissionLevel.Member, "member");
        playerPermissionStrings.put(PlayerPermissionLevel.Operator, "operator");
        playerPermissionStrings.put(PlayerPermissionLevel.Custom, "custom");
    }

    public PermissionsHandler() {
        this.mCommandPermissions = CommandPermissionLevel.Any;
        this.mPlayerPermissions = PlayerPermissionLevel.Member;
    }

    @NotImplemented
    public void addSaveData(CompoundTag tag) {

    }

    @NotImplemented
    public boolean loadSaveData(final CompoundTag tag) {
        return false;
    }

    public CommandPermissionLevel getCommandPermissions() {
        return this.mCommandPermissions;
    }

    public void setCommandPermissions(CommandPermissionLevel permissions) {
        this.mCommandPermissions = permissions;
    }

    public PlayerPermissionLevel getPlayerPermissions() {
        return this.mPlayerPermissions;
    }

    public void setPlayerPermissions(PlayerPermissionLevel permissions) {
        this.mPlayerPermissions = permissions;
    }

    public PlayerPermissionLevel playerPermissionLevelFromString(final String s) {
        for (Map.Entry<PlayerPermissionLevel, String> entry : playerPermissionStrings.entrySet()) {
            if (entry.getValue().equals(s)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String playerPermissionLevelToString(final PlayerPermissionLevel permission) {
        return playerPermissionStrings.get(permission);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionsHandler that = (PermissionsHandler) o;
        return mCommandPermissions == that.mCommandPermissions && mPlayerPermissions == that.mPlayerPermissions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mCommandPermissions, mPlayerPermissions);
    }
}
