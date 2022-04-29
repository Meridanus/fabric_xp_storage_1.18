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
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

public class ModBlocks {

    public static final StorageBlock BLOCK_XP_OBELISK = new StorageBlock(FabricBlockSettings
            .of(Material.METAL)
            .sounds(BlockSoundGroup.METAL)
            .strength(0.25f, 1000.0f)
            .requiresTool()
    );

    public static final Block BLOCK_SOUL_COPPER = new Block(FabricBlockSettings
            .of(Material.METAL)
            .sounds(BlockSoundGroup.METAL)
            .strength(0.25f, 1000f)
            .requiresTool()
    );


    public static final String TAG_ID = "BlockEntityTag";
    public static final BlockEntityType<StorageBlockEntity> STORAGE_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(StorageBlockEntity::new, BLOCK_XP_OBELISK).build(null);

    public static final CropBlock XP_BERRIE_BUSH_BLOCK = new XpBerrieBushBlock(AbstractBlock.Settings.of(Material.PLANT)
            .nonOpaque()
            .noCollision()
            .ticksRandomly()
            .breakInstantly()
            .sounds(BlockSoundGroup.CROP)
            .emissiveLighting(new AbstractBlock.ContextPredicate() {
                @Override
                public boolean test(BlockState state, BlockView world, BlockPos pos) {
                    return true;
                }
            }));

    public static void registerBlocks() {
        Registry.register(Registry.BLOCK, new Identifier(XpStorage.MOD_ID, "block_xp_obelisk"), BLOCK_XP_OBELISK);

        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(XpStorage.MOD_ID, "entity_xp_obelisk"), STORAGE_BLOCK_ENTITY);
        Registry.register(Registry.BLOCK, new Identifier(XpStorage.MOD_ID, "block_soul_copper"), BLOCK_SOUL_COPPER);

        Registry.register(Registry.BLOCK, new Identifier(XpStorage.MOD_ID,"xp_berrie_bush_block"), XP_BERRIE_BUSH_BLOCK);

        BlockEntityType<StorageBlockEntity> STORAGE = null;
//        FluidStorage.SIDED.registerForBlockEntity((storage, direction) -> switch (direction) {
//            case DOWN -> storage.liquidXp;
//            default -> null;
//        }, STORAGE_BLOCK_ENTITY);
        FluidStorage.SIDED.registerForBlockEntity((storage, direction) -> storage.liquidXp, STORAGE_BLOCK_ENTITY);
    }
}
