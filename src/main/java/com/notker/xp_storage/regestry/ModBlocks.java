package com.notker.xp_storage.regestry;

import com.notker.xp_storage.XpStorage;
import com.notker.xp_storage.blocks.StorageBlock;
import com.notker.xp_storage.blocks.StorageBlockEntity;
import com.notker.xp_storage.blocks.XpBerrieBushBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final StorageBlock BLOCK_XP_OBELISK = new StorageBlock(FabricBlockSettings
            .of(Material.METAL)
            .sounds(BlockSoundGroup.METAL)
            .strength(0.25f, 1000.0f)
            .requiresTool()
            .luminance(10)
    );

    public static final Block BLOCK_SOUL_COPPER = new Block(FabricBlockSettings
            .of(Material.METAL)
            .sounds(BlockSoundGroup.METAL)
            .strength(0.25f, 1000f)
            .requiresTool()
    );


    public static final String TAG_ID = "BlockEntityTag";
    public static final BlockEntityType<StorageBlockEntity> STORAGE_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(StorageBlockEntity::new, BLOCK_XP_OBELISK).build(null);

    public static final CropBlock XP_BERRIE_BUSH_BLOCK = new XpBerrieBushBlock(FabricBlockSettings.of(Material.PLANT)
            .nonOpaque()
            .noCollision()
            .ticksRandomly()
            .breakInstantly()
            .sounds(BlockSoundGroup.CROP)
            .emissiveLighting((state, world, pos) -> true));

    @SuppressWarnings("UnstableApiUsage")
    public static void registerBlocks() {
        Registry.register(Registries.BLOCK, new Identifier(XpStorage.MOD_ID, "block_xp_obelisk"), BLOCK_XP_OBELISK);

        Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(XpStorage.MOD_ID, "entity_xp_obelisk"), STORAGE_BLOCK_ENTITY);
        Registry.register(Registries.BLOCK, new Identifier(XpStorage.MOD_ID, "block_soul_copper"), BLOCK_SOUL_COPPER);

        Registry.register(Registries.BLOCK, new Identifier(XpStorage.MOD_ID,"xp_berrie_bush_block"), XP_BERRIE_BUSH_BLOCK);

        //BlockEntityType<StorageBlockEntity> STORAGE = null;
//        FluidStorage.SIDED.registerForBlockEntity((storage, direction) -> switch (direction) {
//            case DOWN -> storage.liquidXp;
//            default -> null;
//        }, STORAGE_BLOCK_ENTITY);
        FluidStorage.SIDED.registerForBlockEntity((storage, direction) -> storage.liquidXp, STORAGE_BLOCK_ENTITY);
    }
}
