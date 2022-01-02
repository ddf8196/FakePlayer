package com.ddf.fakeplayer.util.mc;

import com.ddf.fakeplayer.util.NotImplemented;

public class VanillaBlockUpdater {
    private static CompoundTagUpdaterContext mContext;

    @NotImplemented
    public static void initialize() {
//        CompoundTagUpdaterContext context = VanillaBlockUpdater.get();
//        VanillaBlockUpdater.addBaseUpdater(context);
//        VanillaBlockUpdater.addUpdaters_1_10_0(context);
//        VanillaBlockUpdater.addUpdaters_1_12_0(context);
//        VanillaBlockUpdater.addUpdaters_1_13_0(context);
//        VanillaBlockUpdater.addUpdaters_1_14_0(context);
//        VanillaBlockUpdater.addUpdaters_1_15_0(context);
//        context.sortUpdaters();
    }

    public static CompoundTagUpdaterContext get() {
        if (VanillaBlockUpdater.mContext == null) {
            VanillaBlockUpdater.mContext = new CompoundTagUpdaterContext();
        }
        return VanillaBlockUpdater.mContext;
    }
}
