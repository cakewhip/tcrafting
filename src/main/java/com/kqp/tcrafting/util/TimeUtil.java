package com.kqp.tcrafting.util;

import net.minecraft.util.Util;

/**
 * Utility class for measuring time performance.
 */
public class TimeUtil {
    public static void profile(Runnable runnable, ProfileCallback callback) {
        long start = Util.getMeasuringTimeMs();

        runnable.run();

        callback.print(Util.getMeasuringTimeMs() - start);
    }
}
