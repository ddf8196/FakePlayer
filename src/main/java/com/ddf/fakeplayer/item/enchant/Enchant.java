package com.ddf.fakeplayer.item.enchant;

public class Enchant {
    private final Enchant.Type mEnchantType;
    private final Enchant.Frequency mFrequency;
    private final boolean mIsLootable;
    private final int mPrimarySlots;
    private final int mSecondarySlots;
    private final int mCompatibility;
    private String mDescription;
    private String mStringId;
    private boolean mIsExperimental;
    private boolean mIsDisabled;

    public Enchant(Enchant.Type type, Enchant.Frequency frequency, final String stringId, final String description, int primarySlots, int secondarySlots) {
        this(type, frequency, stringId, description, primarySlots, secondarySlots, true);
    }

    public Enchant(Enchant.Type type, Enchant.Frequency frequency, final String stringId, final String description, int primarySlots, int secondarySlots, boolean isLootable) {
        this.mEnchantType = type;
        this.mFrequency = frequency;
        this.mIsLootable = isLootable;
        this.mPrimarySlots = primarySlots;
        this.mSecondarySlots = secondarySlots;
        this.mCompatibility = EnchantUtils.determineCompatibility(type);
        this.mDescription = description;
        this.mStringId = stringId;
        this.mIsExperimental = false;
        this.mIsDisabled = false;
    }

    public enum Activation {
        EQUIPPED,
        HELD,
        SELF,
        _num_activations,
        _invalid,
    }

    public enum CompatibilityID {
        NON_CONFLICT,
        DAMAGE,
        GATHERING,
        PROTECTION,
        FROSTSTRIDER,
        MENDFINITY,
        LOYALRIPTIDE,
    }

    public enum Frequency {
        Common_0(0x1E),
        Uncommon_0(0xA),
        Rare_0(0x3),
        VeryRare(0x1);

        private final int value;

        Frequency(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Frequency getByValue(int value) {
            for (Frequency frequency : values()) {
                if (frequency.getValue() == value) {
                    return frequency;
                }
            }
            return null;
        }
    }

    public enum Slot {
        NONE_12(0x0),
        All_8(0xFFFFFFFF),
        G_ARMOR(0xF),
        ARMOR_HEAD(0x1),
        ARMOR_TORSO(0x2),
        ARMOR_FEET(0x4),
        ARMOR_LEGS(0x8),
        SWORD(0x10),
        BOW(0x20),
        SPEAR(0x8000),
        CROSSBOW(0x10000),
        G_TOOL(0x201C0),
        HOE(0x40),
        SHEARS(0x80),
        FLINTSTEEL(0x100),
        SHIELD(0x20000),
        G_DIGGING(0xE00),
        AXE(0x200),
        PICKAXE(0x400),
        SHOVEL(0x800),
        FISHING_ROD(0x1000),
        CARROT_STICK(0x2000),
        ELYTRA(0x4000);

        private final int value;

        Slot(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Slot getByValue(int value) {
            for (Slot slot : values()) {
                if (slot.getValue() == value) {
                    return slot;
                }
            }
            return null;
        }
    }

    public enum Type {
        ArmorAll,
        ArmorFire,
        ArmorFall,
        ArmorExplosive,
        ArmorProjectile,
        ArmorThorns,
        WaterBreath,
        WaterSpeed,
        WaterAffinity,
        WeaponDamage,
        WeaponUndead,
        WeaponArthropod,
        WeaponKnockback,
        WeaponFire,
        WeaponLoot,
        MiningEfficiency,
        MiningSilkTouch,
        MiningDurability,
        MiningLoot,
        BowDamage,
        BowKnockback,
        BowFire,
        BowInfinity,
        FishingLoot,
        FishingLure,
        FrostWalker,
        Mending,
        CurseBinding,
        CurseVanishing,
        TridentImpaling,
        TridentRiptide,
        TridentLoyalty,
        TridentChanneling,
        CrossbowMultishot,
        CrossbowPiercing,
        CrossbowQuickCharge,
        NumEnchantments,
        InvalidEnchantment
    }
}
