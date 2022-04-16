package com.notker.xp_storage.fluids;

import com.notker.xp_storage.regestry.ModFluids;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public class LiquidXP extends LiquidXPAbstract {

    @Override
    public Fluid getStill() {
        return ModFluids.LIQUID_XP;
    }

    @Override
    public int getLevel(FluidState state) {
        return 0;
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.LIQUID_XP_FLOWING;
    }

    @Override
    public Item getBucketItem() {
        return ModFluids.XP_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState) {
        return ModFluids.XP_FLUID.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(fluidState));
    }

    @Override
    public boolean isStill(FluidState state) {
        return false;
    }

    public static class Flowing extends LiquidXP {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return false;
        }
    }

    public static class Still extends LiquidXP {
        @Override
        public int getLevel(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return true;
        }
    }
}
