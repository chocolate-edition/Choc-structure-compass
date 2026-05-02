package com.leclowndu93150.structurecompassfix.client;

import com.leclowndu93150.structurecompassfix.item.ModItems;
import com.leclowndu93150.structurecompassfix.util.CompassData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HudHandler {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.tickCount % 10 != 0) return;
        if (!player.isHolding(ModItems.STRUCTURE_COMPASS.get())) return;

        ItemStack stack = player.getMainHandItem();
        if (!stack.is(ModItems.STRUCTURE_COMPASS.get())) {
            stack = player.getOffhandItem();
            if (!stack.is(ModItems.STRUCTURE_COMPASS.get())) return;
        }

        if (!stack.hasTag()) return;
        CompoundTag tag = stack.getTag();
        if (!tag.contains(CompassData.POS) || !tag.contains(CompassData.DIM)) return;

        ResourceLocation dim = ResourceLocation.tryParse(tag.getString(CompassData.DIM));
        if (dim == null || !player.level.dimension().location().equals(dim)) return;

        BlockPos pos = BlockPos.of(tag.getLong(CompassData.POS));
        int distance = player.blockPosition().distManhattan(pos);
        player.displayClientMessage(
                Component.literal(distance + " blocks away").withStyle(ChatFormatting.YELLOW), true);
    }
}
