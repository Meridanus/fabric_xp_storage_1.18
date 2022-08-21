package com.notker.xp_storage;

import com.google.common.math.BigIntegerMath;
import com.google.common.math.LongMath;
import com.notker.xp_storage.blocks.StorageBlockEntity;
import net.minecraft.text.Text;

import java.math.BigInteger;
import java.math.RoundingMode;

public final class XpFunctions {

    public static int exp_to_reach_next_lvl (int nextLevelExp, float progress) {
        return (nextLevelExp - xp_value_from_bar(nextLevelExp, progress));
    }

    public static int xp_value_from_bar(int nextLevelExp, float progress) {
        return (int) (nextLevelExp * progress);
    }

    public static int get_total_xp_value_from_level (int level) {

        if (level >= 1 && level <= 16) {
            return (int) (Math.pow(level, 2) + 6 * level);
        } else if (level >= 17 && level <= 31) {
            return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360);
        } else if (level >= 32) {
            return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220);
        } else {
            return 0;
        }
    }

    public static int get_total_xp (int level, int nextLevelExp, float progress) {
        return get_total_xp_value_from_level(level) + xp_value_from_bar(nextLevelExp, progress);
    }

    public static int getToNextLowerExperienceLevel(int actLevel) {
        int level = actLevel > 0 ? actLevel - 1 : actLevel;
        return getToNextExperienceLevel(level);
    }

    public static int getToNextExperienceLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else {
            return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
    }

    private static final BigInteger B72 = BigInteger.valueOf(72);
    private static final BigInteger B54215 = BigInteger.valueOf(54215);
    private static final BigInteger B325 = BigInteger.valueOf(325);
    private static final BigInteger B18 = BigInteger.valueOf(18);

    @SuppressWarnings("null")
    public static int getLevelFromExp(long exp) {
        if (exp > Long.MAX_VALUE / 72) {
            return BigIntegerMath.sqrt(BigInteger.valueOf(exp).multiply(B72).subtract(B54215), RoundingMode.DOWN).add(B325).divide(B18).intValueExact();
        }
        if (exp > Integer.MAX_VALUE) {
            return (int) ((LongMath.sqrt(72 * exp - 54215, RoundingMode.DOWN) + 325) / 18);
        }
        if (exp > 1395) {
            return (int) ((Math.sqrt(72 * exp - 54215) + 325) / 18);
        }
        if (exp > 315) {
            return (int) (Math.sqrt(40 * exp - 7839) / 10 + 8.1);
        }
        if (exp > 0) {
            return (int) (Math.sqrt(exp + 9) - 3);
        }
        return 0;
    }

    public static Text xp_to_text(StorageBlockEntity tile) {
        return xp_to_text((int)tile.liquidXp.amount / 810);
    }

    public static Text xp_to_text(int value) {
        int containerLevel = XpFunctions.getLevelFromExp(value);
        int container_excess_xp = value - XpFunctions.get_total_xp_value_from_level(containerLevel);
        int container_next_level_xp = XpFunctions.getToNextExperienceLevel(containerLevel);
        float container_progress = ((100f / container_next_level_xp) * container_excess_xp);

        String percentage = String.format(java.util.Locale.US,"%.2f", container_progress);


        if (value == 0) {
            return Text.translatable("text.storageBlock.empty");
        }

        if (container_excess_xp == 0) {
            return Text.translatable("text.storageBlock.level", containerLevel);
        }

        return Text.translatable("text.storageBlock.data", containerLevel, percentage);
    }


}


