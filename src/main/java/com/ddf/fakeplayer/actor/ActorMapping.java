package com.ddf.fakeplayer.actor;


import java.util.HashMap;

public class ActorMapping {
    public static final HashMap<ActorType, ActorMapping> ENTITY_TYPE_MAP = new HashMap<>();

    public String mNamespace;
    public String mPrimaryName;
    public String mAlternateName;
    public String mCanonicalName;

    static {
        ENTITY_TYPE_MAP.put(ActorType.Bee, new ActorMapping("bee", ""));
        ENTITY_TYPE_MAP.put(ActorType.Cat, new ActorMapping("cat", ""));
        ENTITY_TYPE_MAP.put(ActorType.Chicken, new ActorMapping("chicken", ""));
        ENTITY_TYPE_MAP.put(ActorType.Cow, new ActorMapping("cow", ""));
        ENTITY_TYPE_MAP.put(ActorType.Pig, new ActorMapping("pig", ""));
        ENTITY_TYPE_MAP.put(ActorType.Sheep, new ActorMapping("sheep", ""));
        ENTITY_TYPE_MAP.put(ActorType.Wolf, new ActorMapping("wolf", ""));
        ENTITY_TYPE_MAP.put(ActorType.Villager, new ActorMapping("villager", ""));
        ENTITY_TYPE_MAP.put(ActorType.VillagerV2, new ActorMapping("villager_v2", ""));
        ENTITY_TYPE_MAP.put(ActorType.WanderingTrader, new ActorMapping("wandering_trader", ""));
        ENTITY_TYPE_MAP.put(ActorType.MushroomCow, new ActorMapping("mooshroom", "mushroomcow"));
        ENTITY_TYPE_MAP.put(ActorType.Squid, new ActorMapping("squid", ""));
        ENTITY_TYPE_MAP.put(ActorType.Rabbit, new ActorMapping("rabbit", ""));
        ENTITY_TYPE_MAP.put(ActorType.Bat, new ActorMapping("bat", ""));
        ENTITY_TYPE_MAP.put(ActorType.IronGolem, new ActorMapping("iron_golem", "irongolem"));
        ENTITY_TYPE_MAP.put(ActorType.SnowGolem, new ActorMapping("snow_golem", "snowgolem"));
        ENTITY_TYPE_MAP.put(ActorType.Ocelot, new ActorMapping("ocelot", ""));
        ENTITY_TYPE_MAP.put(ActorType.Horse, new ActorMapping("horse", ""));
        ENTITY_TYPE_MAP.put(ActorType.Llama, new ActorMapping("llama", ""));
        ENTITY_TYPE_MAP.put(ActorType.PolarBear, new ActorMapping("polar_bear", "polarbear"));
        ENTITY_TYPE_MAP.put(ActorType.Parrot, new ActorMapping("parrot", ""));
        ENTITY_TYPE_MAP.put(ActorType.Turtle, new ActorMapping("turtle", ""));
        ENTITY_TYPE_MAP.put(ActorType.Dolphin, new ActorMapping("dolphin", ""));
        ENTITY_TYPE_MAP.put(ActorType.Panda, new ActorMapping("panda", ""));
        ENTITY_TYPE_MAP.put(ActorType.Fox, new ActorMapping("fox", ""));
        ENTITY_TYPE_MAP.put(ActorType.Tropicalfish, new ActorMapping("tropicalfish", ""));
        ENTITY_TYPE_MAP.put(ActorType.Fish, new ActorMapping("cod", ""));
        ENTITY_TYPE_MAP.put(ActorType.Pufferfish, new ActorMapping("pufferfish", ""));
        ENTITY_TYPE_MAP.put(ActorType.Salmon, new ActorMapping("salmon", ""));
        ENTITY_TYPE_MAP.put(ActorType.Donkey, new ActorMapping("donkey", ""));
        ENTITY_TYPE_MAP.put(ActorType.Mule, new ActorMapping("mule", ""));
        ENTITY_TYPE_MAP.put(ActorType.SkeletonHorse, new ActorMapping("skeleton_horse", "skeletonhorse"));
        ENTITY_TYPE_MAP.put(ActorType.ZombieHorse, new ActorMapping("zombie_horse", "zombiehorse"));
        ENTITY_TYPE_MAP.put(ActorType.Zombie, new ActorMapping("zombie", ""));
        ENTITY_TYPE_MAP.put(ActorType.Drowned, new ActorMapping("drowned", ""));
        ENTITY_TYPE_MAP.put(ActorType.Creeper, new ActorMapping("creeper", ""));
        ENTITY_TYPE_MAP.put(ActorType.Skeleton, new ActorMapping("skeleton", ""));
        ENTITY_TYPE_MAP.put(ActorType.Spider, new ActorMapping("spider", ""));
        ENTITY_TYPE_MAP.put(ActorType.PigZombie, new ActorMapping("zombie_pigman", "pig_zombie"));
        ENTITY_TYPE_MAP.put(ActorType.Slime, new ActorMapping("slime", ""));
        ENTITY_TYPE_MAP.put(ActorType.EnderMan, new ActorMapping("enderman", ""));
        ENTITY_TYPE_MAP.put(ActorType.Silverfish, new ActorMapping("silverfish", ""));
        ENTITY_TYPE_MAP.put(ActorType.CaveSpider, new ActorMapping("cave_spider", "cavespider"));
        ENTITY_TYPE_MAP.put(ActorType.Ghast, new ActorMapping("ghast", ""));
        ENTITY_TYPE_MAP.put(ActorType.LavaSlime, new ActorMapping("magma_cube", "magmacube"));
        ENTITY_TYPE_MAP.put(ActorType.Blaze, new ActorMapping("blaze", ""));
        ENTITY_TYPE_MAP.put(ActorType.ZombieVillager, new ActorMapping("zombie_villager", "zombievillager"));
        ENTITY_TYPE_MAP.put(ActorType.ZombieVillagerV2, new ActorMapping("zombie_villager_v2", ""));
        ENTITY_TYPE_MAP.put(ActorType.Witch, new ActorMapping("witch", ""));
        ENTITY_TYPE_MAP.put(ActorType.Stray, new ActorMapping("stray", "skeleton.stray"));
        ENTITY_TYPE_MAP.put(ActorType.Husk, new ActorMapping("husk", ""));
        ENTITY_TYPE_MAP.put(ActorType.WitherSkeleton, new ActorMapping("wither_skeleton", "skeleton.wither"));
        ENTITY_TYPE_MAP.put(ActorType.Guardian, new ActorMapping("guardian", ""));
        ENTITY_TYPE_MAP.put(ActorType.ElderGuardian, new ActorMapping("elder_guardian", "guardian.elder"));
        ENTITY_TYPE_MAP.put(ActorType.ElderGuardianGhost, new ActorMapping("elder_guardian_ghost", "guardian.elderghost"));
        ENTITY_TYPE_MAP.put(ActorType.WitherBoss, new ActorMapping("wither", "wither.boss"));
        ENTITY_TYPE_MAP.put(ActorType.Dragon, new ActorMapping("ender_dragon", "dragon"));
        ENTITY_TYPE_MAP.put(ActorType.Shulker, new ActorMapping("shulker", ""));
        ENTITY_TYPE_MAP.put(ActorType.Endermite, new ActorMapping("endermite", ""));
        ENTITY_TYPE_MAP.put(ActorType.Vindicator, new ActorMapping("vindicator", ""));
        ENTITY_TYPE_MAP.put(ActorType.EvocationIllager, new ActorMapping("evocation_illager", "evocationillager"));
        ENTITY_TYPE_MAP.put(ActorType.Vex, new ActorMapping("vex", "vex"));
        ENTITY_TYPE_MAP.put(ActorType.Phantom, new ActorMapping("phantom", ""));
        ENTITY_TYPE_MAP.put(ActorType.Pillager, new ActorMapping("pillager", ""));
        ENTITY_TYPE_MAP.put(ActorType.IllagerBeast, new ActorMapping("ravager", ""));
        ENTITY_TYPE_MAP.put(ActorType.Player_0, new ActorMapping("player", ""));
        ENTITY_TYPE_MAP.put(ActorType.MinecartRideable, new ActorMapping("minecart", ""));
        ENTITY_TYPE_MAP.put(ActorType.MinecartHopper, new ActorMapping("hopper_minecart", "minecarthopper"));
        ENTITY_TYPE_MAP.put(ActorType.MinecartTNT, new ActorMapping("tnt_minecart", "minecarttnt"));
        ENTITY_TYPE_MAP.put(ActorType.MinecartChest, new ActorMapping("chest_minecart", "minecartchest"));
        ENTITY_TYPE_MAP.put(ActorType.MinecartCommandBlock, new ActorMapping("command_block_minecart", "commandblockminecart"));
        ENTITY_TYPE_MAP.put(ActorType.ItemEntity, new ActorMapping("item", ""));
        ENTITY_TYPE_MAP.put(ActorType.PrimedTnt, new ActorMapping("tnt", "primedtnt"));
        ENTITY_TYPE_MAP.put(ActorType.FallingBlock, new ActorMapping("falling_block", "fallingblock"));
        ENTITY_TYPE_MAP.put(ActorType.MovingBlock, new ActorMapping("moving_block", "movingblock"));
        ENTITY_TYPE_MAP.put(ActorType.ExperiencePotion, new ActorMapping("xp_bottle", "potion.experience"));
        ENTITY_TYPE_MAP.put(ActorType.Experience, new ActorMapping("xp_orb", "xporb"));
        ENTITY_TYPE_MAP.put(ActorType.EyeOfEnder, new ActorMapping("eye_of_ender_signal", "eyeofender"));
        ENTITY_TYPE_MAP.put(ActorType.EnderCrystal, new ActorMapping("ender_crystal", "endercrystal"));
        ENTITY_TYPE_MAP.put(ActorType.ShulkerBullet, new ActorMapping("shulker_bullet", "shulkerbullet"));
        ENTITY_TYPE_MAP.put(ActorType.FishingHook, new ActorMapping("fishing_hook", "fishinghook"));
        ENTITY_TYPE_MAP.put(ActorType.DragonFireball, new ActorMapping("dragon_fireball", "fireball.dragon"));
        ENTITY_TYPE_MAP.put(ActorType.Arrow, new ActorMapping("arrow", "arrow.skeleton"));
        ENTITY_TYPE_MAP.put(ActorType.Snowball, new ActorMapping("snowball", ""));
        ENTITY_TYPE_MAP.put(ActorType.ThrownEgg, new ActorMapping("egg", "thrownegg"));
        ENTITY_TYPE_MAP.put(ActorType.Painting, new ActorMapping("painting", ""));
        ENTITY_TYPE_MAP.put(ActorType.Shield, new ActorMapping("shield", ""));
        ENTITY_TYPE_MAP.put(ActorType.Trident, new ActorMapping("thrown_trident", ""));
        ENTITY_TYPE_MAP.put(ActorType.LargeFireball, new ActorMapping("fireball", "fireball.large"));
        ENTITY_TYPE_MAP.put(ActorType.ThrownPotion, new ActorMapping("splash_potion", "thrownpotion"));
        ENTITY_TYPE_MAP.put(ActorType.Enderpearl, new ActorMapping("ender_pearl", "thrownenderpearl"));
        ENTITY_TYPE_MAP.put(ActorType.LeashKnot, new ActorMapping("leash_knot", "leashknot"));
        ENTITY_TYPE_MAP.put(ActorType.WitherSkull, new ActorMapping("wither_skull", "wither.skull"));
        ENTITY_TYPE_MAP.put(ActorType.WitherSkullDangerous, new ActorMapping("wither_skull_dangerous", "wither.skull.dangerous"));
        ENTITY_TYPE_MAP.put(ActorType.BoatRideable, new ActorMapping("boat", ""));
        ENTITY_TYPE_MAP.put(ActorType.LightningBolt, new ActorMapping("lightning_bolt", "lightningbolt"));
        ENTITY_TYPE_MAP.put(ActorType.SmallFireball, new ActorMapping("small_fireball", "fireball.small"));
        ENTITY_TYPE_MAP.put(ActorType.LlamaSpit, new ActorMapping("llama_spit", "llama.spit"));
        ENTITY_TYPE_MAP.put(ActorType.AreaEffectCloud, new ActorMapping("area_effect_cloud", "areaeffectcloud"));
        ENTITY_TYPE_MAP.put(ActorType.LingeringPotion, new ActorMapping("lingering_potion", "lingeringpotion"));
        ENTITY_TYPE_MAP.put(ActorType.EvocationFang, new ActorMapping("evocation_fang", "evocationfang"));
        ENTITY_TYPE_MAP.put(ActorType.ArmorStand, new ActorMapping("armor_stand", "armorstand"));
        ENTITY_TYPE_MAP.put(ActorType.FireworksRocket, new ActorMapping("fireworks_rocket", ""));
        ENTITY_TYPE_MAP.put(ActorType.Agent, new ActorMapping("agent", ""));
        ENTITY_TYPE_MAP.put(ActorType.IceBomb, new ActorMapping("ice_bomb", "thrownicebomb"));
        ENTITY_TYPE_MAP.put(ActorType.Balloon, new ActorMapping("balloon", ""));
        ENTITY_TYPE_MAP.put(ActorType.Chalkboard, new ActorMapping("chalkboard", ""));
        ENTITY_TYPE_MAP.put(ActorType.Npc, new ActorMapping("npc", ""));
        ENTITY_TYPE_MAP.put(ActorType.TripodCamera, new ActorMapping("tripod_camera", "tripodcamera"));
    }

    public ActorMapping(String primary, final String alt) {
        this("minecraft", primary, alt);
    }

    public ActorMapping(final String space, final String primary, final String alt) {
        this.mNamespace = space;
        this.mPrimaryName = primary;
        this.mAlternateName = alt;
        this.mCanonicalName = getMappingName(ActorTypeNamespaceRules.ReturnWithNamespace);
    }

    public String getMappingName(ActorTypeNamespaceRules namespaceRule) {
        if (namespaceRule == ActorTypeNamespaceRules.ReturnWithNamespace) {
            return this.mNamespace + ":" + this.mPrimaryName;
        } else {
            return this.mPrimaryName;
        }
    }

    public String getCanonicalName() {
        return this.mCanonicalName;
    }
}
