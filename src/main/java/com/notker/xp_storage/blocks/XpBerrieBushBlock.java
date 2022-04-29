package com.notker.xp_storage.blocks;

import com.notker.xp_storage.regestry.ModItems;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class XpBerrieBushBlock extends CropBlock {
    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
            Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 5.0D, 12.0D),
            Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 7.0D, 12.0D),
            Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 9.0D, 13.0D),
            Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 12.0D, 13.0D),
            Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D),
            Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D),
            Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D),
            Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D)
    };

    public static final int max_age = 7;

    @Override
    public int getMaxAge() {
        return max_age;
    }

    public XpBerrieBushBlock(Settings settings) {
        super(settings);
    }

    public ItemConvertible getSeedsItem() {
        return ModItems.XP_BERRIES;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE[state.get(this.getAgeProperty())];
    }

    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return state.get(AGE) <= max_age;
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.NETHERRACK) || floor.isOf(Blocks.WARPED_NYLIUM) || floor.isOf(Blocks.CRIMSON_NYLIUM);
    }

    public boolean hasRandomTicks(BlockState state) {
        return state.get(AGE) < max_age;
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = state.get(AGE);
        if (i < max_age && random.nextInt(10) == 0) {
            state = state.with(AGE, i + 1);
            world.setBlockState(pos, state, 2);
        }

    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = Math.min(max_age, state.get(AGE) + 1);
        world.setBlockState(pos, state.with(AGE, i), 2);
    }

    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(this.getSeedsItem());
    }


    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int i = state.get(AGE);
        boolean isFullGrown = i == max_age;
        if (!isFullGrown && player.getStackInHand(hand).isOf(Items.BONE_MEAL)) {
            return ActionResult.PASS;
        } else if (i > max_age - 2) {
            int bonusDrops = 2 + world.random.nextInt(3);
            dropStack(world, pos, new ItemStack(ModItems.XP_BERRIES, 1 + (isFullGrown ? bonusDrops : 0)));
            world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            world.setBlockState(pos, state.with(AGE, max_age - 3), 2);
            return ActionResult.success(world.isClient);
        } else {
            return super.onUse(state, world, pos, player, hand, hit);
        }
    }
}
