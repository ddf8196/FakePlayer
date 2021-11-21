package com.ddf.fakeplayer.level.dimension;

import com.ddf.fakeplayer.util.Vec3;

import java.util.HashMap;
import java.util.Map;

public class VanillaDimensions {
    public static final Map<String, Integer> DimensionMap = new HashMap<>();
    public static final int Overworld = 0;
    public static final int Nether = 1;
    public static final int TheEnd = 2;
    public static final int Undefined = 3;
    public static final Vec3 TheEndSpawnPoint  = new Vec3(100.0f, 50.0f, 0.0f);

    static {
        DimensionMap.put("overworld", VanillaDimensions.Overworld);
        DimensionMap.put("nether", VanillaDimensions.Nether);
        DimensionMap.put("the end", VanillaDimensions.TheEnd);
    }

    public static boolean _convertPointFromEnd(final Vec3 startingPos, Vec3 outputPos, /*DimensionType*/int toID, final Vec3 OverworldSpawnPos) {
        if (toID == VanillaDimensions.Overworld) {
            outputPos.set(OverworldSpawnPos);
            return true;
        } else if (toID == VanillaDimensions.TheEnd) {
            outputPos.set(startingPos);
            return true;
        } else {
            outputPos.set(Vec3.ZERO);
            return false;
        }
    }

    public static boolean _convertPointFromNether(final Vec3 startingPos, Vec3 outputPos, /*DimensionType*/int toID, int netherScale) {
        if (toID == VanillaDimensions.Overworld){
            outputPos.set(startingPos);
            outputPos.x = (float) netherScale * outputPos.x;
            outputPos.z = (float) netherScale * outputPos.z;
            return true;
        } else if (toID == VanillaDimensions.Nether) {
            outputPos.set(startingPos);
            return true;
        } else if (toID == VanillaDimensions.TheEnd) {
            outputPos.set(VanillaDimensions.TheEndSpawnPoint);
            return true;
        } else {
            outputPos.set(Vec3.ZERO);
            return false;
        }
    }

    public static boolean _convertPointFromOverworld(final Vec3 startingPos, Vec3 outputPos, /*DimensionType*/int toID, int netherScale) {
        if (toID == VanillaDimensions.Overworld) {
            outputPos.set(startingPos);
            return true;
        } else if (toID == VanillaDimensions.Nether) {
            outputPos.set(startingPos);
            outputPos.x = outputPos.x / (float) netherScale;
            outputPos.z = outputPos.z / (float) netherScale;
            return true;
        } else if (toID == VanillaDimensions.TheEnd) {
            outputPos.set(VanillaDimensions.TheEndSpawnPoint);
            return true;
        } else {
            outputPos.set(Vec3.ZERO);
            return false;
        }
    }

    public static boolean convertPointBetweenDimensions(final Vec3 startingPosition, Vec3 outputPosition, /*DimensionType*/int fromID, /*DimensionType*/int toID, final DimensionConversionData data) {
        if (fromID == toID) {
            outputPosition.set(startingPosition);
            return true;
        } else if (fromID == VanillaDimensions.Overworld) {
            return VanillaDimensions._convertPointFromOverworld(startingPosition, outputPosition, toID, data.getNetherScale());
        } else if (fromID == VanillaDimensions.Nether) {
            return VanillaDimensions._convertPointFromNether(startingPosition, outputPosition, toID, data.getNetherScale());
        } else if (fromID == VanillaDimensions.TheEnd) {
            return VanillaDimensions._convertPointFromEnd(startingPosition, outputPosition, toID, data.getOverworldSpawnPoint());
        } else {
            return false;
        }
    }

    public static int toSerializedInt(int id) {
        if (VanillaDimensions.Overworld == id )
            return 0;
        if (VanillaDimensions.Nether == id )
            return 1;
        if (VanillaDimensions.TheEnd == id )
            return 2;
        return 3;
    }
}
