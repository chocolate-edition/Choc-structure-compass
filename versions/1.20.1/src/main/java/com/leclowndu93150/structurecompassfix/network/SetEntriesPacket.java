package com.leclowndu93150.structurecompassfix.network;

import com.leclowndu93150.structurecompassfix.item.StructureCompassItem;
import com.leclowndu93150.structurecompassfix.util.CompassData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SetEntriesPacket {

    private final InteractionHand hand;
    private final List<String> entries;

    public SetEntriesPacket(InteractionHand hand, List<String> entries) {
        this.hand = hand;
        this.entries = entries;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(hand == InteractionHand.MAIN_HAND ? 0 : 1);
        buf.writeInt(entries.size());
        for (String e : entries) {
            buf.writeUtf(e);
        }
    }

    public static SetEntriesPacket decode(FriendlyByteBuf buf) {
        InteractionHand hand = buf.readInt() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        int size = buf.readInt();
        List<String> entries = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            entries.add(buf.readUtf());
        }
        return new SetEntriesPacket(hand, entries);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player != null) {
                ItemStack stack = player.getItemInHand(hand);
                if (stack.getItem() instanceof StructureCompassItem) {
                    CompoundTag tag = stack.getOrCreateTag();
                    CompassData.setEntries(tag, entries);
                    stack.setTag(tag);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
