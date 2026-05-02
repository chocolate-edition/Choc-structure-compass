package com.leclowndu93150.structurecompassfix.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class Config {

    public static final ForgeConfigSpec SPEC;
    public static final Common COMMON;

    static {
        Pair<Common, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Common::new);
        SPEC = pair.getRight();
        COMMON = pair.getLeft();
    }

    public static class Common {
        public final ForgeConfigSpec.IntValue compassRange;
        public final ForgeConfigSpec.BooleanValue locateUnexplored;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> structureBlacklist;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("General settings").push("general");

            compassRange = builder
                    .comment("Maximum range in blocks for the compass to locate structures [default: 10000]")
                    .defineInRange("compassRange", 10000, 0, Integer.MAX_VALUE);

            locateUnexplored = builder
                    .comment("Only locate unexplored structures [default: false]")
                    .define("locateUnexplored", false);

            structureBlacklist = builder
                    .comment("Blacklisted structures (supports wildcards, e.g. 'minecraft:*')")
                    .defineListAllowEmpty(List.of("structureBlacklist"), () -> List.of(""), o -> o instanceof String);

            builder.pop();
        }
    }

    public static boolean isBlacklisted(ResourceLocation location) {
        if (location == null) return false;
        List<? extends String> list = COMMON.structureBlacklist.get();
        if (list.isEmpty()) return false;
        if (list.contains(location.toString())) return true;
        for (String wildcard : list) {
            if (!wildcard.contains(":") || !wildcard.contains("*")) continue;
            String[] parts = wildcard.split(":");
            if (parts.length != 2) continue;
            if (parts[0].equals("*") && location.getPath().equals(parts[1])) return true;
            if (parts[1].equals("*") && location.getNamespace().equals(parts[0])) return true;
        }
        return false;
    }
}
