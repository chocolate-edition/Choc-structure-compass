package com.leclowndu93150.structurecompassfix.client;

import com.leclowndu93150.structurecompassfix.client.screen.CompassScreen;
import com.leclowndu93150.structurecompassfix.item.ModItems;
import com.leclowndu93150.structurecompassfix.util.CompassData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nullable;
import java.util.List;

public class ClientSetup {

    @SuppressWarnings("deprecation")
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(ModItems.STRUCTURE_COMPASS.get(), new ResourceLocation("angle"), new net.minecraft.client.renderer.item.ItemPropertyFunction() {
            @OnlyIn(Dist.CLIENT) private double rotation;
            @OnlyIn(Dist.CLIENT) private double rota;
            @OnlyIn(Dist.CLIENT) private long lastTick;

            @Override
            public float call(ItemStack stack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity entity, int seed) {
                if (entity == null && !stack.isFramed()) return 0.0F;
                boolean hasEntity = entity != null;
                Entity e = hasEntity ? entity : stack.getFrame();
                if (clientLevel == null && e.level() instanceof ClientLevel cl) clientLevel = cl;

                double d0;
                BlockPos target = getTarget(stack);
                ResourceLocation dim = getDim(stack);
                if (target != null && dim != null && clientLevel.dimension().location().equals(dim)) {
                    double yRot = hasEntity ? entity.getYRot() : getFrameRotation((ItemFrame) e);
                    yRot = Mth.positiveModulo(yRot / 360.0, 1.0);
                    double angle = Math.atan2((double) target.getZ() - e.getZ(), (double) target.getX() - e.getX()) / (Math.PI * 2);
                    d0 = 0.5 - (yRot - 0.25 - angle);
                } else {
                    d0 = Math.random();
                }

                if (hasEntity) {
                    if (clientLevel.getGameTime() != lastTick) {
                        lastTick = clientLevel.getGameTime();
                        double diff = d0 - rotation;
                        diff = Mth.positiveModulo(diff + 0.5, 1.0) - 0.5;
                        rota += diff * 0.1;
                        rota *= 0.8;
                        rotation = Mth.positiveModulo(rotation + rota, 1.0);
                    }
                    d0 = rotation;
                }
                return Mth.positiveModulo((float) d0, 1.0F);
            }

            private double getFrameRotation(ItemFrame frame) {
                Direction dir = frame.getDirection();
                int i = dir.getAxis().isVertical() ? 90 * dir.getAxisDirection().getStep() : 0;
                return Mth.wrapDegrees(180 + dir.get2DDataValue() * 90 + frame.getRotation() * 45 + i);
            }

            private BlockPos getTarget(ItemStack stack) {
                if (stack.hasTag() && stack.getTag().getBoolean(CompassData.FOUND) && stack.getTag().contains(CompassData.POS)) {
                    return BlockPos.of(stack.getTag().getLong(CompassData.POS));
                }
                return null;
            }

            private ResourceLocation getDim(ItemStack stack) {
                if (stack.hasTag() && stack.getTag().contains(CompassData.DIM)) {
                    return ResourceLocation.tryParse(stack.getTag().getString(CompassData.DIM));
                }
                return null;
            }
        }));
    }

    public static void openScreen(InteractionHand hand, ItemStack compass, List<ResourceLocation> structures, List<ResourceLocation> tags) {
        Minecraft.getInstance().setScreen(new CompassScreen(hand, compass, structures, tags));
    }
}
