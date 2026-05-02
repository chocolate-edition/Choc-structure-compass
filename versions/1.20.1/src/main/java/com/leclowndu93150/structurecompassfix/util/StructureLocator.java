package com.leclowndu93150.structurecompassfix.util;

import com.leclowndu93150.structurecompassfix.config.Config;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;

public class StructureLocator {

    public static List<ResourceLocation> getAvailableList(Level level) {
        List<ResourceLocation> result = new ArrayList<>();
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        registry.holders().forEach(holder -> {
            ResourceLocation loc = holder.key().location();
            if (!result.contains(loc) && !Config.isBlacklisted(loc)) {
                result.add(loc);
            }
        });
        return result;
    }

    public static List<ResourceLocation> getAvailableTags(Level level) {
        List<ResourceLocation> result = new ArrayList<>();
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        registry.getTagNames().forEach(tagKey -> {
            ResourceLocation loc = tagKey.location();
            if (!result.contains(loc)) {
                result.add(loc);
            }
        });
        return result;
    }

    public static HolderSet<Structure> resolve(List<String> entries, Registry<Structure> registry) {
        List<Holder<Structure>> holders = new ArrayList<>();
        for (String entry : entries) {
            if (CompassData.isTag(entry)) {
                ResourceLocation tagLoc = CompassData.parseTag(entry);
                if (tagLoc != null) {
                    TagKey<Structure> tagKey = TagKey.create(Registries.STRUCTURE, tagLoc);
                    registry.getTag(tagKey).ifPresent(named -> named.forEach(holders::add));
                }
            } else {
                ResourceLocation loc = CompassData.parseStructure(entry);
                if (loc != null && !Config.isBlacklisted(loc)) {
                    ResourceKey<Structure> key = ResourceKey.create(Registries.STRUCTURE, loc);
                    registry.getHolder(key).ifPresent(holders::add);
                }
            }
        }
        if (holders.isEmpty()) return null;
        return HolderSet.direct(holders);
    }
}
