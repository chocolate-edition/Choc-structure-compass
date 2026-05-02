package com.leclowndu93150.structurecompassfix.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class OpenScreenPacket {

    private final InteractionHand hand;
    private final ItemStack compass;
    private final List<ResourceLocation> structures;
    private final List<ResourceLocation> tags;

    public OpenScreenPacket(InteractionHand hand, ItemStack compass, List<ResourceLocation> structures, List<ResourceLocation> tags) {
        this.hand = hand;
        this.compass = compass;
        this.structures = structures;
        this.tags = tags;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(hand == InteractionHand.MAIN_HAND ? 0 : 1);
        buf.writeItemStack(compass, false);
        buf.writeInt(structures.size());
        for (ResourceLocation loc : structures) buf.writeResourceLocation(loc);
        buf.writeInt(tags.size());
        for (ResourceLocation loc : tags) buf.writeResourceLocation(loc);
    }

    public static OpenScreenPacket decode(FriendlyByteBuf buf) {
        InteractionHand hand = buf.readInt() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack stack = buf.readItem();
        int sSize = buf.readInt();
        List<ResourceLocation> structures = new ArrayList<>();
        for (int i = 0; i < sSize; i++) structures.add(buf.readResourceLocation());
        int tSize = buf.readInt();
        List<ResourceLocation> tags = new ArrayList<>();
        for (int i = 0; i < tSize; i++) tags.add(buf.readResourceLocation());
        return new OpenScreenPacket(hand, stack, structures, tags);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                com.leclowndu93150.structurecompassfix.client.ClientSetup.openScreen(hand, compass, structures, tags);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
