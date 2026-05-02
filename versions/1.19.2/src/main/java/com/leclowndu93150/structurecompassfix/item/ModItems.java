package com.leclowndu93150.structurecompassfix.item;

import com.leclowndu93150.structurecompassfix.StructureCompassFixForge;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, StructureCompassFixForge.MOD_ID);

    public static final CreativeModeTab TAB = new CreativeModeTab("structurecompassfix") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(STRUCTURE_COMPASS.get());
        }
    };

    public static final RegistryObject<Item> STRUCTURE_COMPASS = ITEMS.register("structure_compass",
            () -> new StructureCompassItem(new Item.Properties().stacksTo(1).tab(TAB)));

}
