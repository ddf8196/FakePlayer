package com.ddf.fakeplayer.level;

public enum LevelEvent {
    Undefined_4((short) 0x0),
    SoundClick((short) 0x3E8),
    SoundClickFail((short) 0x3E9),
    SoundLaunch((short) 0x3EA),
    SoundOpenDoor((short) 0x3EB),
    SoundFizz((short) 0x3EC),
    SoundFuse((short) 0x3ED),
    SoundPlayRecording((short) 0x3EE),
    SoundGhastWarning((short) 0x3EF),
    SoundGhastFireball((short) 0x3F0),
    SoundBlazeFireball((short) 0x3F1),
    SoundZombieWoodenDoor((short) 0x3F2),
    SoundZombieDoorCrash((short) 0x3F4),
    SoundZombieInfected((short) 0x3F8),
    SoundZombieConverted((short) 0x3F9),
    SoundEndermanTeleport((short) 0x3FA),
    SoundAnvilBroken((short) 0x3FC),
    SoundAnvilUsed((short) 0x3FD),
    SoundAnvilLand((short) 0x3FE),
    SoundInfinityArrowPickup((short) 0x406),
    SoundTeleportEnderPearl((short) 0x408),
    SoundAddItem((short) 0x410),
    SoundItemFrameBreak((short) 0x411),
    SoundItemFramePlace((short) 0x412),
    SoundItemFrameRemoveItem((short) 0x413),
    SoundItemFrameRotateItem((short) 0x414),
    SoundExperienceOrbPickup((short) 0x41B),
    SoundTotemUsed((short) 0x41C),
    SoundArmorStandBreak((short) 0x424),
    SoundArmorStandHit((short) 0x425),
    SoundArmorStandLand((short) 0x426),
    SoundArmorStandPlace((short) 0x427),
    ParticlesShoot((short) 0x7D0),
    ParticlesDestroyBlock((short) 0x7D1),
    ParticlesPotionSplash((short) 0x7D2),
    ParticlesEyeOfEnderDeath((short) 0x7D3),
    ParticlesMobBlockSpawn((short) 0x7D4),
    ParticleCropGrowth((short) 0x7D5),
    ParticleSoundGuardianGhost((short) 0x7D6),
    ParticleDeathSmoke((short) 0x7D7),
    ParticleDenyBlock((short) 0x7D8),
    ParticleGenericSpawn((short) 0x7D9),
    ParticlesDragonEgg((short) 0x7DA),
    ParticlesCropEaten((short) 0x7DB),
    ParticlesCrit((short) 0x7DC),
    ParticlesTeleport((short) 0x7DD),
    ParticlesCrackBlock((short) 0x7DE),
    ParticlesBubble((short) 0x7DF),
    ParticlesEvaporate((short) 0x7E0),
    ParticlesDestroyArmorStand((short) 0x7E1),
    ParticlesBreakingEgg((short) 0x7E2),
    ParticleDestroyEgg((short) 0x7E3),
    ParticlesEvaporateWater((short) 0x7E4),
    ParticlesDestroyBlockNoSound((short) 0x7E5),
    ParticlesKnockbackRoar((short) 0x7E6),
    ParticlesTeleportTrail((short) 0x7E7),
    ParticlesPointCloud((short) 0x7E8),
    ParticlesExplosion((short) 0x7E9),
    ParticlesBlockExplosion((short) 0x7EA),
    StartRaining((short) 0xBB9),
    StartThunderstorm((short) 0xBBA),
    StopRaining((short) 0xBBB),
    StopThunderstorm((short) 0xBBC),
    GlobalPause((short) 0xBBD),
    SimTimeStep((short) 0xBBE),
    SimTimeScale((short) 0xBBF),
    ActivateBlock((short) 0xDAC),
    CauldronExplode((short) 0xDAD),
    CauldronDyeArmor((short) 0xDAE),
    CauldronCleanArmor((short) 0xDAF),
    CauldronFillPotion((short) 0xDB0),
    CauldronTakePotion((short) 0xDB1),
    CauldronFillWater((short) 0xDB2),
    CauldronTakeWater((short) 0xDB3),
    CauldronAddDye((short) 0xDB4),
    CauldronCleanBanner((short) 0xDB5),
    CauldronFlush((short) 0xDB6),
    AgentSpawnEffect((short) 0xDB7),
    CauldronFillLava((short) 0xDB8),
    CauldronTakeLava((short) 0xDB9),
    StartBlockCracking((short) 0xE10),
    StopBlockCracking((short) 0xE11),
    UpdateBlockCracking((short) 0xE12),
    AllPlayersSleeping((short) 0x2648),
    JumpPrevented((short) 0x2652),
    ParticleLegacyEvent((short) 0x4000);

    private final short value;

    LevelEvent(short value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LevelEvent getByValue(short value) {
        for (LevelEvent levelEvent : values()) {
            if (levelEvent.getValue() == value) {
                return levelEvent;
            }
        }
        return null;
    }
}
