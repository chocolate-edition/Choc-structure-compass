package com.leclowndu93150.structurecompassfix.item;

import com.leclowndu93150.structurecompassfix.config.Config;
import com.leclowndu93150.structurecompassfix.network.NetworkHandler;
import com.leclowndu93150.structurecompassfix.network.OpenScreenPacket;
import com.leclowndu93150.structurecompassfix.util.CompassData;
import com.leclowndu93150.structurecompassfix.util.StructureLocator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

public class StructureCompassItem extends Item {

    public StructureCompassItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && player instanceof ServerPlayer sp) {
                List<ResourceLocation> structures = StructureLocator.getAvailableList(level);
                List<ResourceLocation> tags = StructureLocator.getAvailableTags(level);
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
                        new OpenScreenPacket(hand, stack, structures, tags));
            }
        } else {
            if (!level.isClientSide) {
                locateStructures(stack, player);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private void locateStructures(ItemStack stack, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        List<String> entries = CompassData.getEntries(tag);
        if (entries.isEmpty()) {
            player.sendSystemMessage(Component.translatable("structurecompassfix.unset").withStyle(ChatFormatting.YELLOW));
            return;
        }

        ServerLevel level = (ServerLevel) player.level;
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY);
        HolderSet<Structure> holderSet = StructureLocator.resolve(entries, registry);

        if (holderSet == null || holderSet.size() == 0) {
            player.sendSystemMessage(Component.translatable("structurecompassfix.fail").withStyle(ChatFormatting.RED));
            return;
        }

        player.sendSystemMessage(Component.translatable("structurecompassfix.locating").withStyle(ChatFormatting.YELLOW));

        boolean findUnexplored = Config.COMMON.locateUnexplored.get();
        int range = Config.COMMON.compassRange.get();
        var pair = level.getChunkSource().getGenerator()
                .findNearestMapStructure(level, holderSet, player.blockPosition(), 100, findUnexplored);

        BlockPos pos = pair != null ? pair.getFirst() : null;
        if (pos == null || player.blockPosition().distManhattan(pos) > range) {
            CompassData.setNotFound(tag);
            player.sendSystemMessage(Component.translatable("structurecompassfix.notfound").withStyle(ChatFormatting.RED));
        } else {
            CompassData.setFound(tag, pos, level.dimension().location());
            int dist = player.blockPosition().distManhattan(pos);
            player.sendSystemMessage(Component.translatable("structurecompassfix.found", dist).withStyle(ChatFormatting.GREEN));
        }
        stack.setTag(tag);
        player.getCooldowns().addCooldown(this, 100);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (!stack.hasTag()) {
            tooltip.add(Component.translatable("structurecompassfix.unset").withStyle(ChatFormatting.GOLD));
            return;
        }
        CompoundTag tag = stack.getTag();
        List<String> entries = CompassData.getEntries(tag);
        if (entries.isEmpty()) {
            tooltip.add(Component.translatable("structurecompassfix.unset").withStyle(ChatFormatting.GOLD));
            return;
        }
        boolean found = tag.getBoolean(CompassData.FOUND);
        ChatFormatting color = found ? ChatFormatting.GREEN : ChatFormatting.YELLOW;
        tooltip.add(Component.literal(entries.size() + " structure(s) selected").withStyle(color));
        for (String e : entries) {
            tooltip.add(Component.literal("  " + CompassData.displayName(e)).withStyle(ChatFormatting.GRAY));
        }
    }
}
