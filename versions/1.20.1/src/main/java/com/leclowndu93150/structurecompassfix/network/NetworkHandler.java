package com.leclowndu93150.structurecompassfix.network;

import com.leclowndu93150.structurecompassfix.StructureCompassFixForge;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {

    private static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(StructureCompassFixForge.MOD_ID, "main"),
            () -> VERSION, VERSION::equals, VERSION::equals);

    private static int id = 0;

    public static void init() {
        CHANNEL.registerMessage(id++, SetEntriesPacket.class, SetEntriesPacket::encode, SetEntriesPacket::decode, SetEntriesPacket::handle);
        CHANNEL.registerMessage(id++, OpenScreenPacket.class, OpenScreenPacket::encode, OpenScreenPacket::decode, OpenScreenPacket::handle);
    }
}
