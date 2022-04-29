package com.notker.xp_storage.blocks;

import com.notker.xp_storage.XpFunctions;
import com.notker.xp_storage.XpStorage;
import com.notker.xp_storage.items.Xp_removerItem;
import com.notker.xp_storage.regestry.ModBlocks;
import com.notker.xp_storage.regestry.ModFluids;
import com.notker.xp_storage.regestry.ModItems;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class StorageBlock extends BlockWithEntity implements BlockEntityProvider, Waterloggable {
    public static final BooleanProperty CHARGED = BooleanProperty.of("charged");
    //public static final BooleanProperty VACUUM = BooleanProperty.of("vacuum");

    public StorageBlock(Settings settings) {
        super(settings.nonOpaque());
        setDefaultState(getStateManager().getDefaultState()
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
                .with(CHARGED, false)
                .with(Properties.WATERLOGGED, false)
                /*.with(VACUUM, false)*/);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite()).with(Properties.WATERLOGGED, bl);
    }


    @SuppressWarnings("deprecation")
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(Properties.WATERLOGGED)) {
            //world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            world.createAndScheduleFluidTick(pos, Fluids.WATER,Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!(Boolean)state.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
            BlockState blockState = state.with(Properties.WATERLOGGED, true);

                world.setBlockState(pos, blockState, 3);


            //world.getFluidTickScheduler().schedule(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
            world.createAndScheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
            return true;
        } else {
            return false;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {

        VoxelShape bottom = Block.createCuboidShape(1D, 0D, 1D, 15D, 2D, 15D);
        VoxelShape top = Block.createCuboidShape(2D, 2D, 2D, 14D, 14D, 14D);

        return VoxelShapes.union(bottom, top);

    }


    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        final StorageBlockEntity tile = (StorageBlockEntity) world.getBlockEntity(pos);
        if (tile == null) { return; }

        ItemStack drop = new ItemStack(asItem());

        if (tile.liquidXp.amount > 0 || !tile.player_uuid.equals(Util.NIL_UUID)) {
            NbtCompound stackTag = new NbtCompound();
            //stackTag.put(ModBlocks.TAG_ID, tile.writeNbt(new NbtCompound()));

            stackTag.put(ModBlocks.TAG_ID, tile.getNbtData());
            drop.setNbt(stackTag);
            //data get entity @s SelectedItem
        }

        dropStack(world,pos,drop);

        super.onBreak(world, pos, state, player);
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
        stateManager.add(CHARGED);
        stateManager.add(Properties.WATERLOGGED);
        //stateManager.add(VACUUM);
    }


    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        final StorageBlockEntity tile = (StorageBlockEntity) world.getBlockEntity(pos);
        if (tile == null) { return; }

        if (tile.liquidXp.amount != 0) {
            world.setBlockState(pos, state.with(CHARGED, true));
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }



    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {

            final StorageBlockEntity tile = (StorageBlockEntity) world.getBlockEntity(pos);

            if (tile == null) { return ActionResult.FAIL; }

            ItemStack mainHand = player.getStackInHand(Hand.MAIN_HAND);
            int itemCountInHand = player.getStackInHand(hand).getCount();
            boolean tileIsLocked = !tile.player_uuid.equals(Util.NIL_UUID);
            boolean isTileOwner = tile.player_uuid.equals(player.getUuid());
            boolean isSurvival = !player.isCreative() && !player.isSpectator();

            // Inspector
            if (mainHand.isOf(ModItems.INSPECTOR)) {
                return displayContainerInfo(world, pos, player, tile);
            }

            // Check if Player is Authorized
            if (tileIsLocked && !isTileOwner && !player.isCreative()) {
                return containerAccessDenied(world, pos, player);
            }


            // Key
            if (mainHand.isOf(ModItems.KEY) && (isTileOwner || player.isCreative())) {
                return unlockContainer(world, pos, player, hand, isSurvival, itemCountInHand, tile);
            }


            // Only Survival Actions
            if (isSurvival) {

                // Redstone Torch
                if (mainHand.isOf(Items.REDSTONE_TORCH)) {
                    return toggleVacuum(world, pos, tile);
                }

                // Lock
                if (mainHand.isOf(ModItems.LOCK)) {
                    if (tileIsLocked) { return containerIsAlreadyLocked(world, pos, player); }
                    return lockContainer(world, pos, player, hand, itemCountInHand, tile);
                }

                // XP Tool
                if (mainHand.isOf(ModItems.XP_REMOVER)) {
                    ItemStack stack = player.getStackInHand(hand);

                    if (stack.hasNbt() && Objects.requireNonNull(stack.getNbt()).getBoolean(Xp_removerItem.tagId)) {
                        return containerXpToPlayer(itemCountInHand, state, world, pos, player, tile);
                    }

                    return playerXpToContainer(itemCountInHand, state, world, pos, player, tile);
                }

                // GlassBottle
                if (mainHand.isOf(Items.GLASS_BOTTLE)) {
                    return fillGlassBottle(state, world, pos, player, hand, tile);
                }

                // EP Flask
                if (mainHand.isOf(Items.EXPERIENCE_BOTTLE)) {
                    return insertBottleXP(itemCountInHand, state, world, pos, player, hand, tile);
                }

                // Empty Bucket
                if (mainHand.isOf(Items.BUCKET)) {
                    return fillBucketOnContainer(state, world, pos, player, hand, tile);
                }

                // Experience Bucket
                if (mainHand.isOf(ModFluids.XP_BUCKET)) {
                    return emptyBucketOnContainer(state, world, pos, player, hand, tile);
                }

                //Mending Item Repair
                //if (mainHand.isOf(Items.BAMBOO)) {
                if (mainHand.isDamaged() && (EnchantmentHelper.getLevel(Enchantments.MENDING, mainHand) > 0)) {
                    return repairItem(world, pos, state, tile, mainHand);
                }

            }

        }

        return ActionResult.CONSUME;
    }

    private ActionResult repairItem(World world, BlockPos pos, BlockState state, StorageBlockEntity tile, ItemStack mainHand) {
        if (tile.getContainerExperience() > 0) {
            tile.isAuthPlayer = true;
            try (Transaction transaction = Transaction.openOuter()) {
                int xpCostToRepair = mainHand.getDamage() / 2;

                //Enough Xp to repair Item complete
                if (tile.getContainerExperience() >= xpCostToRepair) {
                    mainHand.setDamage(0);

                    //Not enough xp, calculate new Damage level
                } else {
                    int maxDamageWithXp = tile.getContainerExperience() * 2;
                    xpCostToRepair = tile.getContainerExperience();
                    mainHand.setDamage(mainHand.getDamage() - maxDamageWithXp);
                }

                tile.liquidXp.extract(FluidVariant.of(ModFluids.LIQUID_XP), xpCostToRepair * XpStorage.MB_PER_XP, transaction);

                world.playSound(null, pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 1f, 1f);
                world.setBlockState(pos, state.with(CHARGED, (tile.liquidXp.amount != 0)));

                transaction.commit();
            }
            tile.isAuthPlayer = false;
        }
        return ActionResult.SUCCESS;
    }


    private  ActionResult toggleVacuum(World world, BlockPos pos, StorageBlockEntity tile) {
        tile.toggleVacuum();
        //world.setBlockState(pos, state.with(VACUUM, tile.vacuum));

        world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1f, 1f);
        return ActionResult.SUCCESS;
    }

    private ActionResult containerIsAlreadyLocked(World world, BlockPos pos, PlayerEntity player) {
        player.sendMessage(new TranslatableText("text.storageBlock.isLocked"), true);

        world.playSound(null, pos, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, SoundCategory.BLOCKS, 1f, 1f);

        return ActionResult.SUCCESS;
    }

    private ActionResult containerAccessDenied(World world, BlockPos pos, PlayerEntity player) {
        player.sendMessage(new TranslatableText("text.storageBlock.denied"), true);

        world.playSound(null, pos, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, SoundCategory.BLOCKS, 1f, 1f);

        return ActionResult.SUCCESS;
    }

    private ActionResult emptyBucketOnContainer(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, StorageBlockEntity tile) {
        try (Transaction transaction = Transaction.openOuter()) {
            tile.liquidXp.insert(FluidVariant.of(ModFluids.LIQUID_XP), FluidConstants.BUCKET, transaction);
            player.getStackInHand(hand).setCount(0);
            player.getInventory().offerOrDrop(new ItemStack(Items.BUCKET));

            world.setBlockState(pos, state.with(CHARGED, (tile.liquidXp.amount != 0)));
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1f, 1f);
            transaction.commit();
        }

        return ActionResult.SUCCESS;
    }

    private ActionResult fillBucketOnContainer(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, StorageBlockEntity tile) {
        if (tile.liquidXp.amount >= FluidConstants.BUCKET) {
            tile.isAuthPlayer = true;
            try (Transaction transaction = Transaction.openOuter()) {
                tile.liquidXp.extract(FluidVariant.of(ModFluids.LIQUID_XP), FluidConstants.BUCKET, transaction);

                player.getStackInHand(hand).decrement(1);
                player.getInventory().offerOrDrop(new ItemStack(ModFluids.XP_BUCKET));

                world.setBlockState(pos, state.with(CHARGED, (tile.liquidXp.amount != 0)));
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1f, 1f);
                transaction.commit();
            }
            tile.isAuthPlayer = false;
        }

        return ActionResult.SUCCESS;
    }

    private ActionResult fillGlassBottle(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, StorageBlockEntity tile) {
        if (tile.liquidXp.amount >= FluidConstants.BOTTLE) {
            //11L * XpStorage.MB_PER_XP
            tile.isAuthPlayer = true;
            try (Transaction transaction = Transaction.openOuter()) {
                tile.liquidXp.extract(FluidVariant.of(ModFluids.LIQUID_XP), FluidConstants.BOTTLE, transaction);
                ItemStack fullBottle = new ItemStack(Items.EXPERIENCE_BOTTLE);
                fullBottle.setCount(1);

                world.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 0.125f, 1f);
                world.setBlockState(pos, state.with(CHARGED, (tile.liquidXp.amount != 0)));

                player.getStackInHand(hand).decrement(1);

                player.getInventory().offerOrDrop(fullBottle);
                transaction.commit();
            }
            tile.isAuthPlayer = false;
        }




        return ActionResult.SUCCESS;
    }


    private ActionResult insertBottleXP(int itemCount, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, StorageBlockEntity tile) {
        ItemStack emptyBottles = new ItemStack(Items.GLASS_BOTTLE);
        emptyBottles.setCount(itemCount);

        long xpToInsert = FluidConstants.BOTTLE * itemCount;
        /*int xpToInsert = 0;

        for (int i = 0; i < itemCount; i++) {
            // Minecraft EP Calculation
            //xpToInsert += 3 + world.random.nextInt(5) + world.random.nextInt(5);
        }*/

        try (Transaction transaction = Transaction.openOuter()) {
            tile.liquidXp.insert(FluidVariant.of(ModFluids.LIQUID_XP), xpToInsert, transaction);
            world.setBlockState(pos, state.with(CHARGED, (tile.liquidXp.amount != 0)));
            world.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1f, 1f);

            player.getStackInHand(hand).setCount(0);

            dropStack(world, pos, player.getHorizontalFacing().getOpposite(), emptyBottles);
            transaction.commit();
        }

        return ActionResult.SUCCESS;
    }

    private ActionResult containerXpToPlayer(int itemCount, BlockState state, World world, BlockPos pos, PlayerEntity player, StorageBlockEntity tile) {
        if (tile.liquidXp.amount > 0) {
            for (int i = 0; i < itemCount; i++) {
                tile.isAuthPlayer = true;
                int xpToNextLevel = XpFunctions.exp_to_reach_next_lvl(player.getNextLevelExperience(), player.experienceProgress);
                int storageXP = tile.getContainerExperience();

                if (storageXP == 0) {
                    break;
                }

                try (Transaction transaction = Transaction.openOuter()){
                    if (storageXP >= xpToNextLevel) {
                        tile.liquidXp.extract(FluidVariant.of(ModFluids.LIQUID_XP), xpToNextLevel * XpStorage.MB_PER_XP, transaction);
                        player.addExperience(xpToNextLevel);
                    } else {
                        tile.liquidXp.extract(FluidVariant.of(ModFluids.LIQUID_XP), tile.liquidXp.amount, transaction);
                        player.addExperience(storageXP);
                    }
                    transaction.commit();
                }
                tile.markDirty();
                tile.toUpdatePacket();
            }

            world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, 0.125f, 1f);
            world.setBlockState(pos, state.with(CHARGED, (tile.liquidXp.amount != 0)));
        }
        tile.isAuthPlayer = false;
        return ActionResult.SUCCESS;
    }

    private ActionResult playerXpToContainer(int itemCount, BlockState state, World world, BlockPos pos, PlayerEntity player, StorageBlockEntity tile) {
        if (XpFunctions.get_total_xp(player.experienceLevel, player.getNextLevelExperience(), player.experienceProgress) > 0) {

            int xpToInsert = 0;

            for (int i = 0; i < itemCount; i++) {
                int totalPlayerXp = XpFunctions.get_total_xp(player.experienceLevel, player.getNextLevelExperience(), player.experienceProgress);
                int xpExchange = XpFunctions.xp_value_from_bar(player.getNextLevelExperience(), player.experienceProgress);
                if (xpExchange == 0) {
                    xpExchange = XpFunctions.getToNextLowerExperienceLevel(player.experienceLevel);
                    player.experienceProgress = 0f;
                }

                if (totalPlayerXp >= xpExchange) {
                    player.addExperience(-xpExchange);
                    xpToInsert += xpExchange;
                } else {
                    player.addExperience(-totalPlayerXp);
                    xpToInsert += totalPlayerXp;
                }
            }

            try (Transaction transaction = Transaction.openOuter()) {
                tile.liquidXp.insert(FluidVariant.of(ModFluids.LIQUID_XP), xpToInsert * XpStorage.MB_PER_XP, transaction);
                transaction.commit();
            }

            world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 0.125f, 1f);
            world.setBlockState(pos, state.with(CHARGED, (tile.liquidXp.amount != 0)));
        }
        return ActionResult.SUCCESS;
    }


    private ActionResult unlockContainer(World world, BlockPos pos, PlayerEntity player, Hand hand, boolean isSurvival, int itemCountInHand, StorageBlockEntity tile) {
        tile.setUuidAndNameTo();

        player.sendMessage(new TranslatableText("text.storageBlock.isOpen"), true);
        world.playSound(null, pos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1f, 1f);

        if (isSurvival) {
            player.getStackInHand(hand).setCount(itemCountInHand > 1 ? itemCountInHand - 1 : 0);
        }
        return ActionResult.SUCCESS;
    }

    private ActionResult lockContainer(World world, BlockPos pos, PlayerEntity player, Hand hand, int itemCountInHand, StorageBlockEntity tile) {
        tile.setUuidAndNameTo(player.getUuid(), player.getName());

        player.sendMessage(new TranslatableText("text.storageBlock.locked"), true);
        world.playSound(null, pos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1f, 1f);

        player.getStackInHand(hand).setCount(itemCountInHand > 1 ? itemCountInHand - 1 : 0);

        return ActionResult.SUCCESS;
    }

    private ActionResult displayContainerInfo(World world, BlockPos pos, PlayerEntity player, StorageBlockEntity tile) {
        int totalXp = XpFunctions.get_total_xp(player.experienceLevel, player.getNextLevelExperience(), player.experienceProgress);
        UUID noUUid = Util.NIL_UUID;

        if (!tile.player_uuid.equals(noUUid)) {
            player.sendSystemMessage(new TranslatableText("item.tooltip.owner", tile.playerName.asString()), noUUid);
            player.sendSystemMessage(new LiteralText("UUid: " + tile.player_uuid), noUUid);
        } else {
            player.sendSystemMessage(new TranslatableText("item.debug_info.xp.container_no_owner"), noUUid);
        }
        String xp = String.format(Locale.GERMAN, "%,d", tile.getContainerExperience());
        String playerXp = String.format(Locale.GERMAN,"%,d", totalXp);

        player.sendSystemMessage(new TranslatableText("item.debug_info.xp.container_info", xp, Integer.MAX_VALUE), noUUid);
        player.sendSystemMessage(new TranslatableText("item.debug_info.xp.container_fill", tile.getContainerFillPercentage()), noUUid);
        player.sendSystemMessage(new TranslatableText("item.debug_info.xp.player_info", playerXp), noUUid);

        if (tile.vacuum) {
            player.sendSystemMessage(new TranslatableText("text.storageBlock.vacuum"), noUUid);
        }



        world.playSound(null, pos, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 0.5f, 1f);

        return ActionResult.SUCCESS;
    }


    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new StorageBlockEntity(pos, state);
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlocks.STORAGE_BLOCK_ENTITY, StorageBlockEntity::tick);
    }
}
