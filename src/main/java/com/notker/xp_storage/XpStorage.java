package com.notker.xp_storage;

import com.notker.xp_storage.regestry.ModBlocks;
import com.notker.xp_storage.regestry.ModFluids;
import com.notker.xp_storage.regestry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("UnstableApiUsage")
public class XpStorage implements ModInitializer {

    public static final  String MOD_ID = "xps";
    public static final Long MB_PER_XP = FluidConstants.BUCKET / 100;
    public static final  int XP_PER_BERRIE = 3;
    public static final  Long MB_PER_BERRIE = XP_PER_BERRIE * MB_PER_XP;

    public static final int shiftKey = 340;

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    private static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(MOD_ID, "general"));

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, FabricItemGroup.builder()
                .displayName(Text.translatable("block.xps.block_xp_obelisk"))
                .icon(() -> new ItemStack(ModItems.BLOCK_XP_OBELISK))
                .entries((context, entries) -> {
                    entries.add(ModItems.BLOCK_XP_OBELISK);
                    entries.add(ModItems.XP_BERRIES);
                    entries.add(ModItems.XP_ROD);
                    entries.add(ModItems.SOUL_COPPER_BLEND);
                    entries.add(ModItems.SOUL_COPPER_INGOT);
                    entries.add(ModItems.SOUL_COPPER_NUGGET);
                    entries.add(ModItems.XP_REMOVER);
                    entries.add(ModItems.INSPECTOR);
                    entries.add(ModItems.LOCK);
                    entries.add(ModItems.XP_DUST);
                    entries.add(ModItems.KEY);
                    entries.add(ModItems.HANDBOOK);
                    entries.add(ModBlocks.BLOCK_SOUL_COPPER);
                    entries.add(ModBlocks.XP_BERRIE_BUSH_BLOCK);
                    entries.add(ModFluids.XP_BUCKET);
                })
                .build()
        );

        ModItems.registerItems();
        ModBlocks.registerBlocks();
        ModFluids.registerFluids();
    }


}
