package com.ddf.fakeplayer.item.enchant;

import com.ddf.fakeplayer.item.ItemStack;
import com.ddf.fakeplayer.item.ItemStackBase;

public class EnchantUtils {

    public static int getEnchantLevel(Enchant.Type enchantType, final ItemStackBase stack) {
        int level = 0;
        if (stack.isEnchanted()) {
            ItemEnchants enchants = stack.getEnchantsFromUserData();
            level = enchants.hasEnchant(enchantType);
        }
        return level;
    }

    public static boolean hasEnchant(Enchant.Type enchantType, final ItemStack item) {
        if (item.isEnchanted()) {
            return item.getEnchantsFromUserData().hasEnchant(enchantType) > 0;
        }
        return false;
    }

    public static int determineCompatibility(Enchant.Type type) {
        int compatibility = 0;
        switch (type) {
            case ArmorAll:
            case ArmorFire:
            case ArmorExplosive:
            case ArmorProjectile:
                compatibility = 3;
                break;
            case WaterSpeed:
            case FrostWalker:
                compatibility = 4;
                break;
            case WeaponDamage:
            case WeaponUndead:
            case WeaponArthropod:
                compatibility = 1;
                break;
            case MiningSilkTouch:
            case MiningLoot:
                compatibility = 2;
                break;
            case BowInfinity:
            case Mending:
                compatibility = 5;
                break;
            case TridentRiptide:
            case TridentLoyalty:
                compatibility = 6;
                break;
            default:
                return compatibility;
        }
        return compatibility;
    }

    public static int determineActivation(Enchant.Type enchantType) {
        int activation = 4;
        if (enchantType.ordinal() >= Enchant.Type.WeaponDamage.ordinal()) {
            if ((enchantType.ordinal() - 9) < 3 )
                return 1;
            if (enchantType == Enchant.Type.WeaponKnockback)
                return 2;
            if ((enchantType.ordinal() - 13) < 2 )
                return 1;
            if (enchantType == Enchant.Type.MiningEfficiency)
                return 2;
            if ((enchantType.ordinal() - 16) < 3 )
                return 1;
            if ((enchantType.ordinal() - 19) < 2 )
                return 2;
            switch (enchantType) {
                case BowFire:
                case FishingLoot:
                    return 1;
                case BowInfinity:
                case FishingLure:
                    return 2;
            }
            if (enchantType != Enchant.Type.FrostWalker) {
                if ((enchantType.ordinal() - 26) >= 3) {
                    if (enchantType != Enchant.Type.TridentImpaling) {
                        if ((enchantType.ordinal() - 30) >= 6)
                            return activation;
                        return 2;
                    }
                    return 1;
                }
                return 2;
            }
        }
        return 0;
    }
}
