package com.ddf.fakeplayer.actor.player;

import com.ddf.fakeplayer.actor.player.permission.CommandPermissionLevel;
import com.ddf.fakeplayer.actor.player.permission.PermissionsHandler;
import com.ddf.fakeplayer.actor.player.permission.PlayerPermissionLevel;
import com.ddf.fakeplayer.nbt.CompoundTag;
import com.ddf.fakeplayer.util.NotImplemented;

import java.util.Arrays;
import java.util.Objects;

import static com.ddf.fakeplayer.actor.player.Ability.Options.*;

public class Abilities {
    public static String TAG = "abilities";
    public static String[] ABILITY_NAMES = new String[] {
            "build",
            "mine",
            "doorsandswitches",
            "opencontainers",
            "attackplayers",
            "attackmobs",
            "op",
            "teleport",
            "invulnerable",
            "flying",
            "mayfly",
            "instabuild",
            "lightning",
            "flySpeed",
            "walkSpeed",
            "mute",
            "worldbuilder",
            "noclip"
    };

    private PermissionsHandler mPermissions;
    private Ability[] mAbilities = new Ability[18];
    private Ability[] mCustomAbilityCache = new Ability[8];

    public Abilities() {
        this.mPermissions = new PermissionsHandler();
        this._initAbilities();
        this._registerAbilities();
        this.setPlayerPermissions(this.mPermissions.getPlayerPermissions());
    }
    public Abilities(Abilities abilities) {
        this.mPermissions = abilities.mPermissions;
        abilities.mPermissions = null;
        this.mAbilities = abilities.mAbilities;
        this.mCustomAbilityCache = abilities.mCustomAbilityCache;
    }

    public void _initAbilities() {
        this._resetCustomAbilities();
        this._resetAbilities();
    }

    public void _registerAbilities() {
        this._registerAbility(AbilitiesIndex.ExposedAbilityCount, new Ability(false, None_2));
        this._registerAbility(AbilitiesIndex.Flying, new Ability(false, None_2));
        this._registerAbility(AbilitiesIndex.MayFly, new Ability(false, CommandExposed));
        this._registerAbility(AbilitiesIndex.Instabuild, new Ability(false, None_2));
        this._registerAbility(AbilitiesIndex.Lightning, new Ability(false, None_2));
        this._registerAbility(AbilitiesIndex.FlySpeed, new Ability(0.050000001f, None_2));
        this._registerAbility(AbilitiesIndex.WalkSpeed, new Ability(0.1f, None_2));
        this._registerAbility(AbilitiesIndex.Muted, new Ability(false, NoSave,CommandExposed));
        this._registerAbility(AbilitiesIndex.WorldBuilder, new Ability(false, NoSave, CommandExposed));
        this._registerAbility(AbilitiesIndex.NoClip, new Ability(false, NoSave));
        this._registerAbility(AbilitiesIndex.Build, new Ability(true, PermissionsInterfaceExposed, WorldbuilderOverrides));
        this._registerAbility(AbilitiesIndex.Mine, new Ability(true, PermissionsInterfaceExposed, WorldbuilderOverrides));
        this._registerAbility(AbilitiesIndex.DoorsAndSwitches, new Ability(false, PermissionsInterfaceExposed, WorldbuilderOverrides));
        this._registerAbility(AbilitiesIndex.OpenContainers, new Ability(false, PermissionsInterfaceExposed, WorldbuilderOverrides));
        this._registerAbility(AbilitiesIndex.AttackPlayers, new Ability(false, PermissionsInterfaceExposed));
        this._registerAbility(AbilitiesIndex.AttackMobs, new Ability(false, PermissionsInterfaceExposed, WorldbuilderOverrides));
        this._registerAbility(AbilitiesIndex.OperatorCommands, new Ability(false, PermissionsInterfaceExposed));
        this._registerAbility(AbilitiesIndex.Teleport, new Ability(false, PermissionsInterfaceExposed, WorldbuilderOverrides));
    }

