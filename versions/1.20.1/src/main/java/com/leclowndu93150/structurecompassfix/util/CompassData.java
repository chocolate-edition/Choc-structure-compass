package com.leclowndu93150.structurecompassfix.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class CompassData {

    public static final String ENTRIES = "scf_entries";
    public static final String FOUND = "scf_found";
    public static final String POS = "scf_pos";
    public static final String DIM = "scf_dim";
    public static final String TAG_PREFIX = "#";

    public static List<String> getEntries(CompoundTag tag) {
        List<String> result = new ArrayList<>();
        if (tag == null || !tag.contains(ENTRIES, Tag.TAG_LIST)) return result;
        ListTag list = tag.getList(ENTRIES, Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            result.add(list.getString(i));
        }
        return result;
    }

    public static void setEntries(CompoundTag tag, List<String> entries) {
        ListTag list = new ListTag();
        for (String e : entries) {
            list.add(StringTag.valueOf(e));
        }
        tag.put(ENTRIES, list);
        tag.remove(FOUND);
        tag.remove(POS);
        tag.remove(DIM);
    }

    public static void setFound(CompoundTag tag, BlockPos pos, ResourceLocation dim) {
        tag.putBoolean(FOUND, true);
        tag.putLong(POS, pos.asLong());
        tag.putString(DIM, dim.toString());
    }

    public static void setNotFound(CompoundTag tag) {
        tag.putBoolean(FOUND, false);
        tag.remove(POS);
        tag.remove(DIM);
    }

    public static boolean isTag(String entry) {
        return entry.startsWith(TAG_PREFIX);
    }

    public static String displayName(String entry) {
        return entry;
    }

    public static ResourceLocation parseStructure(String entry) {
        return ResourceLocation.tryParse(entry);
    }

    public static ResourceLocation parseTag(String entry) {
        return ResourceLocation.tryParse(entry.substring(1));
    }
}
