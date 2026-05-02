package com.leclowndu93150.structurecompassfix;

import com.leclowndu93150.structurecompassfix.client.ClientSetup;
import com.leclowndu93150.structurecompassfix.client.HudHandler;
import com.leclowndu93150.structurecompassfix.config.Config;
import com.leclowndu93150.structurecompassfix.item.ModItems;
import com.leclowndu93150.structurecompassfix.network.NetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("structurecompassfix")
public class StructureCompassFixForge {

    public static final String MOD_ID = "structurecompassfix";

    public StructureCompassFixForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        ModItems.ITEMS.register(bus);
        ModItems.TABS.register(bus);
        bus.addListener(this::commonSetup);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(ClientSetup::onClientSetup);
            MinecraftForge.EVENT_BUS.register(new HudHandler());
        });
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.init();
    }
}