    public void _registerAbility(AbilitiesIndex abilityIndex, final Ability ability) {
        if (ability.getType() != null) {
            Ability v4 = this.mAbilities[abilityIndex.getValue()];
            if (v4 == null || v4.getType() == Ability.Type.Unset || v4.getType() == Ability.Type.Invalid_2) {
                this.mAbilities[abilityIndex.getValue()] = ability;
            }
        }
    }

    public void _resetAbilities() {
        for (Ability ability : mAbilities) {
            if (ability == null)
                continue;
            ability.reset(Ability.Type.Unset);
        }
    }

    public void _resetCustomAbilities() {
        for (Ability ability : mCustomAbilityCache) {
            if (ability == null)
                continue;
            ability.reset(Ability.Type.Unset);
        }
    }

    public void _setCustomCachedAbility(AbilitiesIndex val) {
        int index = val.getValue();
        this.mCustomAbilityCache[index] = this.mAbilities[index];
    }

    @NotImplemented
    public void addSaveData(CompoundTag parentTag) {
    }

    @NotImplemented
    boolean loadSaveData(final CompoundTag parentTag) {
        return false;
    }

    public void forEachAbility(final AbilityForEachCallback callback, Ability.Options... requiredOptions) {
        for (int i = 0; i < AbilitiesIndex.AbilityCount.getValue(); ++i) {
            Ability ability = mAbilities[i];
            if (requiredOptions != null) {
                if (!ability.hasOption(requiredOptions))
                    continue;
            }
            callback.accept(ability, Abilities.getAbilityName(AbilitiesIndex.getByValue(i)));
        }
    }

    public Ability getAbility(AbilitiesIndex index) {
        return mAbilities[index.getValue()];
    }

    public static String getAbilityName(AbilitiesIndex abilityIndex) {
        return Abilities.ABILITY_NAMES[abilityIndex.getValue()];
    }

    public final boolean getBool(AbilitiesIndex abilityIndex) {
        return getAbility(abilityIndex).getBool();
    }

    public CommandPermissionLevel getCommandPermissions() {
        return this.mPermissions.getCommandPermissions();
    }

    public Ability getCustomAbilityFromCache(AbilitiesIndex abilityIndex) {
        return this.mCustomAbilityCache[abilityIndex.getValue()];
    }

    public float getFloat(AbilitiesIndex ability) {
        return this.getAbility(ability).getFloat();
    }

    public PlayerPermissionLevel getPlayerPermissions() {
        return this.mPermissions.getPlayerPermissions();
    }

    public boolean isFlying() {
        return this.getBool(AbilitiesIndex.Flying) || this.getBool(AbilitiesIndex.NoClip);
    }

    public boolean isOperator() {
        PlayerPermissionLevel permissionLevel = this.getPlayerPermissions();
        if (permissionLevel != PlayerPermissionLevel.Operator) {
            if (permissionLevel == PlayerPermissionLevel.Custom)
                return this.getBool(AbilitiesIndex.OperatorCommands);
        } else {
            return true;
        }
        return false;
    }

    public AbilitiesIndex nameToAbilityIndex(final String name) {
        if (!name.isEmpty()) {
            for (int i = 0; i < AbilitiesIndex.AbilityCount.getValue(); ++i) {
                if (Abilities.ABILITY_NAMES[i].equals(name))
                    return AbilitiesIndex.getByValue(i);
            }
        }
        return AbilitiesIndex.Invalid_3;
    }

    public void onSwitchToCustom() {
        for (int i = 0; i < AbilitiesIndex.ExposedAbilityCount.getValue(); ++i) {
            this.setAbility(AbilitiesIndex.getByValue(i), this.mCustomAbilityCache[i]);
        }
    }

