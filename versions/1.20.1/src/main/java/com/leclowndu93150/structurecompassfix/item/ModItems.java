package com.leclowndu93150.structurecompassfix.item;

import com.leclowndu93150.structurecompassfix.StructureCompassFixForge;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, StructureCompassFixForge.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StructureCompassFixForge.MOD_ID);

    public static final RegistryObject<Item> STRUCTURE_COMPASS = ITEMS.register("structure_compass",
            () -> new StructureCompassItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> STRUCTURE_COMPASS.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup.structurecompassfix"))
                    .displayItems((params, output) -> output.accept(STRUCTURE_COMPASS.get()))
                    .build());
}
