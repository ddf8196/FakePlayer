package com.ddf.fakeplayer.item.items;

import com.ddf.fakeplayer.actor.component.StateVectorComponent;
import com.ddf.fakeplayer.actor.player.AbilitiesIndex;
import com.ddf.fakeplayer.actor.player.Player;
import com.ddf.fakeplayer.container.inventory.PlayerInventoryProxy;
import com.ddf.fakeplayer.item.*;
import com.ddf.fakeplayer.item.enchant.Enchant;
import com.ddf.fakeplayer.item.enchant.EnchantUtils;
import com.ddf.fakeplayer.util.Vec3;

public class TridentItem extends Item {
    public TridentItem(String name/*, int id*/) {
        super(name/*, id*/);
        this.mUseAnim = UseAnimation.Spear;
        setMaxUseDuration(72000);
        this.mMaxDamage = 8;
    }

    @Override
    public short getMaxDamage() {
        return this.mMaxDamage;
    }

    @Override
    public int getAttackDamage() {
        return 8;
    }

    @Override
    public boolean canDestroyInCreative() {
        return false;
    }

//    @Override
//    String appendFormattedHovertext(final ItemStackBase stack, Level level, String hovertext, final boolean showCategory) {
//        String statText = super.appendFormattedHovertext(stack, level, hovertext, showCategory);
//        float bonus = (float) EnchantUtils.getEnchantLevel(WeaponDamage, stack) * 1.25f;
//        statText = "+" + Float.toString(this.getAttackDamage() + bonus) + " " + I18n.get("attribute.name.generic.attackDamage");
//        hovertext += "\n\n" + ColorFormat.BLUE + statText + ColorFormat.RESET;
//        return hovertext;
//    }

    @Override
    public int getEnchantSlot() {
        return Enchant.Slot.SPEAR.getValue();
    }

    @Override
    public int getEnchantValue() {
        return 1;
    }

    @Override
    public ItemStack use(ItemStack instance, Player player) {
        if (EnchantUtils.getEnchantLevel(Enchant.Type.TridentRiptide, instance) == 0 || player.isInWaterOrRain()) {
            if (instance.getDamageValue() < instance.getMaxDamage()) {
                player.startUsingItem(instance, instance.getMaxUseDuration());
            }
        }
        return instance;
    }

    @Override
    public void releaseUsing(ItemStack itemStack, Player player, int durationLeft) {
        int riptideLevel = EnchantUtils.getEnchantLevel(Enchant.Type.TridentRiptide, itemStack);
        if (riptideLevel <= 0) {
            if ((this.getMaxUseDuration(itemStack) - durationLeft) >= 10 ){
                PlayerInventoryProxy playerInv = player.getSupplies();
                //Level level = player.getLevel();
                //level.getActorEventCoordinator().sendActorUseItem(player, new ItemInstance(itemStack), (ItemUseMethod) 5);
                //level.broadcastSoundEvent(player.getRegion(), LevelSoundEvent.TridentThrow, player.getAttachPos(ActorLocation.WeaponAttachPoint, 0.0f), -1, new ActorDefinitionIdentifier(), 0, 0);
                if (!player.mAbilities.getBool(AbilitiesIndex.Instabuild)) {
                    itemStack.remove(1);
                    if (itemStack.isEmptyStack()) {
                        PlayerInventoryProxy.SlotData selectedSlot = player.getSupplies().getSelectedSlot();
                        playerInv.clearSlot(selectedSlot.mSlot, selectedSlot.mContainerId);
                    }
                }
            }
        } else {
            int timeHeld = this.getMaxUseDuration(itemStack) - durationLeft;
            if (player.isInWaterOrRain() && !player.isRiding() && timeHeld >= 10 ) {
                //MinecraftEventing.fireEventAwardAchievement(player, DoaBarrelRoll);
                float yRot = player.mRot.y; //yaw
                float xRot = player.mRot.x; //pitch
                float x_ = (float) (-Math.sin(0.017453292f * yRot) * Math.cos(0.017453292f * xRot));
                float y_ = (float) -Math.sin(0.017453292f * xRot);
                float z_ = (float) (Math.cos(0.017453292f * yRot) * Math.cos(0.017453292f * xRot));
                Vec3 impulse = new Vec3(x_, y_, z_);
                float dist = impulse.length();
                float n = 3.0f * (riptideLevel + 1.0f) / 4.0f;
                impulse = impulse.multiply(n / dist);
                player.startSpinAttack();
                if (!player.isRemoved() && !player.isImmobile()) {
                    StateVectorComponent svc = player.getStateVectorComponentNonConst();
                    svc.setPosDelta(svc.getPosDelta().add(impulse));
                }
//                Level level = player.getLevel();
//                if (riptideLevel >= 3) {
//                    level.broadcastSoundEvent(player.getRegion(), LevelSoundEvent.TridentRiptide_3, player.getAttachPos(ActorLocation.WeaponAttachPoint, 0.0f), -1, new ActorDefinitionIdentifier(), 0, 0);
//                } else if (riptideLevel == 2) {
//                    level.broadcastSoundEvent(player.getRegion(), LevelSoundEvent.TridentRiptide_2, player.getAttachPos(ActorLocation.WeaponAttachPoint, 0.0f), -1, new ActorDefinitionIdentifier(), 0, 0);
//                } else {
//                    level.broadcastSoundEvent(player.getRegion(), LevelSoundEvent.TridentRiptide_1, player.getAttachPos(ActorLocation.WeaponAttachPoint, 0.0f), -1, new ActorDefinitionIdentifier(), 0, 0);
//                }
                if (player.mOnGround) {
                    player.move(new Vec3(0.0f, 1.0f, 0.0f));
                }
            }
        }
    }

//    @NotImplemented
//    @Override
//    public boolean dispense(BlockSource region, Container container, int slot, final Vec3 pos, FacingID face) {
//    }
//
//    @Override
//    public void hurtEnemy(ItemStack itemStack, Mob mob, Mob attacker) {
//        itemStack.hurtAndBreak(1, mob);
//    }
}