    public Abilities set(final Abilities rhs){
        this.mPermissions = rhs.mPermissions;
        rhs.mPermissions = null;
        this.mAbilities = rhs.mAbilities;
        this.mCustomAbilityCache = rhs.mCustomAbilityCache;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Abilities abilities = (Abilities) o;
        return Objects.equals(mPermissions, abilities.mPermissions) && Arrays.equals(mAbilities, abilities.mAbilities);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(mPermissions);
        result = 31 * result + Arrays.hashCode(mAbilities);
        return result;
    }

    public void setAbility(AbilitiesIndex val, final Ability ability) {
        this.getAbility(val).set(ability);
    }

    public void setAbility(AbilitiesIndex ability, boolean value) {
        this.getAbility(ability).setBool(value);
    }

    public void setAbility(AbilitiesIndex abilityIndex, float value) {
        this.getAbility(abilityIndex).setFloat(value);
    }

    public boolean setAbilityDiff(AbilitiesIndex ability, boolean value, boolean diff) {
        Ability current = this.getAbility(ability);
        if (!diff && current.getBool() != value)
            diff = true;
        current.setBool(value);
        return diff;
    }

    public void setCommandPermissions(CommandPermissionLevel permissions) {
        this.mPermissions.setCommandPermissions(permissions);
    }

    public void setPlayerPermissions(PlayerPermissionLevel permissions) {
        if (this.mPermissions.getPlayerPermissions() == PlayerPermissionLevel.Custom){
            this._resetCustomAbilities();
            this._setCustomCachedAbility(AbilitiesIndex.Build);
            this._setCustomCachedAbility(AbilitiesIndex.Mine);
            this._setCustomCachedAbility(AbilitiesIndex.DoorsAndSwitches);
            this._setCustomCachedAbility(AbilitiesIndex.OpenContainers);
            this._setCustomCachedAbility(AbilitiesIndex.AttackPlayers);
            this._setCustomCachedAbility(AbilitiesIndex.AttackMobs);
            this._setCustomCachedAbility(AbilitiesIndex.OperatorCommands);
            this._setCustomCachedAbility(AbilitiesIndex.Teleport);
        }
        this.mPermissions.setPlayerPermissions(permissions);
        switch (permissions) {
            case Visitor:
                this.setAbility(AbilitiesIndex.Build, 0);
                this.setAbility(AbilitiesIndex.Mine, 0);
                this.setAbility(AbilitiesIndex.DoorsAndSwitches, 0);
                this.setAbility(AbilitiesIndex.OpenContainers, 0);
                this.setAbility(AbilitiesIndex.AttackPlayers, 0);
                this.setAbility(AbilitiesIndex.AttackMobs, 0);
                this.setAbility(AbilitiesIndex.OperatorCommands, 0);
                this.setAbility(AbilitiesIndex.Teleport, 0);
                break;
            case Member:
                this.setAbility(AbilitiesIndex.Build, 1);
                this.setAbility(AbilitiesIndex.Mine, 1);
                this.setAbility(AbilitiesIndex.DoorsAndSwitches, 1);
                this.setAbility(AbilitiesIndex.OpenContainers, 1);
                this.setAbility(AbilitiesIndex.AttackPlayers, 1);
                this.setAbility(AbilitiesIndex.AttackMobs, 1);
                this.setAbility(AbilitiesIndex.OperatorCommands, 0);
                this.setAbility(AbilitiesIndex.Teleport, 0);
                break;
            case Operator:
                this.setAbility(AbilitiesIndex.Build, 1);
                this.setAbility(AbilitiesIndex.Mine, 1);
                this.setAbility(AbilitiesIndex.DoorsAndSwitches, 1);
                this.setAbility(AbilitiesIndex.OpenContainers, 1);
                this.setAbility(AbilitiesIndex.AttackPlayers, 1);
                this.setAbility(AbilitiesIndex.AttackMobs, 1);
                this.setAbility(AbilitiesIndex.OperatorCommands, 1);
                this.setAbility(AbilitiesIndex.Teleport, 1);
                break;
        }
    }

    @FunctionalInterface
    public interface AbilityForEachCallback {
        void accept(Ability ability, String abilityName);
    }
}
